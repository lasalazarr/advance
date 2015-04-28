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
 * PKCS#11 de Alladin, instaladas previamente.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.1 $
 */
public class AlladinKeyStoreProvider extends PKCS11KeyStoreProvider {

	private static final String config;

	static {
		StringBuffer sb = new StringBuffer();
		sb.append("name=Aladdin-eToken\n");
		sb.append("library=C:\\WINDOWS\\SYSTEM32\\eTPKCS11.dll\n");
		config = sb.toString();
	}

	@Override
	public String getConfig() {
		return config;
	}
}