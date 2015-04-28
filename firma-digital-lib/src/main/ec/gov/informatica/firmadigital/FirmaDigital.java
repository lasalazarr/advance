/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import ec.gov.informatica.firmadigital.cert.RevocationException;
import ec.gov.informatica.firmadigital.cert.ServicioCertificadoBancoCentral;
import ec.gov.informatica.firmadigital.util.Utils;

/**
 * Permite firmar y verificar digitalmente el contenido de archivos.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.1 $
 */
public abstract class FirmaDigital {

	/**
	 * KeyStore para acceder a la llave privada y el certificado,
	 * independientemente de como fueron almacenados.
	 */
	protected KeyStore keyStore;

	/** Password opcional para la llave privada */
	protected char[] privateKeyPassword = null;

	protected PrivateKey privateKey;

	protected Certificate[] certificateChain;

	/**
	 * 
	 * @param keyStore
	 */
	public FirmaDigital(KeyStore keyStore) {
		if (keyStore == null) {
			throw new IllegalStateException("Se necesita un KeyStore para poder firmar");
		}

		this.keyStore = keyStore;
	}

	/**
	 * Metodo opcional en caso de que la llave privada dentro del KeyStore este
	 * protegida por un password.
	 * 
	 * @param privateKeyPassword
	 */
	public void setPrivateKeyPassword(char[] privateKeyPassword) {
		this.privateKeyPassword = privateKeyPassword;
	}

	public void verificarRevocado(Certificate cert) throws RevocationException {
		ServicioCertificadoBancoCentral bce = new ServicioCertificadoBancoCentral();
		if (bce.estaRevocado(cert)) {
			throw new RevocationException();
		}
	}

	public void inicializar() {
		try {
			if (privateKey == null || certificateChain == null) {
				String alias = getSigningAlias(keyStore, privateKeyPassword);
				this.privateKey = (PrivateKey) keyStore.getKey(alias, privateKeyPassword);
				this.certificateChain = keyStore.getCertificateChain(alias);
			}
		} catch (UnrecoverableKeyException e) {
			throw new RuntimeException(e); // FIXME
		} catch (KeyStoreException e) {
			throw new RuntimeException(e); // FIXME
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e); // FIXME
		}
	}

	public static String getSigningAlias(KeyStore keyStore, char[] privateKeyPassword) {
		try {
			Enumeration<String> aliases = keyStore.aliases();

			while (aliases.hasMoreElements()) {
				String alias = aliases.nextElement();
				Key key = keyStore.getKey(alias, privateKeyPassword);

				if (key instanceof PrivateKey) {
					Certificate[] certs = keyStore.getCertificateChain(alias);
					if (certs.length >= 1) {
						Certificate cert = certs[0];
						if (cert instanceof X509Certificate) {
							X509Certificate signerCertificate = (X509Certificate) cert;
							boolean[] keyUsage = signerCertificate.getKeyUsage();
							// Digital Signature Key Usage:
							if (keyUsage[0]) {
								return alias;
							}
						}
					}
				}
			}

			throw new RuntimeException("No hay llave privada para firmar!");
		} catch (KeyStoreException e) {
			throw new RuntimeException(e); // FIXME
		} catch (UnrecoverableKeyException e) {
			throw new RuntimeException(e); // FIXME
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e); // FIXME
		}
	}
}