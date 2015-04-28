package ec.gov.informatica.firmadigital;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Hashtable;

import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.Time;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;

public class Dev {

	public byte[] sign(PrivateKey privateKey, X509Certificate cert,
			byte[] dataToSign) throws CMSException, NoSuchProviderException,
			NoSuchAlgorithmException, IOException {
	
		AttributeTable attributeTable = getSigningTime();
		
		CMSSignedDataGenerator sgen = new CMSSignedDataGenerator();
		sgen.addSigner(privateKey, cert, CMSSignedDataGenerator.DIGEST_MD5, attributeTable, null);

		CMSSignedData csd = sgen.generate(new CMSProcessableByteArray(dataToSign), true, "BC");
		return csd.getEncoded();
	}
	
	private AttributeTable  getSigningTime() {
		// Agregar la fecha y hora de la firma
		Date date = new Date(); // Get it from the server!
		Time time = new Time(date);
		Attribute attribute = new Attribute(CMSAttributes.signingTime, new DERSet(time.toASN1Primitive()));
		Hashtable<DERObjectIdentifier, Attribute> hashTable = new Hashtable<DERObjectIdentifier, Attribute> ();
		hashTable.put(CMSAttributes.signingTime, attribute);
		return new AttributeTable(hashTable);
	}
}