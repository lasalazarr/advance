/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.keystore;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

/**
 * Implementacion de KeyStoreProvider para acceder al keystore del sistema
 * operativo Microsoft Windows.
 * 
 * Utiliza funcionalidad disponible desde el JDK6 en adelante para acceder al MS
 * CAPI.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.2 $
 */
public class WindowsJDKKeyStoreProvider extends KeyStoreProvider {

	public KeyStore getKeystore() throws KeyStoreException {
		try {
			KeyStore keyStore = KeyStore.getInstance("Windows-MY");
			keyStore.load(null, null);
			return keyStore;
		} catch (NoSuchAlgorithmException e) {
			throw new KeyStoreException(e);
		} catch (CertificateException e) {
			throw new KeyStoreException(e);
		} catch (IOException e) {
			throw new KeyStoreException(e);
		}
	}

	public KeyStore getKeystore(char[] password) throws KeyStoreException {
		return getKeystore();
	}
}