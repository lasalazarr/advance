package ec.gov.informatica.firmadigital.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Set;

/**
 * Implementacion de X509Certificate utilizando el certificado raiz
 * del Banco Central del Ecuador, obtenido de su pagina web
 * http://www.bce.fin.ec/
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.1 $
 */
public class CertificadoBancoCentral extends X509Certificate {

	private X509Certificate certificate;

	public CertificadoBancoCentral() {
		super();

		StringBuffer cer = new StringBuffer();
		cer.append("-----BEGIN CERTIFICATE-----\n");
		cer.append("MIIEwTCCA6mgAwIBAgIESQCvojANBgkqhkiG9w0BAQUFADApMQswCQYDVQQGEwJl\n");
		cer.append("YzEMMAoGA1UEChMDYmNlMQwwCgYDVQQLEwNlY2kwHhcNMDgxMDIzMTYzODUxWhcN\n");
		cer.append("MjgxMDIzMTcwODUxWjApMQswCQYDVQQGEwJlYzEMMAoGA1UEChMDYmNlMQwwCgYD\n");
		cer.append("VQQLEwNlY2kwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDDLQrdgDQ6\n");
		cer.append("ejQfnK/yo5WLEzeqfQJN7j3GuCs1oetyBrXdg2TOsj8AGuFY2eGtVjKYFW3XjtZ9\n");
		cer.append("Lw+1JsFbPkbKCbtTTgWO6TwHTKgX38E13l3u3JpKlRha3N2W51n3iVm/eG6EqgDt\n");
		cer.append("3Zldd+6E06eZkw4fs2jmI6IKHdMlcs3+wojG+rs32ld8+EeG7/S+v6Lu9eAr3dNC\n");
		cer.append("7ZnGhEL4lRzcN35hBUznhhn8m5ZMCEYcK7DyOYDNNzcgGrCIKV0j8UjLdwz13foA\n");
		cer.append("P1KlwvF4rtOKCzE+EVBEICot7hFF6bxCNcGE9Z2UiKTPRkEfTzheGMEi/0RUlWhq\n");
		cer.append("wOK0Fu6gysa5AgMBAAGjggHvMIIB6zARBglghkgBhvhCAQEEBAMCAAcwggEtBgNV\n");
		cer.append("HR8EggEkMIIBIDBAoD6gPKQ6MDgxCzAJBgNVBAYTAmVjMQwwCgYDVQQKEwNiY2Ux\n");
		cer.append("DDAKBgNVBAsTA2VjaTENMAsGA1UEAxMEQ1JMMTCB26CB2KCB1YYtaHR0cDovL0VD\n");
		cer.append("SUJDRVVJTzFXL0NSTC9lY2lfYmNlX2VjX2NybGZpbGUuY3JshixmdHA6Ly9FQ0lC\n");
		cer.append("Q0VVSU8xVy9DUkwvZWNpX2JjZV9lY19jcmxmaWxlLmNybIZFbGRhcDovLzE3Mi4y\n");
		cer.append("MC40MC4yMzIvb3U9ZWNpLG89YmNlLGM9ZWM/Y2VydGlmaWNhdGVSZXZvY2F0aW9u\n");
		cer.append("TGlzdD9iYXNlhi9maWxlOi8vXFxFQ0lCQ0VVSU8xV1xDUkxcZWNpX2JjZV9lY19j\n");
		cer.append("cmxmaWxlLmNybDArBgNVHRAEJDAigA8yMDA4MTAyMzE2Mzg1MVqBDzIwMjgxMDIz\n");
		cer.append("MTcwODUxWjALBgNVHQ8EBAMCAQYwHwYDVR0jBBgwFoAUeuYM7FXmBiDKcGrysLsi\n");
		cer.append("mSJMg5EwHQYDVR0OBBYEFHrmDOxV5gYgynBq8rC7IpkiTIORMAwGA1UdEwQFMAMB\n");
		cer.append("Af8wHQYJKoZIhvZ9B0EABBAwDhsIVjcuMTo0LjADAgSQMA0GCSqGSIb3DQEBBQUA\n");
		cer.append("A4IBAQBtjhS7UL8m4IoLOieRgru/T5n8JwOndyUTB0wSPp0YC5wWury1m92wg/o5\n");
		cer.append("QI03KuYkMvDOSFo48cJ/HVq1ez6nc5pYI+xUpMZqrIFok+vlPipTiGNALe/cJ8L/\n");
		cer.append("dsJ2/TmJVk1has992uUVa4Ue1BkFwSC98NczV0ochLOsf9rz5TvpUQQpblMYwrgE\n");
		cer.append("SG9UEV3n2iA7adyXHXCrkC9Lwv3zJZRRrC8a8AkjD7/+88OO6jISBIl16ZO0P/0y\n");
		cer.append("PDNxAWij9p42NHN1G2uqcZhc9HXKbXXdof6sZ5qOhg7M96gz7NrLXCUQvEK1Dzg3\n");
		cer.append("oNQJ38IBvGHmc7BmcZTdE00DxGFf\n");
		cer.append("-----END CERTIFICATE-----");

		try {
			InputStream is = new ByteArrayInputStream(cer.toString().getBytes("UTF-8"));
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			this.certificate = (X509Certificate) cf.generateCertificate(is);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		} catch (GeneralSecurityException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
		certificate.checkValidity();
	}

	@Override
	public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
		certificate.checkValidity(date);
	}

