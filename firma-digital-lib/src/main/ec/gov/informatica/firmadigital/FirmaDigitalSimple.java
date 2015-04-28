/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital;

import java.security.KeyStore;
import java.security.PublicKey;

import ec.gov.informatica.firmadigital.signature.PKCS1SignatureProcessor;

/**
 * Firma digital en formato PKCS#1.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.1 $
 */
public class FirmaDigitalSimple extends FirmaDigital {

	// Procesador de firma digital en formato PKCS#1
	private PKCS1SignatureProcessor signatureProcessor = new PKCS1SignatureProcessor();

	public FirmaDigitalSimple(KeyStore keyStore) {
		super(keyStore);
	}

	public byte[] firmar(byte[] data) {
		inicializar();
		return signatureProcessor.sign(data, privateKey);
	}

	public boolean verify(byte[] data, byte[] signature, PublicKey publicKey) {
		return signatureProcessor.verify(data, signature, publicKey);
	}
}