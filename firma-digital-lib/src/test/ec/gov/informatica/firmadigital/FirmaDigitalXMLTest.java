package ec.gov.informatica.firmadigital;

import java.security.KeyStore;

import org.junit.Test;

import ec.gov.informatica.firmadigital.keystore.KeyStoreProvider;
import ec.gov.informatica.firmadigital.keystore.MockKeyStoreProvider;

public class FirmaDigitalXMLTest {

	private static final char[] PASSWORD = "password".toCharArray();
	
	@Test
	public void testXMLSignature() throws Exception {
		// Obtener un KeyStore de prueba
		KeyStoreProvider keyStoreProvider = new MockKeyStoreProvider();
		KeyStore keyStore = keyStoreProvider.getKeystore();
		
		FirmaDigitalXML xml = new FirmaDigitalXML(keyStore);
		xml.setPrivateKeyPassword(PASSWORD);
		String signed = xml.firmar("<a>1</a>");
		System.out.println("<a>1</a>");

		System.out.println(signed);
		System.out.println(xml.verificar(signed));
	}
}