	@Override
	public int getBasicConstraints() {
		return certificate.getBasicConstraints();
	}

	@Override
	public Principal getIssuerDN() {
		return certificate.getIssuerDN();
	}

	@Override
	public boolean[] getIssuerUniqueID() {
		return certificate.getIssuerUniqueID();
	}

	@Override
	public boolean[] getKeyUsage() {
		return certificate.getKeyUsage();
	}

	@Override
	public Date getNotAfter() {
		return certificate.getNotAfter();
	}

	@Override
	public Date getNotBefore() {
		return certificate.getNotBefore();
	}

	@Override
	public BigInteger getSerialNumber() {
		return certificate.getSerialNumber();
	}

	@Override
	public String getSigAlgName() {
		return certificate.getSigAlgName();
	}

	@Override
	public String getSigAlgOID() {
		return certificate.getSigAlgOID();
	}

	@Override
	public byte[] getSigAlgParams() {
		return certificate.getSigAlgParams();
	}

	@Override
	public byte[] getSignature() {
		return certificate.getSignature();
	}

	@Override
	public Principal getSubjectDN() {
		return certificate.getSubjectDN();
	}

	@Override
	public boolean[] getSubjectUniqueID() {
		return certificate.getSubjectUniqueID();
	}

	@Override
	public byte[] getTBSCertificate() throws CertificateEncodingException {
		return certificate.getTBSCertificate();
	}

	@Override
	public int getVersion() {
		return certificate.getVersion();
	}

	@Override
	public byte[] getEncoded() throws CertificateEncodingException {
		return certificate.getEncoded();
	}

	@Override
	public PublicKey getPublicKey() {
		return certificate.getPublicKey();
	}

	@Override
	public String toString() {
		return certificate.toString();
	}

	@Override
	public void verify(PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
		certificate.verify(key);
	}

	@Override
	public void verify(PublicKey key, String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
		certificate.verify(key, sigProvider);
	}

	@Override
	public Set<String> getCriticalExtensionOIDs() {
		return certificate.getCriticalExtensionOIDs();
	}

	@Override
	public byte[] getExtensionValue(String oid) {
		return certificate.getExtensionValue(oid);
	}

	@Override
	public Set<String> getNonCriticalExtensionOIDs() {
		return certificate.getNonCriticalExtensionOIDs();
	}

	@Override
	public boolean hasUnsupportedCriticalExtension() {
		return certificate.hasUnsupportedCriticalExtension();
	}
}