package synox.jukebox.printer.model;

import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

public class PrintBox {

	private final Album album;
	private final PDXObjectImage image;
	private final PDXObjectImage qrImage;

	public Album getAlbum() {
		return album;
	}

	public PDXObjectImage getImage() {
		return image;
	}

	public PDXObjectImage getQrImage() {
		return qrImage;
	}

	public PrintBox(Album album, PDXObjectImage image, PDXObjectImage qrImage) {
		this.album = album;
		this.image = image;
		this.qrImage = qrImage;
	}

}
