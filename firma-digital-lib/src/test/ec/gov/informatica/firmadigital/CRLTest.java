package ec.gov.informatica.firmadigital;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.LDAPCertStoreParameters;
import java.security.cert.X509CertSelector;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.x500.X500Principal;

import ec.gov.informatica.firmadigital.cert.ErrorVerificacionRevocacion;

public class CRLTest {

	private static final Logger logger = Logger.getLogger(CRLTest.class
			.getName());

	/**
	 * URL de la Certificate Revocation List provista por el Banco Central del
	 * Ecuador
	 */
	private static final String CRL_URL = "http://www.eci.bce.ec/CRL/eci_bce_ec_crlfile.crl";
	// ldap://ldap.bce.ec/ou=eci,o=bce,c=ec?

	/**
	 * 
	 * @param certificate
	 * @return
	 * @throws ErrorVerificacionRevocacion
	 */
	public boolean isRevoked(Certificate certificate)
			throws ErrorVerificacionRevocacion {
		InputStream inStream = null;
		try {
			URL url = new URL(CRL_URL);
			inStream = url.openStream();
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			CRL crl = cf.generateCRL(inStream);
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

	public static void main(String[] args) throws Exception {
		// Archivo de certificado
		String keyStoreFile = "/home/ricardo/Documents/certificados/ricardoarguello.jks";
		char[] password = "password".toCharArray();

		/*
		 * // Obtener KeyStore KeyStoreProvider keyStoreProvider = new
		 * FileKeyStoreProvider( keyStoreFile); KeyStore keyStore =
		 * keyStoreProvider.getKeystore(password);
		 * 
		 * // Obtener certificado Enumeration<String> enumeration =
		 * keyStore.aliases(); String alias = enumeration.nextElement();
		 * Certificate certificate = keyStore.getCertificate(alias);
		 */

		LDAPCertStoreParameters lcsp = new LDAPCertStoreParameters(
				"ldap.bce.ec", 389);
		CertStore cs = CertStore.getInstance("LDAP", lcsp);

		
		// No se puede buscar solo por email: http://bugs.sun.com/view_bug.do?bug_id=4731456
		
		X509CertSelector xcs = new X509CertSelector();
		// select only unexpired certificates
		xcs.setCertificateValid(new Date());
		// select only certificates issued to
		// 'CN=alice, O=xyz, C=us'
		// xcs.setSubject(new X500Principal(
		// "CN=Marco Ricardo Arguello Jacome,OU=eci,O=bce,C=ec"));
		xcs.setSubject((X500Principal) null);
		xcs.setIssuer(new X500Principal("OU=eci,O=bce,C=ec"));
		// select only end-entity certificates
		xcs.setBasicConstraints(-2);
		// select only certificates with a digitalSignature
		// keyUsage bit set (set the first entry in the
		// boolean array to true)
		boolean[] keyUsage = { true };
		xcs.setKeyUsage(keyUsage);
		// select only certificates with a subjectAltName of
		// 'alice@xyz.com' (1 is the integer value of
		// an RFC822Name)
		//xcs.addSubjectAlternativeName(1, "ricardo.arguello@soportelibre.com");

		Collection<? extends Certificate> certs = cs.getCertificates(xcs);

		for (Certificate certificate2 : certs) {
			System.out.println(certificate2);
		}

		// CRLTest test = new CRLTest();
		// boolean isRevoked = test.isRevoked(certificate);
		// System.out.println("isRevoked=" + isRevoked);
	}

	/*
	private Set<TrustAnchor> getCACerts() throws IOException,
			CertificateException {
		Set<TrustAnchor> caCerts = new HashSet<TrustAnchor>();
		String caDirectory = getProperty("SSL.caDirectory");
		if (caDirectory != null) {
			Resource caDirRes = resourceLoader.getResource(caDirectory);
			File caDir = caDirRes.getFile();
			if (!caDir.isDirectory()) {
				log.error("Expecting directory as SSL.caDirectory parameter");
				throw new SLRuntimeException(
						"Expecting directory as SSL.caDirectory parameter");
			}
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			for (File f : caDir.listFiles()) {
				try {
					FileInputStream fis = new FileInputStream(f);
					X509Certificate cert = (X509Certificate) cf
							.generateCertificate(fis);
					fis.close();
					log.debug("Adding trusted cert " + cert.getSubjectDN());
					caCerts.add(new TrustAnchor(cert, null));
				} catch (Exception e) {
					log.error("Cannot add trusted ca", e);
				}
			}
			return caCerts;

		} else {
			log.warn("No CA certificates configured");
		}
		return null;
	}
	*/

}