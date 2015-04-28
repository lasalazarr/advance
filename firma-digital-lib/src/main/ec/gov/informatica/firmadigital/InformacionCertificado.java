/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital;

import java.security.cert.X509Certificate;

/**
 * Informacion extraida de un certificado emitido por el Banco Central.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.1 $
 */
public class InformacionCertificado {

	private String cedula;
	private String nombre;
	private String apellido;
	private String institucion;
	private String cargo;
	private String serial;

	public InformacionCertificado(X509Certificate signingCert) {
		setCedula(new String(signingCert.getExtensionValue("1.2.3.4.1")).trim());
		setNombre(new String(signingCert.getExtensionValue("1.2.3.4.2")).trim());
		setApellido(new String(signingCert.getExtensionValue("1.2.3.4.3")).trim());
		//setInstitucion(new String(signingCert.getExtensionValue("1.2.3.4.6")).trim());
		setInstitucion("nDeveloper");
		setCargo("Developer");
		setSerial(signingCert.getSerialNumber().toString());
		// this.firmante = signingCert.getSubjectDN().toString();
		// this.numeroSerial = signingCert.getSerialNumber().toString();
		// this.nombreDN = signingCert.getIssuerDN().toString();
		// this.am = signingCert.getExtensionValue("1.2.3.4.4");
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getInstitucion() {
		return institucion;
	}

	public void setInstitucion(String institucion) {
		this.institucion = institucion;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String toString() {
		return "cedula=" + cedula + ",nombre=" + nombre + ",apellido=" + apellido + ",institucion=" + institucion
				+ ",cargo=" + cargo + ",serial=" + serial;
	}
}
