/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.encryption;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ec.gov.informatica.firmadigital.FirmaDigital;
import ec.gov.informatica.firmadigital.cms.CMSEncryption;
import ec.gov.informatica.firmadigital.keystore.KeyStoreProvider;
import ec.gov.informatica.firmadigital.keystore.MockKeyStoreProvider;

/**
 * Pruebas de encripcion usando CMS
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.2 $
 */
public class CMSEncryptionTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testEncryption() throws Exception {
		char[] password = "password".toCharArray();

		KeyStoreProvider ksp1 = new MockKeyStoreProvider();
		KeyStore keyStore1 = ksp1.getKeystore();
		KeyStoreProvider ksp2 = new MockKeyStoreProvider();
		KeyStore keyStore2 = ksp2.getKeystore();
		KeyStoreProvider ksp3 = new MockKeyStoreProvider();
		KeyStore keyStore3 = ksp3.getKeystore();

		String alias1 = FirmaDigital.getSigningAlias(keyStore1, password);
		PrivateKey privateKey1 = (PrivateKey) keyStore1.getKey(alias1, password);
		X509Certificate cert1 = (X509Certificate) keyStore1.getCertificateChain(alias1)[0];

		String alias2 = FirmaDigital.getSigningAlias(keyStore2, password);
		PrivateKey privateKey2 = (PrivateKey) keyStore2.getKey(alias2, password);
		X509Certificate cert2 = (X509Certificate) keyStore2.getCertificateChain(alias2)[0];

		String alias3 = FirmaDigital.getSigningAlias(keyStore3, password);
		PrivateKey privateKey3 = (PrivateKey) keyStore3.getKey(alias3, password);
		X509Certificate cert3 = (X509Certificate) keyStore3.getCertificateChain(alias3)[0];

		byte[] data = "Encrypt me please!".getBytes();

		CMSEncryption cmsEncryption = new CMSEncryption();
		List<X509Certificate> certs = new ArrayList<X509Certificate>();
		certs.add(cert1);
		certs.add(cert2);

		byte[] encrypted = cmsEncryption.encrypt(certs, data);

		byte[] data1 = cmsEncryption.decrypt(encrypted, cert1, privateKey1, ksp1.getKeystore().getProvider());
		byte[] data2 = cmsEncryption.decrypt(encrypted, cert2, privateKey2, ksp2.getKeystore().getProvider());

		try {
			byte[] data3 = cmsEncryption.decrypt(encrypted, cert3, privateKey3, ksp3.getKeystore().getProvider());
			exception.expect(RuntimeException.class);
		} catch (Exception ignore) {
		}

		assertArrayEquals(data, data1);
		assertArrayEquals(data, data2);

		assertTrue(cmsEncryption.verifyRecipient(encrypted, cert1));
		assertFalse(cmsEncryption.verifyRecipient(encrypted, cert3));
	}
}