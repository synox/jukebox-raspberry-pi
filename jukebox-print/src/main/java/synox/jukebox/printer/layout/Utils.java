package synox.jukebox.printer.layout;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import synox.jukebox.printer.model.Paragraph;

public class Utils {

	public static float write(Paragraph paragraph, PDPageContentStream out) throws IOException {
		out.beginText();
		out.appendRawCommands(paragraph.getFontHeight() + " TL\n");
		out.setFont(paragraph.getFont(), paragraph.getFontSize());
		out.moveTextPositionByAmount(paragraph.getX(), paragraph.getY());
	
		List<String> lines = paragraph.getLines();
		for (Iterator<String> i = lines.iterator(); i.hasNext();) {
			out.drawString(i.next().trim());
			if (i.hasNext()) {
				out.appendRawCommands("T*\n");
			}
		}
		out.endText();
	
		float height = lines.size() * paragraph.getFontHeight();
		return height;
	}

}
