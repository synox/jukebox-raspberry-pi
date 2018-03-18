package synox.jukebox.printer.layout;

import org.apache.jempbox.xmp.XMPMetadata;
import org.apache.jempbox.xmp.pdfa.XMPSchemaPDFAId;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import synox.jukebox.printer.model.Constants;
import synox.jukebox.printer.model.PrintBox;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Printer {

	public PDDocument getDocument() {
		return document;
	}
	private PDDocument document;
	private PDPageContentStream frontContentStream;
	private PDPageContentStream backContentStream;

	private PDTrueTypeFont font;
	private float startX;
	private float startY;
	private float paperEndX;

	public Printer() throws Exception {
		document = new PDDocument();

		// Create a new font object by loading a TrueType font into the document
		font = PDTrueTypeFont.loadTTF(document, Printer.class.getResourceAsStream("/cmunrm.ttf"));

		PDDocumentCatalog cat = document.getDocumentCatalog();
		PDMetadata metadata = new PDMetadata(document);
		cat.setMetadata(metadata);

		XMPMetadata xmp = new XMPMetadata();
		XMPSchemaPDFAId pdfaid = new XMPSchemaPDFAId(xmp);
		xmp.addSchema(pdfaid);
		pdfaid.setConformance("B");
		pdfaid.setPart(1);
		pdfaid.setAbout("");
		metadata.importXMPMetadata(xmp);

		InputStream colorProfile = Printer.class
				.getResourceAsStream("/sRGB Color Space Profile.icm");
		// create output intent
		PDOutputIntent oi = new PDOutputIntent(document, colorProfile);
		oi.setInfo("sRGB IEC61966-2.1");
		oi.setOutputCondition("sRGB IEC61966-2.1");
		oi.setOutputConditionIdentifier("sRGB IEC61966-2.1");
		oi.setRegistryName("http://www.color.org");
		cat.addOutputIntent(oi);
	}


	private List<PDPage> backPages = new ArrayList<>();
	private List<PDPage> frontPages = new ArrayList<>();

	public void print(List<PrintBox> boxes, String outFile) throws COSVisitorException,
			IOException {

		if (!outFile.endsWith(".pdf")) {
			outFile = outFile + ".pdf";
		}

		List<Card> cards = new ArrayList<>();

		// create first front page, so we can read the format
		nextPageFront();

		// layout all pages
		float vOffset = startY;
		float hOffset = startX;
		for (int i = 0; i < boxes.size(); i++) {
			PrintBox box = boxes.get(i);

			if (i > 0 && isLineBegin(i)) {
				vOffset -= Constants.boxHeight;
				hOffset = startX; // reset
			}
			if (isMakeNewPage(i)) {
				vOffset = startY;
				hOffset = startX;
				cards.add(new PageBreak());
			}
			cards.add(new Card(box, hOffset, vOffset, font));

			hOffset += Constants.boxWidth;
		}

		// print front pages, first page already startet above
		for (Card card : cards) {
			if (card instanceof PageBreak) {
				nextPageFront();
			} else {
				card.printFront(frontContentStream);
			}
		}
		frontContentStream.close();

		// print back pages
		nextPageBack();
		for (Card card : cards) {
			if (card instanceof PageBreak) {
				nextPageBack();
			} else {
				card.printBack(backContentStream, paperEndX);
			}
		}
		backContentStream.close();


		// add pages in alternating order
		for (int i = 0; i < frontPages.size(); i++) {
			document.addPage(frontPages.get(i));
			document.addPage(backPages.get(i));
		}

		// Save the results and ensure that the document is properly closed:
		document.save(outFile);
		document.close();
	}


	

	private boolean isMakeNewPage(int i) {
		return i > 0 && i % (Constants.boxesPerLine * Constants.linesPerPage) == 0;
	}

	private boolean isLineBegin(int i) {
		return i % Constants.boxesPerLine == 0;
	}

	private void nextPageFront() throws IOException {
		if (frontContentStream != null) {
			frontContentStream.close();
		}

		PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
		page.setRotation(90);
		frontPages.add(page);
		frontContentStream = new PDPageContentStream(document, page, false, false);
		frontContentStream.concatenate2CTM(0, 1, -1, 0, page.findMediaBox().getWidth(), 0);

		PDRectangle mediabox = page.findMediaBox();
		startX = mediabox.getLowerLeftX() + Constants.pageMargin;
		startY = mediabox.getWidth() - Constants.pageMargin;
		paperEndX = mediabox.getHeight();

		// print cut lines
		for (int i = 0; i < Constants.boxesPerLine; i++) {
			float hOffset = startX + Constants.boxWidth * i;
			float vOffset = startY;
			frontContentStream.drawLine(hOffset, vOffset, hOffset, vOffset + 10);
			frontContentStream.drawLine(hOffset + Constants.boxWidth, vOffset, hOffset + Constants.boxWidth,
					vOffset + 10);
		}
		for (int i = 0; i < Constants.linesPerPage; i++) {
			// left lines
			float hOffset = startX;
			float vOffset = startY - Constants.boxHeight * i;
			frontContentStream.drawLine(hOffset, vOffset, hOffset - 10, vOffset);
			frontContentStream.drawLine(hOffset, vOffset - Constants.boxHeight, hOffset - 10, vOffset
					- Constants.boxHeight);
		}
	}

	private void nextPageBack() throws IOException {
		if (backContentStream != null) {
			backContentStream.close();
		}

		PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
		page.setRotation(90);
		backPages.add(page);

		backContentStream = new PDPageContentStream(document, page, false, false);
		backContentStream.concatenate2CTM(0, 1, -1, 0, page.findMediaBox().getWidth(), 0);

		PDRectangle mediabox = page.findMediaBox();
		startX = mediabox.getLowerLeftX() + Constants.pageMargin;
		startY = mediabox.getWidth() - Constants.pageMargin;
		paperEndX = mediabox.getHeight();

		// print cut lines
		for (int i = 0; i < Constants.boxesPerLine; i++) {
			// Top lines
			float hOffsetInverse = paperEndX - (startX + Constants.boxWidth * i);
			backContentStream.drawLine(hOffsetInverse, startY, hOffsetInverse, startY + 10);
			backContentStream.drawLine(hOffsetInverse - Constants.boxWidth, startY,
					hOffsetInverse - Constants.boxWidth, startY + 10);
		}
		for (int i = 0; i < Constants.linesPerPage; i++) {
			float vOffset = startY - Constants.boxHeight * i;
			float hOffsetInverse = paperEndX - startX;
			// Right lines
			backContentStream.drawLine(hOffsetInverse, vOffset, hOffsetInverse + 10, vOffset);
			backContentStream.drawLine(hOffsetInverse, vOffset - Constants.boxHeight, hOffsetInverse + 10,
					vOffset - Constants.boxHeight);
		}
	}




}
