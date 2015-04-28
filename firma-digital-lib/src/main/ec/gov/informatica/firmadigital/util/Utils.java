package ec.gov.informatica.firmadigital.util;

import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;

import javax.security.auth.x500.X500Principal;
import javax.security.auth.x500.X500PrivateCredential;

import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;

public class Utils {

	private static final int VALIDITY_PERIOD = 7 * 24 * 60 * 60 * 1000; // one
	// week

	public static String ROOT_ALIAS = "root";
	public static String INTERMEDIATE_ALIAS = "intermediate";
	public static String END_ENTITY_ALIAS = "end";

	public static char[] KEY_PASSWD = "password".toCharArray();

	/**
	 * Create a random 1024 bit RSA key pair
	 */
	public static KeyPair generateRSAKeyPair() throws Exception {
		KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
		kpGen.initialize(1024, new SecureRandom());
		return kpGen.generateKeyPair();
	}

	/**
	 * Generate a sample V1 certificate to use as a CA root certificate
	 */
	public static X509Certificate generateRootCert(KeyPair pair) throws Exception {
		X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();

		// certGen.setSerialNumber(BigInteger.valueOf(1));
		certGen.setSerialNumber(new BigInteger(10, new SecureRandom()));

		certGen.setIssuerDN(new X500Principal("CN=Test CA Certificate"));
		certGen.setNotBefore(new Date(System.currentTimeMillis()));
		certGen.setNotAfter(new Date(System.currentTimeMillis() + VALIDITY_PERIOD));
		certGen.setSubjectDN(new X500Principal("CN=Test CA Certificate"));

		certGen.setPublicKey(pair.getPublic());
		certGen.setSignatureAlgorithm("SHA1WithRSAEncryption");

		return certGen.generate(pair.getPrivate(), "BC");
	}

