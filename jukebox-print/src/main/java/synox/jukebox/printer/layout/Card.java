package synox.jukebox.printer.layout;

import static synox.jukebox.printer.model.Constants.*;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;

import synox.jukebox.printer.model.Paragraph;
import synox.jukebox.printer.model.PrintBox;

public class Card {

	float vOffset;
	float hOffset;
	private PrintBox model;
	private PDTrueTypeFont font;

	public Card(PrintBox box, float hOffset, float vOffset, PDTrueTypeFont font) {
		this.model = box;
		this.hOffset = hOffset;
		this.vOffset = vOffset;
		this.font = font;
	}

	public void printFront(PDPageContentStream frontContentStream) throws IOException {
		float vertical = vOffset;

		// Padding on top
		vertical -= paddingTop;

		// Album
		Paragraph album = new Paragraph(hOffset + paddingLeft, vertical, model.getAlbum().album)
				.withWidth(coverSize).withFont(font, 12);
		vertical -= Utils.write(album, frontContentStream);

		// Padding between
		vertical -= textPadding;

		// Artist
		Paragraph artist = new Paragraph(hOffset + paddingLeft, vertical, model.getAlbum().artist)
				.withWidth(coverSize).withFont(font, 10);
		vertical -= Utils.write(artist, frontContentStream);

		// padding
		vertical -= textPadding;

		// cover
		frontContentStream.drawXObject(model.getImage(), hOffset + paddingLeft, vertical
				- coverSize,
				coverSize, coverSize);
		vertical -= coverSize;

		// the rest is dynamic

	}

	public void printBack(PDPageContentStream backContentStream, float paperEndX)
			throws IOException {
		// Center the qr_code in the box
		float hPos = hOffset + (boxWidth - qrCodeSize) / 2;

		// vertial middle
		float inverseLeftSide = paperEndX - hPos;
		float vPos = vOffset - (boxHeight - qrCodeSize) / 2;
		backContentStream.drawXObject(model.getQrImage(), inverseLeftSide - qrCodeSize, vPos
				- qrCodeSize, qrCodeSize, qrCodeSize);

	}

}