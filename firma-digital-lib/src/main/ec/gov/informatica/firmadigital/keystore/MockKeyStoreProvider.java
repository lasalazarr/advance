/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.keystore;

import java.security.KeyStore;
import java.security.KeyStoreException;

import ec.gov.informatica.firmadigital.util.Utils;

/**
 * Implementacion de KeyStoreProvider para pruebas.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.2 $
 */
public class MockKeyStoreProvider extends KeyStoreProvider {

	public KeyStore getKeystore() throws KeyStoreException {
		try {
			return Utils.createCredentials();
		} catch (Exception e) {
			throw new KeyStoreException(e);
		}
	}

	@Override
	public KeyStore getKeystore(char[] password) throws KeyStoreException {
		return getKeystore();
	}
}