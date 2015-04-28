package ec.gov.informatica.firmadigital.keystore;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormatSymbols;
import java.util.logging.Logger;

/**
 * Obtiene la implementacion correcta de KeyStoreProvider de acuerdo al sistema
 * operativo.
 * 
 * @deprecated Se recomienda establecer el KeyStoreProvider a mano.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1.4 $
 */
public class KeyStoreProviderFactory {

	private static final Logger log = Logger
			.getLogger(KeyStoreProviderFactory.class.getName());

	/**
	 * Obtiene la implementacion correcta de KeyStoreProvider de acuerdo al
	 * sistema operativo.
	 * 
	 * @return implementacion de KeyStoreProvider
	 */
	public static KeyStoreProvider createKeyStoreProvider() {
		String osName = System.getProperty("os.name");
		String javaVersion = System.getProperty("java.version");

		log.info("Operating System:" + osName);
		log.info("Java Version:" + javaVersion);

		if (osName.toUpperCase().indexOf("WINDOWS") == 0) {
			log.info("Using jdk windows provider");
			Double dblJavaVersion = getJavaVersion(javaVersion);

			if (dblJavaVersion >= 1.6) {
				return new WindowsJDKKeyStoreProvider();
			}

			// if(javaVersion.startsWith("1.6")){
			// return new WindowsJDK6KeyStoreProvider();
			// } else if(javaVersion.startsWith("1.7")){
			// return new WindowsJDK7KeyStoreProvider();
			// } else if(javaVersion.startsWith("1.8")){
			// return new WindowsJDK8KeyStoreProvider();
			// }
		} else if (osName.toUpperCase().indexOf("LINUX") == 0) {
			log.info("Using safenet linux provider");
			return new SafeNetLinuxKeyStoreProvider();
		} else if (osName.toUpperCase().indexOf("MAC") == 0) {
			log.info("Using safenet apple provider");
			return new AppleKeyStoreProvider();
		}
		throw new IllegalArgumentException("Sistema operativo no soportado!");
	}

	/**
	 * @param javaVersion
	 * @return
	 */
	private static Double getJavaVersion(String javaVersion) {
		int firstDotIndex = javaVersion.indexOf(".");
		String majorVersion = javaVersion.substring(0, firstDotIndex);
		String minorVersion = javaVersion.substring(firstDotIndex + 1);
		// Replace with regex de la minorVersion
		String minorVersionNoDots = minorVersion.replaceAll("[^\\d]", "");
		StringBuilder sbJavaVersion = new StringBuilder();
		sbJavaVersion.append(majorVersion);
		sbJavaVersion.append(minorVersionNoDots);
		BigDecimal bd = new BigDecimal(
				new BigInteger(sbJavaVersion.toString()),
				minorVersionNoDots.length());
		return bd.doubleValue();
	}
}