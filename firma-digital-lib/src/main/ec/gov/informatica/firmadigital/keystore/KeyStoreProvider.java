/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.keystore;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Iterator;

import ec.gov.informatica.firmadigital.cert.PrivateKeyAndCertificateChain;

/**
 * Obtiene un KeyStore.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.2 $
 */
public abstract class KeyStoreProvider implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7550113582760665695L;
	/**
	 * 
	 */
	private static final Long serial = -7550113582760665695L;
	private static Field aliasesField = null;
	private static Field privateKeyField = null;
	private static Field certificateChainField = null;

	/**
	 * Obtiene un KeyStore que no esta protegido por un password, o que puede
	 * obtener el password usando un mecanismo nativo (PKCS#11 en Windows).
	 * 
	 * @param password
	 * @return
	 * @throws KeyStoreException
	 */
	abstract public KeyStore getKeystore() throws KeyStoreException;

	/**
	 * Obtiene un KeyStore que no esta protegido por un password, o que puede
	 * obtener el password usando un mecanismo nativo (PKCS#11 en Windows).
	 * 
	 * @param password
	 * @return
	 * @throws KeyStoreException
	 */
	abstract public KeyStore getKeystore(char[] password)
			throws KeyStoreException;

	public PrivateKeyAndCertificateChain getPrivateKeyAndCertificateChain(
			KeyStore keyStore, Integer aliasIndex) throws KeyStoreException,
			NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		Object outcomePrev = null;
		if (aliasIndex != null) {
			synchronized (serial) {
				if (aliasesField == null) {
					aliasesField = keyStore.aliases().getClass()
							.getDeclaredField("val$iter");
					aliasesField.setAccessible(true);
				}
			}
			Iterator<?> spi = (Iterator<?>) aliasesField
					.get(keyStore.aliases());
			int i = 0;
			while (spi.hasNext()) {
				if (aliasIndex == i) {
					outcomePrev = spi.next();
					break;
				}
				spi.next();
				i++;
			}
		}
		return (outcomePrev == null) ? null
				: new PrivateKeyAndCertificateChain(
						getCertificateChain(outcomePrev), getKey(outcomePrev));
	}

	private static PrivateKey getKey(Object entry) throws KeyStoreException,
			NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		PrivateKey outcome = null;
		if (entry != null) {
			synchronized (serial) {
				if (privateKeyField == null) {
					privateKeyField = entry.getClass().getDeclaredField(
							"privateKey");
					privateKeyField.setAccessible(true);
				}
			}
			outcome = (PrivateKey) privateKeyField.get(entry);
		}
		return outcome;
	}

	private static Certificate[] getCertificateChain(Object entry)
			throws KeyStoreException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Certificate[] outcome = null;
		if (entry != null) {
			synchronized (serial) {
				if (certificateChainField == null) {
					certificateChainField = entry.getClass().getDeclaredField(
							"certChain");
					certificateChainField.setAccessible(true);
				}
			}
			outcome = (Certificate[]) certificateChainField.get(entry);
		}
		return outcome;
	}

}