/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.cert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;

/**
 * Servicio de certificados provistos por el Banco Central del Ecuador.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.4 $
 */
public class ServicioCertificadoBancoCentral {

	/** Servidor LDAP publico del Banco Central */
	private static final String LDAP_BCE = "ldap://ldap.bce.ec:389";

	/**
	 * URL de la Certificate Revocation List provista por el Banco Central del
	 * Ecuador
	 */
	//private static final String CRL_URL = "http://www.eci.bce.ec/CRL/eci_bce_ec_crlfile.crl";
	private static final String CRL_URL = "http://10.128.39.51:8080/ejbca/publicweb/webdist/certdist?cmd=crl&issuer=CN=Ca%20Subordina%20Fuerza%20Naval,O=Fuerzas%20Armadas,C=EC";

	private static final Logger logger = Logger.getLogger(ServicioCertificadoBancoCentral.class.getName());

	/**
	 * Busca un certificado en el LDAP del Banco Central.
	 * 
	 * @param numeroRuc
	 *            numero de RUC (opcional)
	 * @param cedula
	 *            numero de cedula (opcional)
	 * @return
	 */
	public X509Certificate obtenerCertificado(String mail) {
		// Establecer el entorno para crear el InitialContext
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, LDAP_BCE);
		env.put(Context.SECURITY_AUTHENTICATION, "none");

		try {
			DirContext ctx = new InitialDirContext(env);
			Attributes matchAttrs = new BasicAttributes(true);

			// Buscar por email
			if (mail != null) {
				matchAttrs.put(new BasicAttribute("mail", mail));
			} else {
				throw new IllegalArgumentException();
			}

			// Buscar objetos que tengan los atributos
			NamingEnumeration<SearchResult> answer = ctx.search("OU=eci,O=bce,C=ec", matchAttrs);

			if ((answer != null) && (answer.hasMoreElements())) {
				Attributes attribs = ((SearchResult) answer.next()).getAttributes();
				Attribute attribCertificate = attribs.get("userCertificate;binary");

				if (attribCertificate != null) {
					byte[] certificate = (byte[]) attribCertificate.get();
					InputStream is = new ByteArrayInputStream(certificate);
					CertificateFactory cf = CertificateFactory.getInstance("X.509");
					return (X509Certificate) cf.generateCertificate(is);
				}
			}

			return null;
		} catch (NamingException e) {
			// FIXME
			throw new RuntimeException(e);
		} catch (CertificateException e) {
			// FIXME
			throw new RuntimeException(e);
		}
	}

	/**
	 * Averigua si un certificado esta revocado, segun la CRL.
	 * 
	 * @param certificate
	 * @return
	 * @throws ErrorVerificacionRevocacion
	 */
	public boolean estaRevocado(Certificate certificate) throws ErrorVerificacionRevocacion {
		InputStream inStream = null;
		try {
			URL url = new URL(CRL_URL);
			System.out.println("URL --> "+ url);
			inStream = url.openStream();
			System.out.println("inStream --> "+ inStream);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			CRL crl = cf.generateCRL(inStream);
			System.out.println("CRL --> "+ crl);
			
			boolean estaRevocado = crl.isRevoked(certificate);
			System.out.println("¿Esta Revocado? --> " + estaRevocado);
			return crl.isRevoked(certificate);
		} catch (MalformedURLException e) {
			throw new ErrorVerificacionRevocacion(e);
		} catch (IOException e) {
			throw new ErrorVerificacionRevocacion(e);
		} catch (CertificateException e) {
			throw new ErrorVerificacionRevocacion(e);
		} catch (CRLException e) {
			throw new ErrorVerificacionRevocacion(e);
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					logger.log(Level.WARNING, null, e);
				}
			}
		}
	}
	
	/**
	 * Averigua si un certificado esta revocado, segun la CRL.
	 * 
	 * @param certificate
	 * @return
	 * @throws ErrorVerificacionRevocacion
	 */
	public boolean estaRevocado(Certificate[] certificates) throws ErrorVerificacionRevocacion {
		InputStream inStream = null;
		try {
			System.out.println("CRL_URL --> " + CRL_URL);
			URL url = new URL(CRL_URL);
			System.out.println("URL --> "+ url);
			inStream = url.openStream();
			//System.out.println("inStream --> "+ inStream);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			CRL crl = cf.generateCRL(inStream);
			//System.out.println("CRL --> "+ crl);
			if((certificates==null)||(certificates.length==0)){
				return true;
			}else{
				for(Certificate certificate:certificates){
					boolean estaRevocado = crl.isRevoked(certificate);
					System.out.println("¿Esta Revocado? --> " + estaRevocado);
					if(crl.isRevoked(certificate)){
						return true;
					}
				}
			}
			return false;
		} catch (MalformedURLException e) {
			throw new ErrorVerificacionRevocacion(e);
		} catch (IOException e) {
			throw new ErrorVerificacionRevocacion(e);
		} catch (CertificateException e) {
			throw new ErrorVerificacionRevocacion(e);
		} catch (CRLException e) {
			throw new ErrorVerificacionRevocacion(e);
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					logger.log(Level.WARNING, null, e);
				}
			}
		}
	}
}
