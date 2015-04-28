/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import ec.gov.informatica.firmadigital.xml.XmlSignature;

/**
 * Firma digital en formato XML Signature.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.1 $
 */
public class FirmaDigitalXML extends FirmaDigital {

	public FirmaDigitalXML(KeyStore keyStore) {
		super(keyStore);
	}

	public String firmar(String xml) {
		try {
			inicializar();
			XmlSignature xmlSignature = new XmlSignature();
			return xmlSignature.signXML(xml, (X509Certificate) certificateChain[0], privateKey);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e); // FIXME
		} catch (Exception e) {
			throw new RuntimeException(e); // FIXME
		}
	}
	
	public String verificar(String xml) {
		try {
			XmlSignature xmlSignature = new XmlSignature();
			return xmlSignature.verify(xml);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e); // FIXME
		} catch (Exception e) {
			throw new RuntimeException(e); // FIXME
		}
	}
}