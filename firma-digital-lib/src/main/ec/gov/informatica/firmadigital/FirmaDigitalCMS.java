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

import org.bouncycastle.cms.CMSException;

import ec.gov.informatica.firmadigital.signature.CMSSignatureProcessor;
import ec.gov.informatica.firmadigital.signature.SignatureVerificationException;

/**
 * Firma digital en formato CMS (derivado de PKCS#7).
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.2 $
 */
public class FirmaDigitalCMS extends FirmaDigital {

	// Procesador de firma digital en formato PKCS#7
	private CMSSignatureProcessor signatureProcessor = new CMSSignatureProcessor();

	public FirmaDigitalCMS(KeyStore keyStore) {
		super(keyStore);
	}

	public byte[] firmar(byte[] data) throws CMSException {
		inicializar();
		return signatureProcessor.sign(data, privateKey, certificateChain);
	}

	public byte[] verify(byte[] signedBytes) throws SignatureVerificationException {
		return signatureProcessor.verify(signedBytes);
	}
}