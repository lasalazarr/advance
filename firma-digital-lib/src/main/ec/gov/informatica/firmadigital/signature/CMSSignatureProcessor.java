/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.signature;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.Time;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;
import org.bouncycastle.x509.NoSuchStoreException;
import org.bouncycastle.x509.X509Store;

import ec.gov.informatica.firmadigital.util.BouncyCastleUtils;
import ec.gov.informatica.firmadigital.util.CertificadoBancoCentral;

/**
 * Procesador de firmas tipo Cryptographic Message Syntax (CMS).
 * 
 * El estandar CMS esta descrito en el RFC 3852:
 * http://www.ietf.org/rfc/rfc3852.txt
 * 
 * Implementacion de SignatureProcessor que utliza BouncyCastle para implementar
 * firma en formato CMS.
 * 
 * TODO: Agregar opcion de firma detached: generator.generate(content, false,
 * "BC");
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.6 $
 */
public class CMSSignatureProcessor {

	// Certificado del Banco Central
	private static X509Certificate eci;
	
	private static final Logger log = Logger.getLogger(CMSSignatureProcessor.class.getName());

	static {
		BouncyCastleUtils.initializeBouncyCastle();
		eci = new CertificadoBancoCentral();
	}
	
	public CMSSignatureProcessor() {
		super();
	}

