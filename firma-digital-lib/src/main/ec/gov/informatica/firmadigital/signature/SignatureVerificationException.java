/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.signature;

/**
 * Excepcion lanzada si ocurre un error al verificar una firma.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.1 $
 */
public class SignatureVerificationException extends Exception {

	private static final long serialVersionUID = 8692706681299088789L;

	public SignatureVerificationException() {
	}

	public SignatureVerificationException(String message) {
		super(message);
	}

	public SignatureVerificationException(Throwable cause) {
		super(cause);
	}

	public SignatureVerificationException(String message, Throwable cause) {
		super(message, cause);
	}
}