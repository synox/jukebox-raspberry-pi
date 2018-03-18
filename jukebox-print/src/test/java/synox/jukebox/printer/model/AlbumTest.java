package synox.jukebox.printer.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class AlbumTest {
	@Test
	public void testAlbum() throws Exception {
		Album a = new Album();
		a.artist = "Agnes Obel";
		a.album = "Philharmonics";
		// a.year = 2011;
		assertEquals("{\"album\":\"Philharmonics\",\"artist\":\"Agnes Obel\"}",
				a.toCode());
	}
}
