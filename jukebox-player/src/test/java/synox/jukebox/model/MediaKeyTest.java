package synox.jukebox.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class MediaKeyTest {
	@Test
	public void testName() throws Exception {
		MediaKey m = new MediaKey();
		m.album = "Today";
		m.artist = "Jim";
		m.song = "This place";
		assertEquals("{\"song\":\"This place\",\"album\":\"Today\",\"artist\":\"Jim\"}", MediaKey.toCode(m));
	}
}
