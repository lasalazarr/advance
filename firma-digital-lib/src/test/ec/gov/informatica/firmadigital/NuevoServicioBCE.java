package ec.gov.informatica.firmadigital;

import java.security.cert.X509Certificate;

import ec.gov.informatica.firmadigital.cert.ServicioCertificadoBancoCentral;

public class NuevoServicioBCE {

	public static void main(String[] args) throws Exception {
		ServicioCertificadoBancoCentral bce = new ServicioCertificadoBancoCentral();
		X509Certificate cert = bce.obtenerCertificado("ricardo.arguello@soportelibre.com");
		boolean revocado = bce.estaRevocado(cert);
		System.out.println(cert);
		System.out.println("revocado=" + revocado);
	}
}