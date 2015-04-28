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

import junit.framework.TestCase;

import org.junit.Test;

import ec.gov.informatica.firmadigital.keystore.KeyStoreProvider;
import ec.gov.informatica.firmadigital.keystore.MockKeyStoreProvider;
import ec.gov.informatica.firmadigital.util.Utils;

/**
 * Prueba de Firma Digital
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.1 $
 */
public class FirmaDigitalCMSTest extends TestCase {

	private static final char[] PASSWORD = "password".toCharArray();
	private static final byte[] DATA = Utils.generateRandomByteArray(238);

	@Test
	public void testFirmarCMS() throws Exception {
		KeyStoreProvider keyStoreProvider = new MockKeyStoreProvider();
		KeyStore keyStore = keyStoreProvider.getKeystore();

		// Firmar
		FirmaDigitalCMS firma = new FirmaDigitalCMS(keyStore);
		firma.setPrivateKeyPassword(PASSWORD);  
		byte[] signature = firma.firmar(DATA);

		// Verificar
		firma.verify(signature);
	}
}