	public static byte[] sign(byte[] data, PrivateKey privateKey, Certificate[] chain) throws CMSException {
		try {
			CMSSignedDataGenerator generator = createCMSSignedDataGenerator(privateKey, chain);
			CMSProcessable content = new CMSProcessableByteArray(data);
			CMSSignedData signedData = generator.generate(content, true, ("sun.security.mscapi.RSAPrivateKey".equals(privateKey.getClass().getName()))?"SunMSCAPI":"BC");
			return signedData.getEncoded();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e); // FIXME
		} catch (NoSuchProviderException e) {
			throw new RuntimeException(e); // FIXME
		} catch (IOException e) {
			throw new RuntimeException(e); // FIXME
		}
	}

	/**
	 * @param signedBytes
	 * @return
	 * @throws SignatureVerificationException
	 */
	public byte[] verify(byte[] signedBytes) throws SignatureVerificationException {
		try {
			CMSSignedData signedData = new CMSSignedData(signedBytes);
			//CertStore certs = signedData.getCertificatesAndCRLs("Collection", "BC");
			
			Store certs = signedData.getCertificates();

			SignerInformationStore signersInformationStore = signedData.getSignerInfos();
			Collection signers = signersInformationStore.getSigners();
			Iterator it = signers.iterator();

			while (it.hasNext()) {
				SignerInformation signer = (SignerInformation) it.next();
				Collection certCollection;
//				try {
					certCollection = certs.getMatches(signer.getSID());
//				} catch (CertStoreException e) {
//					throw new SignatureVerificationException("Problema al obtener los certificados", e);
//				}

				Iterator certIt = certCollection.iterator();
				//X509Certificate cert = (X509Certificate) certIt.next();ç
				
				
				 X509CertificateHolder cert = (X509CertificateHolder)certIt.next();
				/*
				 * try { // Verificar si el certificado esta firmado por el
				 * Banco // Central cert.verify(eci.getPublicKey()); } catch
				 * (CertificateException e) { throw new
				 * SignatureVerificationException
				 * ("El certificado esta mal codificado", e); } catch
				 * (SignatureException e) { throw new
				 * SignatureVerificationException("Error en la firma", e); }
				 */

				// Verificar la firma
				try {
//					if (!signer.verify(cert, "BC")) {
//						throw new SignatureVerificationException("La firma no verifico con " + signer.getSID());
//					}
					
					if (!signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert))) {
						throw new SignatureVerificationException("La firma no verifico con " + signer.getSID());
				    }  
				} catch (CertificateExpiredException e) {
					throw new SignatureVerificationException("El certificado estaba expirado al momento de la firma", e);
				} catch (CertificateNotYetValidException e) {
					throw new SignatureVerificationException("El certificado no era valido todavia al momento de la firma", e);
				} catch (OperatorCreationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CertificateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			CMSProcessable signedContent = signedData.getSignedContent();
			return (byte[]) signedContent.getContent();

		} /*catch (NoSuchAlgorithmException e) {
			throw new SignatureVerificationException("Algoritmo incorrecto", e);
		} catch (NoSuchProviderException e) {
			throw new SignatureVerificationException("Provider incorrecto", e);
		}*/ 
		catch (CMSException e) {
			throw new SignatureVerificationException("Excepcion CMS", e);
		}
		/*
		 * catch (InvalidKeyException e) { // Esto es nuevo! throw new
		 * SignatureVerificationException("Excepcion CMS", e); }
		 */
	}

	public byte[] addSignature(byte[] signedBytes, PrivateKey privateKey, Certificate[] chain) {
		X509Certificate cert = (X509Certificate) chain[0];

		try {
			CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
			generator.addSigner(privateKey, cert, CMSSignedDataGenerator.DIGEST_SHA1);

			CertStore certs = CertStore.getInstance("Collection", new CollectionCertStoreParameters(Arrays.asList(chain)));

			CMSSignedData signedData = new CMSSignedData(signedBytes);
			SignerInformationStore signers = signedData.getSignerInfos();
			CertStore existingCerts = signedData.getCertificatesAndCRLs("Collection", ("sun.security.mscapi.RSAPrivateKey".equals(privateKey.getClass().getName()))?"SunMSCAPI":"BC");
			X509Store x509Store = signedData.getAttributeCertificates("Collection", ("sun.security.mscapi.RSAPrivateKey".equals(privateKey.getClass().getName()))?"SunMSCAPI":"BC");

			// add new certs
			generator.addCertificatesAndCRLs(certs);
			// add existing certs
			generator.addCertificatesAndCRLs(existingCerts);
			// add existing certs attributes
			generator.addAttributeCertificates(x509Store);
			// add existing signers
			generator.addSigners(signers);

			CMSProcessable content = signedData.getSignedContent();
			signedData = generator.generate(content, true, ("sun.security.mscapi.RSAPrivateKey".equals(privateKey.getClass().getName()))?"SunMSCAPI":"BC");
			return signedData.getEncoded();
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		} catch (CMSException e) {
			throw new RuntimeException(e);
		} catch (NoSuchStoreException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static CMSSignedDataGenerator createCMSSignedDataGenerator(PrivateKey privateKey, Certificate[] chain) throws NoSuchAlgorithmException, NoSuchProviderException, CMSException {
		try {
			CertStore certsAndCRLS = CertStore.getInstance("Collection", new CollectionCertStoreParameters(Arrays.asList(chain)), "BC");

			X509Certificate cert = (X509Certificate) chain[0];

			// Agregar la fecha y hora de la firma
			AttributeTable attributeTable = getSigningDate();

			CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
			if(privateKey==null){
				new CMSException("No se encuentra la llave privada del token");
			}
			if(cert==null){
				new CMSException("No se encuentra la certificado digital en el token");
			}
			if(attributeTable==null){
				log.info("attributeTable is null");
			}
			if(chain.length<=0){
				log.info("chain has no elements");
			}
			generator.addSigner(privateKey, cert, CMSSignedDataGenerator.DIGEST_SHA1, attributeTable, null);
			generator.addCertificatesAndCRLs(certsAndCRLS);
			return generator;
		} catch (InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e); // FIXME
		} catch (CertStoreException e) {
			throw new RuntimeException(e); // FIXME
		}
	}

	/**
	 * Agregar la fecha y hora de la firma
	 * 
	 * @return
	 */
	private static AttributeTable getSigningDate() {
		Time time = new Time(new Date());
		Attribute attribute = new Attribute(CMSAttributes.signingTime, new DERSet(time.toASN1Primitive()));
		Hashtable<DERObjectIdentifier, Attribute> hashTable = new Hashtable<DERObjectIdentifier, Attribute>();
		hashTable.put(CMSAttributes.signingTime, attribute);
		return new AttributeTable(hashTable);
	}
}