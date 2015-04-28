/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.signature;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
/**
 * Procesador de firmas primitivo, tipo PKCS#1.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.1 $
 */
public class PKCS1SignatureProcessor     {

	private static final String DIGITAL_SIGNATURE_ALGORITHM_NAME = "SHA1withRSA";

	private static final Logger logger = Logger
			.getLogger(PKCS1SignatureProcessor.class.getName());

	public PKCS1SignatureProcessor() {
	}

	public byte[] sign(byte[] data, PrivateKey privateKey) {
		try {
			Signature signature = Signature
					.getInstance(DIGITAL_SIGNATURE_ALGORITHM_NAME);
			signature.initSign(privateKey);
			signature.update(data);
			return signature.sign();
		} catch (GeneralSecurityException e) {
			logger.log(Level.SEVERE, null, e);
			throw new RuntimeException(e); // TODO Fix
		}
	}

	public boolean verify(byte[] data, byte[] signature, PublicKey publicKey) {
		try {
			Signature sig = Signature
					.getInstance(DIGITAL_SIGNATURE_ALGORITHM_NAME);
			sig.initVerify(publicKey);
			sig.update(data);
			return sig.verify(signature);
		} catch (GeneralSecurityException e) {
			logger.log(Level.SEVERE, null, e);
			throw new RuntimeException(e); // FIXME
		}
	}

	public byte[] sign(InputStream data, PrivateKey privateKey) {
		try {
			Signature signature = Signature
					.getInstance(DIGITAL_SIGNATURE_ALGORITHM_NAME);
			signature.initSign(privateKey);

			byte[] buffer = new byte[8192];
			int length;

			while ((length = data.read(buffer)) != -1) {
				signature.update(buffer, 0, length);
			}

			data.close();
			return signature.sign();
		} catch (GeneralSecurityException e) {
			logger.log(Level.SEVERE, null, e);
			throw new RuntimeException(e); // TODO Fix
		} catch (IOException e) {
			logger.log(Level.SEVERE, null, e);
			throw new RuntimeException(e); // TODO Fix
		}
	}

	public boolean verify(InputStream data, byte[] signature,
			PublicKey publicKey) {
		try {
			Signature sig = Signature
					.getInstance(DIGITAL_SIGNATURE_ALGORITHM_NAME);
			sig.initVerify(publicKey);

			byte[] buffer = new byte[8192];
			int length;

			while ((length = data.read(buffer)) != -1) {
				sig.update(buffer, 0, length);
			}

			data.close();
			return sig.verify(signature);
		} catch (GeneralSecurityException e) {
			logger.log(Level.SEVERE, null, e);
			throw new RuntimeException(e); // FIXME
		} catch (IOException e) {
			logger.log(Level.SEVERE, null, e);
			throw new RuntimeException(e); // FIXME
		}
	}
}