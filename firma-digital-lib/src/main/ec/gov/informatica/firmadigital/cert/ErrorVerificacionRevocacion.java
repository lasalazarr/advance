/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.cert;

/**
 * Excepcion lanzada al presentarse un error en la verificacion
 * de certificados contra el Banco Central del Ecuador.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.1 $
 */
public class ErrorVerificacionRevocacion extends RuntimeException {

	private static final long serialVersionUID = -8947074968115564695L;

	public ErrorVerificacionRevocacion() {
	}

	public ErrorVerificacionRevocacion(String message) {
		super(message);
	}

	public ErrorVerificacionRevocacion(Throwable cause) {
		super(cause);
	}

	public ErrorVerificacionRevocacion(String message, Throwable cause) {
		super(message, cause);
	}
}