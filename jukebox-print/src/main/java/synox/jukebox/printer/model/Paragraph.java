package synox.jukebox.printer.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class Paragraph {

	/** position X */
	private float x;

	/** position Y */
	private float y;

	/** width of this paragraph */
	private int width = 500;

	/** text to write */
	private String text;

	/** font to use */
	private PDSimpleFont font = PDType1Font.HELVETICA;

	/** font size to use */
	private int fontSize = 12;


	public Paragraph(float x, float y, String text) {
		this.x = x;
		this.y = y;
		this.text = text;
	}

	/**
	 * Break the text in lines
	 * 
	 * @return
	 */
	public List<String> getLines() throws IOException {
		List<String> result = new ArrayList<>();

		String[] split = text.split("(?<=\\W)");
		int[] possibleWrapPoints = new int[split.length];
		possibleWrapPoints[0] = split[0].length();
		for (int i = 1; i < split.length; i++) {
			possibleWrapPoints[i] = possibleWrapPoints[i - 1] + split[i].length();
		}

		int start = 0;
		int end = 0;
		for (int i : possibleWrapPoints) {
			float width = font.getStringWidth(text.substring(start, i)) / 1000 * fontSize;
			if (start < end && width > this.width) {
				result.add(text.substring(start, end));
				start = end;
			}
			end = i;
		}
		// Last piece of text
		result.add(text.substring(start));
		return result;
	}

	int reduceFontHeight = 3;
	public float getFontHeight() {
		return font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize
				- reduceFontHeight;
	}

	public Paragraph withWidth(int width) {
		this.width = width;
		return this;
	}

	public Paragraph withFont(PDSimpleFont font, int fontSize) {
		this.font = font;
		this.fontSize = fontSize;
		return this;
	}


	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public String getText() {
		return text;
	}

	public PDSimpleFont getFont() {
		return font;
	}

	public int getFontSize() {
		return fontSize;
	}
}