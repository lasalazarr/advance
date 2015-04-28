package ec.gov.informatica.firmadigital.cert;

public class RevocationException extends Exception {

	private static final long serialVersionUID = 8862141748488916348L;

	public RevocationException() {
	}

	public RevocationException(String message) {
		super(message);
	}

	public RevocationException(Throwable cause) {
		super(cause);
	}

	public RevocationException(String message, Throwable cause) {
		super(message, cause);
	}
}