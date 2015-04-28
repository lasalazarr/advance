package ec.gov.informatica.firmadigital;

import java.security.KeyStore;
import java.security.cert.Certificate;

import org.junit.Test;

import ec.gov.informatica.firmadigital.keystore.KeyStoreProvider;
import ec.gov.informatica.firmadigital.keystore.MockKeyStoreProvider;
import ec.gov.informatica.firmadigital.util.Utils;

public class FirmaDigitalSimpleTest {

	private static final char[] PASSWORD = "password".toCharArray();
	private static final byte[] DATA = Utils.generateRandomByteArray(238);

	@Test
	public void testFirmarSimple() throws Exception {
		// Obtener un KeyStore de prueba
		KeyStoreProvider keyStoreProvider = new MockKeyStoreProvider();
		KeyStore keyStore = keyStoreProvider.getKeystore();

		// Firmar
		FirmaDigitalSimple firma = new FirmaDigitalSimple(keyStore);
		firma.setPrivateKeyPassword(PASSWORD);
		byte[] signature = firma.firmar(DATA);

		// Solo para la prueba:
		String alias = FirmaDigital.getSigningAlias(keyStore, PASSWORD);
		Certificate certificate = keyStore.getCertificate(alias);
		
		// Verificar
		firma.verify(DATA, signature, certificate.getPublicKey());
	}
}