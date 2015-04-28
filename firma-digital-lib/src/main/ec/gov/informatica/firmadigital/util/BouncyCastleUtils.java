package ec.gov.informatica.firmadigital.util;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class BouncyCastleUtils {

	/**
	 * Inicializa el Proveedor de Seguridad BouncyCastle
	 */
	public static void initializeBouncyCastle() {
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			BouncyCastleProvider BC_provider = new BouncyCastleProvider();
			Security.addProvider(BC_provider);
			System.out.println("Cargado " + BC_provider.getInfo());
		}
	}
}