/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import ec.gov.informatica.firmadigital.signature.PKCS1SignatureProcessor;

/**
 * Herramienta de linea de comando para firmar archivos con PKCS#1 en formato
 * Base64.
 * 
 * Utiliza un KeyStore almacenado en un archivo. Una implementacion que utilice
 * PKCS#11 seria posible modificando esta clase.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.1 $
 */
public class Firmador {

	public static void main(String[] args) throws Exception {
		if (args.length != 6) {
			System.out.println("Uso: Firmador -s|-v keystore storepass alias messagefile signaturefile");
			return;
		}

		String options = args[0];
		String keystorefile = args[1];
		char[] storepass = args[2].toCharArray();
		String alias = args[3];
		String messagefile = args[4];
		String signaturefile = args[5];

		Signature signature = Signature.getInstance("SHA1withRSA");

		// KeyStore almacenado en un archivo
		// TODO: Agregar la opcion de utilizar PKCS#11
		KeyStore keystore = KeyStore.getInstance("JKS");
		keystore.load(new FileInputStream(keystorefile), storepass);

		FileInputStream messageFileInputStream = new FileInputStream(messagefile);
		PKCS1SignatureProcessor processor = new PKCS1SignatureProcessor();

		PublicKey publicKey = keystore.getCertificate(alias).getPublicKey();
		PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, storepass);

		if (options.indexOf("s") != -1) {
			// Firmar
			byte[] raw = processor.sign(messageFileInputStream, privateKey);
			byte[] base64 = new BASE64Encoder().encode(raw).getBytes();
			

			FileOutputStream out = new FileOutputStream(signaturefile);
			out.write(base64);
			out.close();
		} else {
			// Verificar
			FileInputStream sigIn = new FileInputStream(signaturefile);
			byte[] base64 = new byte[sigIn.available()];
			sigIn.read(base64);
			sigIn.close();
			byte[] raw = new BASE64Decoder().decodeBuffer(new String(base64));
			boolean valid = processor.verify(messageFileInputStream, raw, publicKey);

			if (valid) {
				System.out.println("La firma es correcta.");
			} else {
				System.out.println("La firma es INVALIDA!");
			}
		}
	}
}