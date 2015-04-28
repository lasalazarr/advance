package ec.gov.informatica.firmadigital.util;

import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.pdf.parser.TextMarginFinder;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

public class SpecificTextMarginFinder extends TextMarginFinder {

	private Rectangle2D.Float textRectangle = null;
	private Rectangle2D.Float lastTextRectangle = null;
	private String[] searchStringArr = null;
	private String searchString = null;
	private int lastSeqChar;

	public SpecificTextMarginFinder(String searchString) {
		super();
		this.searchString = searchString;
		char[] seqChars = searchString.toCharArray();
		this.searchStringArr = new String[seqChars.length];
		int i = 0;
		for (char seqChar : seqChars) {
			this.searchStringArr[i] = new String(new char[] { seqChar });
			i++;
		}
		this.lastSeqChar = 0;
	}

	/**
	 * Method invokes by the PdfContentStreamProcessor. Passes a TextRenderInfo
	 * for every text chunk that is encountered. We'll use this object to obtain
	 * coordinates.
	 * 
	 * @see com.itextpdf.text.pdf.parser.RenderListener#renderText(com.itextpdf.text.pdf.parser.TextRenderInfo)
	 */
	// public void renderText(TextRenderInfo renderInfo) {
	// if (lastTextRectangle == null) {
	// String content = renderInfo.getText();
	// if (content != null
	// && content.equals(searchStringArr[this.lastSeqChar])) {
	// if (this.lastSeqChar == 0) {
	// textRectangle = renderInfo.getDescentLine()
	// .getBoundingRectange();
	// }
	// this.lastSeqChar++;
	// if (searchStringArr.length == this.lastSeqChar) {
	// lastTextRectangle = renderInfo.getDescentLine()
	// .getBoundingRectange();
	// }
	// } else {
	// this.lastSeqChar = 0;
	// this.textRectangle = null;
	// }
	// }
	// }

	public void renderText(TextRenderInfo renderInfo) {
		if (lastTextRectangle == null) {
			String content = renderInfo.getText();
			if (content != null) {
				if (content.length() == 1) {
					if (content.equals(searchStringArr[this.lastSeqChar])) {
						if (this.lastSeqChar == 0) {
							textRectangle = renderInfo.getDescentLine()
									.getBoundingRectange();
						}
						this.lastSeqChar++;
						if (searchStringArr.length == this.lastSeqChar) {
							lastTextRectangle = renderInfo.getDescentLine()
									.getBoundingRectange();
						}
					} else {
						this.lastSeqChar = 0;
						this.textRectangle = null;
					}
				} else {
					if (content.equals(this.searchString)) {
						if (this.lastSeqChar == 0) {
							textRectangle = renderInfo.getDescentLine()
									.getBoundingRectange();
							lastTextRectangle = renderInfo.getDescentLine()
									.getBoundingRectange();
						}
					}else {
						this.lastSeqChar = 0;
						this.textRectangle = null;
					}
				}
			}
		}
	}

	private void evaluateContent(String contentVal, TextRenderInfo renderInfo, boolean isMoreThanOne) {
		
	}

	public boolean hasTextBeenFound() {
		return this.lastTextRectangle != null;
	}

	/**
	 * Getter for the left margin.
	 * 
	 * @return the X position of the left margin
	 */
	public float getLlx() {
		return this.textRectangle.x;
	}

	/**
	 * Getter for the bottom margin.
	 * 
	 * @return the Y position of the bottom margin
	 */
	public float getLly() {
		return this.textRectangle.y;
	}

	/**
	 * Getter for the right margin.
	 * 
	 * @return the X position of the right margin
	 */
	public float getUrx() {
		return this.lastTextRectangle.x + this.lastTextRectangle.width;
	}

	/**
	 * Getter for the top margin.
	 * 
	 * @return the Y position of the top margin
	 */
	public float getUry() {
		return this.textRectangle.y + this.textRectangle.height;
	}

	/**
	 * Gets the width of the text block.
	 * 
	 * @return a width
	 */
	public float getWidth() {
		return (this.lastTextRectangle.x - this.textRectangle.x)
				+ this.lastTextRectangle.width;
	}

	/**
	 * Gets the height of the text block.
	 * 
	 * @return a height
	 */
	public float getHeight() {
		return this.textRectangle.height;
	}

}