	/**
	 * Generate a sample V3 certificate to use as an intermediate CA certificate
	 */
	public static X509Certificate generateIntermediateCert(PublicKey intKey, PrivateKey caKey, X509Certificate caCert) throws Exception {
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

		// certGen.setSerialNumber(BigInteger.valueOf(1));
		certGen.setSerialNumber(new BigInteger(10, new SecureRandom()));
		certGen.setIssuerDN(caCert.getSubjectX500Principal());
		certGen.setNotBefore(new Date(System.currentTimeMillis()));
		certGen.setNotAfter(new Date(System.currentTimeMillis() + VALIDITY_PERIOD));
		certGen.setSubjectDN(new X500Principal("CN=Test Intermediate Certificate"));
		certGen.setPublicKey(intKey);
		certGen.setSignatureAlgorithm("SHA1WithRSAEncryption");

		certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(caCert));
		certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifierStructure(intKey));
		certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(0));
		certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));

		return certGen.generate(caKey, "BC");
	}

	/**
	 * Generate a sample V3 certificate to use as an end entity certificate
	 */
	public static X509Certificate generateEndEntityCert(PublicKey entityKey, PrivateKey caKey, X509Certificate caCert) throws Exception {
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

		// certGen.setSerialNumber(BigInteger.valueOf(1));
		certGen.setSerialNumber(new BigInteger(10, new SecureRandom()));
		certGen.setIssuerDN(caCert.getSubjectX500Principal());
		certGen.setNotBefore(new Date(System.currentTimeMillis()));
		certGen.setNotAfter(new Date(System.currentTimeMillis() + VALIDITY_PERIOD));
		certGen.setSubjectDN(new X500Principal("CN=Test End Certificate"));
		certGen.setPublicKey(entityKey);
		certGen.setSignatureAlgorithm("SHA1WithRSAEncryption");

		certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(caCert));
		certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifierStructure(entityKey));
		certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
		certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));

		return certGen.generate(caKey, "BC");
	}

	/**
	 * Generate a X500PrivateCredential for the root entity.
	 */
	public static X500PrivateCredential createRootCredential() throws Exception {
		KeyPair rootPair = generateRSAKeyPair();
		X509Certificate rootCert = generateRootCert(rootPair);

		return new X500PrivateCredential(rootCert, rootPair.getPrivate(), ROOT_ALIAS);
	}

	/**
	 * Generate a X500PrivateCredential for the intermediate entity.
	 */
	public static X500PrivateCredential createIntermediateCredential(PrivateKey caKey, X509Certificate caCert) throws Exception {
		KeyPair interPair = generateRSAKeyPair();
		X509Certificate interCert = generateIntermediateCert(interPair.getPublic(), caKey, caCert);

		return new X500PrivateCredential(

		interCert, interPair.getPrivate(), INTERMEDIATE_ALIAS);
	}

	/**
	 * Generate a X500PrivateCredential for the end entity.
	 */
	public static X500PrivateCredential createEndEntityCredential(PrivateKey caKey, X509Certificate caCert) throws Exception {
		KeyPair endPair = generateRSAKeyPair();
		X509Certificate endCert = generateEndEntityCert(endPair.getPublic(), caKey, caCert);

		return new X500PrivateCredential(endCert, endPair.getPrivate(), END_ENTITY_ALIAS);
	}

	/**
	 * Create a KeyStore containing the a private credential with certificate
	 * chain and a trust anchor.
	 */
	public static KeyStore createCredentials() throws Exception {
		BouncyCastleUtils.initializeBouncyCastle();

		KeyStore store = KeyStore.getInstance("JKS");
		store.load(null, null);

		X500PrivateCredential rootCredential = Utils.createRootCredential();
		X500PrivateCredential interCredential = Utils.createIntermediateCredential(rootCredential.getPrivateKey(), rootCredential.getCertificate());
		X500PrivateCredential endCredential = Utils.createEndEntityCredential(interCredential.getPrivateKey(), interCredential.getCertificate());

		store.setCertificateEntry(rootCredential.getAlias(), rootCredential.getCertificate());

		store.setKeyEntry(endCredential.getAlias(), endCredential.getPrivateKey(), KEY_PASSWD, new Certificate[] { endCredential.getCertificate(), interCredential.getCertificate(),
				rootCredential.getCertificate() });
		return store;
	}

	/**
	 * Build a path using the given root as the trust anchor, and the passed in
	 * end constraints and certificate store.
	 * 
	 * Note: the path is built with revocation checking turned off.
	 */
	public static PKIXCertPathBuilderResult buildPath(X509Certificate rootCert, X509CertSelector endConstraints, CertStore certsAndCRLs) throws Exception {
		CertPathBuilder builder = CertPathBuilder.getInstance("PKIX", "BC");
		PKIXBuilderParameters buildParams = new PKIXBuilderParameters(Collections.singleton(new TrustAnchor(rootCert, null)), endConstraints);

		buildParams.addCertStore(certsAndCRLs);
		buildParams.setRevocationEnabled(false);

		return (PKIXCertPathBuilderResult) builder.build(buildParams);
	}

	public static String getSigningAliasxxx(KeyStore keyStore, char[] privateKeyPassword) {
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
	
	public static byte[] generateRandomByteArray(int size) {
		Random random = new Random();
		byte[] data = new byte[size];
		random.nextBytes(data);
		return data;
	}
	
	
	public static float[] getPosicion(byte[] filebytes, String texto){
//		PdfReader reader = new PdfReader(Thread.currentThread()
//				.getContextClassLoader().getResourceAsStream("test.pdf"));
		float[] coordenadas = new float[5];
		try{
		PdfReader reader = new PdfReader(filebytes);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        SpecificTextMarginFinder finder;
        
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            finder = parser.processContent(i, new SpecificTextMarginFinder(texto));
            if(finder.hasTextBeenFound()){
            	System.out.print("getLlx: ");
            	coordenadas[0] = finder.getLlx();
            	System.out.println(coordenadas[0]);
            	System.out.print("getLly: ");
            	coordenadas[1] = finder.getLly();
            	System.out.println(coordenadas[1]);
            	System.out.print("getUrx: ");
            	coordenadas[2] = finder.getLlx() + 130;
            	System.out.println(coordenadas[2]);
            	System.out.print("getUrx: ");
            	coordenadas[3] = finder.getLly() + 50;
            	System.out.println(coordenadas[3]);
            	coordenadas[4] = i;
            	break;
            }
        }
		} catch (IOException e) {
			e.printStackTrace();
			coordenadas[0] = 410.0F;
        	coordenadas[1] = 28.0F;
        	coordenadas[2] = 510.0F;
        	coordenadas[3] = 58.0F;
        	coordenadas[4] = 1;
		}
        return coordenadas;
	}
}