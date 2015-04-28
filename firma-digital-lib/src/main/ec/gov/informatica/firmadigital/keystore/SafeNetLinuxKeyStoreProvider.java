/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.keystore;

/**
 * Implementacion de <code>KeyStoreProvider</code> para utilizar con librerias
 * PKCS#11 de SafeNet, instaladas previamente.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.2 $
 */
public class SafeNetLinuxKeyStoreProvider extends PKCS11KeyStoreProvider {

	private static final String CONFIG;

	static {
		StringBuffer config = new StringBuffer();
		config.append("name=Safenetikey2032\n");
		config.append("library=/usr/lib/libeTPkcs11.so\n");
		config.append("disabledMechanisms={ CKM_SHA1_RSA_PKCS }");
		CONFIG = config.toString();
	}

	@Override
	public String getConfig() {
		return CONFIG;
	}
}