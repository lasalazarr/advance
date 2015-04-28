package ec.gov.informatica.firmadigital.signature;

import java.security.PrivateKey;
import java.security.PublicKey;

import sun.security.x509.CertAndKeyGen;
import sun.security.x509.X500Name;
 
public class GenCert
{
    public static void main(String[] args)throws Exception
    {
        CertAndKeyGen cakg = new CertAndKeyGen("RSA", "MD5WithRSA");
        cakg.generate(1024);
        
        PublicKey publicKey = cakg.getPublicKey();
        System.out.println(publicKey);
        
        PrivateKey privateKey = cakg.getPrivateKey();
        System.out.println(privateKey);
        
        X500Name name = new X500Name("One", "Two", "Three", "Four", "Five", "Six");
        System.out.println(name);
        
        //X509Cert cert = cakg.getSelfCert(name, 2000000); //deprecated
        java.security.cert.X509Certificate certificate = cakg.getSelfCertificate(name,2000000);
        System.out.println("cert: "+certificate);
        
        //X509Certificate certificate = X509Certificate.getInstance(cert.getSignedCert());
        certificate.checkValidity();
        System.out.println("Issuer DN .......... " + certificate.getIssuerDN());
        System.out.println("Not after .......... " + certificate.getNotAfter());
        System.out.println("Not before ......... " + certificate.getNotBefore());
        System.out.println("Serial No. ......... " + certificate.getSerialNumber());
        System.out.println("Signature Alg. ..... " + certificate.getSigAlgName());
        System.out.println("Signature Alg. OID . " + certificate.getSigAlgOID());
        System.out.println("Subject DN ......... " + certificate.getSubjectDN());
    }
}