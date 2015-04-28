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
 * Implementacion de KeyStoreProvider para acceder al keystore del sistema
 * operativo Microsoft Windows.
 * 
 * Funciona en JDK 5, accede a las librerias PKCS#11 del sistema operativo.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.1 $
 */
public class SafeNetWindowsKeyStoreProvider extends PKCS11KeyStoreProvider {

	private static final String config;

	static {
		StringBuffer sb = new StringBuffer();
		sb.append("name=Safenetikey2032\n");
		sb.append("library=C:\\WINDOWS\\SYSTEM32\\dkck201.dll\n");
		sb.append("disabledMechanisms={ CKM_SHA1_RSA_PKCS }");
		config = sb.toString();
	}

	@Override
	public String getConfig() {
		return config;
	}
}