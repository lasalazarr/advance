package ec.gov.informatica.firmadigital.xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XmlSignature {

	public String signXML(String xml, X509Certificate cert, PrivateKey keyEntry) throws Exception {

		InputStream is = new ByteArrayInputStream(xml.getBytes());

		// Create a DOM XMLSignatureFactory to generate the enveloped signature.
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

		// SHA1 digest algorithm
		DigestMethod digestMethod = fac.newDigestMethod(DigestMethod.SHA1, null);

		// ENVELOPED Transform
		Transform transform = fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null);
		List<Transform> transforms = Collections.singletonList(transform);

		// Create a Reference to the enveloped document.
		// A URI of "" signifies you are signing the whole document.
		Reference reference = fac.newReference("", digestMethod, transforms, null, null);

		CanonicalizationMethod cm = fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
				(C14NMethodParameterSpec) null);
		SignatureMethod sm = fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null);
		List<Reference> references = Collections.singletonList(reference);

		// Create the SignedInfo.
		SignedInfo si = fac.newSignedInfo(cm, sm, references);

		// Create the KeyInfo containing the X509Data.
		KeyInfoFactory kif = fac.getKeyInfoFactory();
		List<Object> x509Content = new ArrayList<Object>();
		x509Content.add(cert.getSubjectX500Principal().getName());
		x509Content.add(cert);
		X509Data xd = kif.newX509Data(x509Content);
		KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

		// Instantiate the document to be signed.
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);

		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(is);

		// Create a DOMSignContext and specify the RSA PrivateKey and
		// location of the resulting XMLSignature's parent element.
		DOMSignContext dsc = new DOMSignContext(keyEntry, doc.getDocumentElement());

		// Create the XMLSignature, but don't sign it yet.
		XMLSignature signature = fac.newXMLSignature(si, ki);

		// Marshal, generate, and sign the enveloped signature.
		signature.sign(dsc);

		// Marshal
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer trans = tf.newTransformer();
		trans.transform(new DOMSource(doc), new StreamResult(out));

		return out.toString();
	}

	@SuppressWarnings({ "unused", "unchecked" })
	public String verify(String xml) throws Exception {
		InputStream is = new ByteArrayInputStream(xml.getBytes());

		// Instantiate the document to be signed.
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);

		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(is);

		// Find Signature element.
		NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");

		if (nl.getLength() == 0) {
			throw new Exception("Cannot find Signature element");
		}

		// Create a DOMValidateContext and specify a KeySelector
		// and document context.
		DOMValidateContext valContext = new DOMValidateContext(new X509KeySelector(), nl.item(0));

		// Create a DOM XMLSignatureFactory to generate the enveloped signature.
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

		valContext.setProperty("javax.xml.crypto.dsig.cacheReference", Boolean.TRUE);

		// Unmarshal the XMLSignature.
		XMLSignature signature = fac.unmarshalXMLSignature(valContext);

		// Validate the XMLSignature.
		boolean coreValidity = signature.validate(valContext);

		// Check core validation status.
		if (coreValidity == false) {
			//System.err.println("Signature failed core validation");
			boolean sv = signature.getSignatureValue().validate(valContext);
			//System.out.println("signature validation status: " + sv);
			if (sv == false) {
				// Check the validation status of each Reference.
				Iterator<?> i = signature.getSignedInfo().getReferences().iterator();
				for (int j = 0; i.hasNext(); j++) {
					boolean refValid = ((Reference) i.next()).validate(valContext);
					//System.out.println("ref[" + j + "] validity status: " + refValid);
				}
			}
		} else {
			//System.out.println("Signature passed core validation");
		}

		List<Reference> references = signature.getSignedInfo().getReferences();

		for (Reference reference : references) {
			InputStream iis = reference.getDigestInputStream();
			return convertStreamToString(iis);
		}

		throw new Exception("Cannot find Reference element");
	}

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

}

class X509KeySelector extends KeySelector {

	@SuppressWarnings("unchecked")
	@Override
	public KeySelectorResult select(KeyInfo keyInfo, KeySelector.Purpose purpose, AlgorithmMethod method,
			XMLCryptoContext context) throws KeySelectorException {

		List<XMLStructure> ki = keyInfo.getContent();

		for (XMLStructure info : ki) {
			if (!(info instanceof X509Data))
				continue;

			X509Data x509Data = (X509Data) info;
			List<Object> contents = x509Data.getContent();

			for (Object o : contents) {
				if (!(o instanceof X509Certificate))
					continue;

				final PublicKey key = ((X509Certificate) o).getPublicKey();

				// Make sure the algorithm is compatible with the method.
				if (algEquals(method.getAlgorithm(), key.getAlgorithm())) {
					return new KeySelectorResult() {
						public Key getKey() {
							return key;
						}
					};
				}
			}
		}

		throw new KeySelectorException("No key found!");
	}

	private static boolean algEquals(String algURI, String algName) {
		if ((algName.equalsIgnoreCase("DSA") && algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1))
				|| (algName.equalsIgnoreCase("RSA") && algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1))) {
			return true;
		} else {
			return false;
		}
	}
}