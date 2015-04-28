/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.signature;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import sun.security.pkcs.ContentInfo;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.PKCS9Attribute;
import sun.security.pkcs.PKCS9Attributes;
import sun.security.pkcs.ParsingException;
import sun.security.pkcs.SignerInfo;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X500Name;

/**
 * Implementacion de CMSSignatureProcessor utilizando clases no soportadas del
 * paquete sun.security.pkcs.
 * 
 * Esta implementacion se debe considerar solamente de prueba, no se deberia
 * utilizar.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.1 $
 */
public class SunPKCS7SignatureProcessor {

	private static final String DIGEST_ALGORITHM = "SHA1";
	private static final String SIGNING_ALGORITHM = "SHA1withRSA";

	public byte[] sign(byte[] dataIN, PrivateKey privateKey, Certificate[] chain) {
		try {
			X509Certificate cert = (X509Certificate) chain[0];

			// Create the PKCS7 Blob here
			AlgorithmId[] digestAlgorithmIds = { AlgorithmId.get(DIGEST_ALGORITHM) };

			// calculate message digest
			MessageDigest md = MessageDigest.getInstance(DIGEST_ALGORITHM);
			md.update(dataIN);
			byte[] digestedContent = md.digest();

			// construct authenticated attributes...
			PKCS9Attribute[] authenticatedAttributeList = {
					new PKCS9Attribute(PKCS9Attribute.CONTENT_TYPE_OID, ContentInfo.DATA_OID),
					new PKCS9Attribute(PKCS9Attribute.SIGNING_TIME_OID, new java.util.Date()),
					new PKCS9Attribute(PKCS9Attribute.MESSAGE_DIGEST_OID, digestedContent) };

			PKCS9Attributes authenticatedAttributes = new PKCS9Attributes(authenticatedAttributeList);

			// digitally sign the DER encoding of the authenticated attributes
			// with Private Key
			Signature signer = Signature.getInstance(SIGNING_ALGORITHM);
			signer.initSign(privateKey);
			signer.update(authenticatedAttributes.getDerEncoding());
			byte[] signedAttributes = signer.sign();

			ContentInfo contentInfo = new ContentInfo(ContentInfo.DATA_OID, new DerValue(DerValue.tag_OctetString,
					dataIN));

			X509Certificate[] certificates = { cert };

			// for compatibility with 1.5.x SignerInfo
			BigInteger serial = cert.getSerialNumber();

			SignerInfo si = new SignerInfo(new X500Name(cert.getIssuerDN().getName()), // X500Name
					// issuerName,
					serial, // x509.getSerialNumber(), // BigInteger serial,
					AlgorithmId.get(DIGEST_ALGORITHM), // AlgorithmId
					// digestAlgorithmId,
					authenticatedAttributes,// PKCS9Attributes
					// authenticatedAttributes,
					new AlgorithmId(AlgorithmId.RSAEncryption_oid), // AlgorithmId
					// digestEncryptionAlgorithmId,
					signedAttributes, // byte[] encryptedDigest,
					null); // PKCS9Attributes unauthenticatedAttributes) {

			SignerInfo[] signerInfos = { si };

			PKCS7 p7 = new PKCS7(digestAlgorithmIds, contentInfo, certificates, signerInfos);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			p7.encodeSignedData(out);

			return out.toByteArray();
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] verify(byte[] signedBytes) throws SignatureVerificationException {
		try {
			// Parse the PKCS7 input file...
			PKCS7 p7 = new PKCS7(signedBytes);

			// Verify!
			SignerInfo[] si = p7.verify();

			// Check the results of the verification
			if (si == null) {
				throw new SignatureVerificationException("Signature failed verification, data has been tampered");
			}

			return p7.getContentInfo().getContentBytes();
		} catch (ParsingException e) {
			throw new RuntimeException(e);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] addSignature(byte[] signedBytes, PrivateKey privateKey, Certificate[] chain) {
		throw new NotImplementedException();
	}
}