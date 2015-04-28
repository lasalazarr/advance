package ec.gov.informatica.firmadigital.cert;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.cert.Certificate;

public class PrivateKeyAndCertificateChain implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3491611290576134775L;
	private Certificate[] certificateChain;
	private PrivateKey privateKey;
	
	

	public PrivateKeyAndCertificateChain() {
		super();
	}
	
	public PrivateKeyAndCertificateChain(Certificate[] certificateChain,
			PrivateKey privateKey) {
		super();
		this.certificateChain = certificateChain;
		this.privateKey = privateKey;
	}



	public Certificate[] getCertificateChain() {
		return certificateChain;
	}

	public void setCertificateChain(Certificate[] certificateChain) {
		this.certificateChain = certificateChain;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

}
