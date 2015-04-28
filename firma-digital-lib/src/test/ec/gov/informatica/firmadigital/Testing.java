package ec.gov.informatica.firmadigital;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.util.Store;

import ec.gov.informatica.firmadigital.util.Utils;

public class Testing {

	/**
	 * Return a boolean array representing keyUsage with digitalSignature set.
	 */
	static boolean[] getKeyUsageForSignature() {
		boolean[] val = new boolean[9];
		val[0] = true;
		return val;
	}

	/**
	 * Take a CMS Signed-Data message and a trust anchor and determine if the
	 * message is signed with a valid signature from a end entity entity
	 * certificate recognized by the trust anchor rootCert.
	 */
	public static boolean isValid(CMSSignedData signedData,
			X509Certificate rootCert) throws Exception {
//		CertStore certsAndCRLs = signedData.getCertificatesAndCRLs(
//				"Collection", "BC");
//		SignerInformationStore signers = signedData.getSignerInfos();
//		Iterator it = signers.getSigners().iterator();
//
//		if (it.hasNext()) {
//			SignerInformation signer = (SignerInformation) it.next();
//			
//			X509CertSelector signerConstraints = signer.getSID();
//			signerConstraints.setKeyUsage(getKeyUsageForSignature());
//
//			PKIXCertPathBuilderResult result = Utils.buildPath(rootCert, signer
//					.getSID(), certsAndCRLs);
//
//			return signer.verify(result.getPublicKey(), "BC");
//		}
		
		Store                   certStore = signedData.getCertificates();
		  SignerInformationStore  signers = signedData.getSignerInfos();
		  Collection              c = signers.getSigners();
		  Iterator                it = c.iterator();
		  
		  while (it.hasNext())
		  {
		      SignerInformation   signer = (SignerInformation)it.next();
		      Collection          certCollection = certStore.getMatches(signer.getSID());

		      Iterator              certIt = certCollection.iterator();
		      X509CertificateHolder cert = (X509CertificateHolder)certIt.next();
		  
		      return signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert));
		  }
		
		
		return false;
	}

	public static void main(String[] args) throws Exception {
		KeyStore credentials = Utils.createCredentials();
		PrivateKey key = (PrivateKey) credentials.getKey(
				Utils.END_ENTITY_ALIAS, Utils.KEY_PASSWD);
		Certificate[] chain = credentials
				.getCertificateChain(Utils.END_ENTITY_ALIAS);
		CertStore certsAndCRLs = CertStore.getInstance("Collection",
				new CollectionCertStoreParameters(Arrays.asList(chain)), "BC");
		X509Certificate cert = (X509Certificate) chain[0];

		// set up the generator
		CMSSignedDataGenerator gen = new CMSSignedDataGenerator();

		gen.addSigner(key, cert, CMSSignedDataGenerator.DIGEST_SHA1);
		gen.addCertificatesAndCRLs(certsAndCRLs);

		// create the signed-data object
		CMSProcessable data = new CMSProcessableByteArray("Hello World!"
				.getBytes());

		CMSSignedData signed = gen.generate(data, "BC");

		// re-create
		signed = new CMSSignedData(data, signed.getEncoded());

		// verification step
		X509Certificate rootCert = (X509Certificate) credentials
				.getCertificate(Utils.ROOT_ALIAS);

		if (isValid(signed, rootCert)) {
			System.out.println("verification succeeded");
		} else {
			System.out.println("verification failed");
		}
	}
}