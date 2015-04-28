package ec.gov.informatica.firmadigital.util;

import sun.misc.BASE64Encoder;

public class Base64Test {
	
	public static void main (String []args)throws Exception{
		
		String texto = "abcfdsafdshakjfhkjafdkj hfkjdsa fdsahjkfd shjk fhd kjsafhkjsa hkj hkjfd sahkjfds ahjk ";
		String texto2 = "abcfdsafdshakjfhkjafdkj hfkjdsa fdsahjkfd shjk fhd kjsafhkjsa hkj hkjfd sahkjfds ahjk ";
		String texto3 = "abcfdsafdshakjfhkjafdkj hfkjdsa fdsahjkfd shjk fhd kjsafhkjsa hkj hkjfd sahkjfds ahjk ";
		
		String damn = texto+texto2+texto3;
		String textoBase64= Base64.encodeBytes(damn.getBytes(),Base64.DO_BREAK_LINES);
		System.out.println(textoBase64);
		
		System.out.println("---------------");
		String fuck = new BASE64Encoder().encode(damn.getBytes());
		System.out.println(fuck);
	}
}