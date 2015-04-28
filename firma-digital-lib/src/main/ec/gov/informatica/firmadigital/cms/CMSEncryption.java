/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.cms;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.KeyTransRecipientInformation;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509StoreCertCollection;
import org.bouncycastle.x509.X509CollectionStoreParameters;

/**
 * Clase para encriptar usando el algoritmo CMS. Se debe usar el certificado
 * digital del destinatario para encriptar. Se pueden tener multiples
 * destinatarios del mensaje encriptado.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.7 $
 */
public class CMSEncryption {

	/**
	 * Encriptar un mensaje a un destinatario usando su certificado digital.
	 * 
	 * @param cert
	 *            Certificado digital del destinatario
	 * @param message
	 *            Mensaje a encriptar
	 * @return Mensaje encriptado
	 */
	public static byte[] encrypt(X509Certificate cert, byte[] message) {
		return encrypt(Collections.singletonList(cert), message);
	}

	/**
	 * Encriptar un mensaje a multiples destinatarios.
	 * 
	 * @param certs
	 *            Listado de certificados digitales de los destinatarios
	 * @param message
	 *            Mensaje a encriptar
	 * @return Mensaje encriptado
	 */
	public static byte[] encrypt(List<X509Certificate> certs, byte[] message) {
		try {

			CMSEnvelopedDataGenerator gen = new CMSEnvelopedDataGenerator();

			if (certs.isEmpty()) {
				throw new IllegalStateException(
						"No se puede enviar un listado vacio de certificados!, No se encuentran parametrizados los certificados digitales de los usuarios en el servidor LDAP.");
			}

			for (X509Certificate cert : certs) {
				gen.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(
						cert).setProvider(BouncyCastleProvider.PROVIDER_NAME));
				// gen.addKeyTransRecipient(cert);
			}

			// 7CMSProcessable data = new CMSProcessableByteArray(message);
			CMSTypedData data = new CMSProcessableByteArray(message);
			// Encriptar!

			CMSEnvelopedData enveloped = gen.generate(data,
					new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC)
							.setProvider(BouncyCastleProvider.PROVIDER_NAME)
							.build());

			// CMSEnvelopedData enveloped = gen.generate(data,
			// CMSEnvelopedDataGenerator.AES128_CBC, "BC");

			// Retornar mensaje encriptado
			return enveloped.getEncoded();
		} catch (CMSException e) {
			throw new RuntimeException(e); // FIXME
		} catch (IOException e) {
			throw new RuntimeException(e); // FIXME
		} catch (CertificateEncodingException e) {
			throw new RuntimeException(e); // FIXME
		}
	}

	/**
	 * Permite averiguar si un mensaje encriptado está destinado al poseedor de
	 * un certificado.
	 * 
	 * @param encrypted
	 * @param certificate
	 * @return
	 * @throws Exception
	 */
	public boolean verifyRecipient(byte[] encrypted, X509Certificate certificate)
			throws Exception {
		// Listado de destinatarios
		List<RecipientId> recipientsToReturn = new ArrayList<RecipientId>();

		// Mensaje encriptado
		CMSEnvelopedData enveloped = new CMSEnvelopedData(encrypted);

		// Iterar en los destinatarios
		RecipientInformationStore recipients = enveloped.getRecipientInfos();
		Iterator it = recipients.getRecipients().iterator();

		RecipientInformation recipient = null;

		while (it.hasNext()) {
			recipient = (RecipientInformation) it.next();
			if (recipient instanceof KeyTransRecipientInformation) {
				RecipientId rid = recipient.getRID();

				// Verificar si el mensaje esta encriptado para este certificado
				if (rid.match(certificate)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Desencriptar mensaje
	 * 
	 * @param encrypted
	 *            Mensaje encriptado
	 * @param cert
	 *            Certificado del destinatario
	 * @param privateKey
	 *            Llave privada del destinatario
	 * @param provider
	 * @return Mensaje original
	 */
	public static byte[] decrypt(byte[] encrypted, X509Certificate cert,
			PrivateKey privateKey, Provider provider) {
		try {
			CMSEnvelopedData enveloped = new CMSEnvelopedData(encrypted);

			RecipientInformationStore recipients = enveloped
					.getRecipientInfos();
			// CertStore certStore = CertStore.getInstance("Collection", new
			// CollectionCertStoreParameters(Collections.singleton(cert)),
			// "BC");
			X509CollectionStoreParameters s = new X509CollectionStoreParameters(
					Collections.singleton(new JcaX509CertificateHolder(cert)));

			X509StoreCertCollection s1 = new X509StoreCertCollection();
			s1.engineInit(s);

			Iterator it = recipients.getRecipients().iterator();

			RecipientInformation recipient = null;

			while (it.hasNext()) {
				recipient = (RecipientInformation) it.next();

				if (recipient instanceof KeyTransRecipientInformation) {
					// match the recipient ID
					// Collection matches =
					// certStore.getCertificates(recipient.getRID());
					Collection matches = s1
							.engineGetMatches(recipient.getRID());

					if (!matches.isEmpty()) {
						// decrypt the data
						// return recipient.getContent(privateKey, "BC");

						// PrivateKey pk = getPrivateKey();
						JceKeyTransEnvelopedRecipient ter = null;

						if ("sun.security.mscapi.RSAPrivateKey"
								.equals(privateKey.getClass()
										.getCanonicalName())) {
							ter = new JceKeyTransEnvelopedRecipient(privateKey);
							ter.setProvider(provider);
							ter.setContentProvider(BouncyCastleProvider.PROVIDER_NAME);
						} else {
							ter = new JceKeyTransEnvelopedRecipient(privateKey);
							ter.setProvider(BouncyCastleProvider.PROVIDER_NAME);
						}

						return recipient.getContent(ter);
					}
				} else {
					throw new RuntimeException(
							"Wrong type of RecipientInformation: "
									+ recipient.getClass()); // FIXME
				}
				recipient = null;
			}

			if (recipient == null) {
				throw new RuntimeException(
						"Could not find a matching recipient"); // FIXME
			}

			throw new RuntimeException("no deberia llegar aqui"); // FIXME
		} catch (CMSException e) {
			throw new RuntimeException(e); // FIXME
		} catch (CertificateEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}