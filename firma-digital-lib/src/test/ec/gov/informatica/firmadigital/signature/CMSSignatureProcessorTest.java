package ec.gov.informatica.firmadigital.signature;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ec.gov.informatica.firmadigital.FirmaDigital;
import ec.gov.informatica.firmadigital.keystore.KeyStoreProvider;
import ec.gov.informatica.firmadigital.keystore.MockKeyStoreProvider;
import ec.gov.informatica.firmadigital.util.Utils;

public class CMSSignatureProcessorTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private static final char[] PASSWORD = "password".toCharArray();

	@Test
	public void testAttachedSignature() throws Exception {
		KeyStoreProvider ksp = new MockKeyStoreProvider();
		KeyStore keyStore = ksp.getKeystore();

		String alias = FirmaDigital.getSigningAlias(keyStore, PASSWORD);
		PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, PASSWORD);
		Certificate[] certificateChain = keyStore.getCertificateChain(alias);

		byte[] data = Utils.generateRandomByteArray(299);

		CMSSignatureProcessor signatureProcessor = new CMSSignatureProcessor();
		byte[] signature = signatureProcessor.sign(data, privateKey, certificateChain);

		byte[] data2 = signatureProcessor.verify(signature);
		assertArrayEquals(data, data2);

		try {
			byte[] forged = new byte[signature.length];
			System.arraycopy(signature, 0, forged, 0, signature.length);
			forged[forged.length - 1] = 4;
			signatureProcessor.verify(forged);
			exception.expect(SignatureVerificationException.class);
		} catch (Exception ignore) {
		}
	}
}