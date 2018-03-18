package synox.jukebox.musicResolver;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import synox.jukebox.model.MediaKey;

public class MusicResolverTest {
	@Test
	public void simple() throws Exception {
		MusicResolver resolver = new MusicResolver(Paths.get("/Users/synox/Music"));

		MediaKey a = new MediaKey();
		a.artist = "Frittenbude";
		a.album = "Katzengold";
		Path f = resolver.findFuzzy(a);
		assertEquals("/Users/synox/Music/Frittenbude/Katzengold", f.toString());
	}

	@Test
	public void simple_swapAlbumArtist() throws Exception {
		MusicResolver resolver = new MusicResolver(Paths.get("/Users/synox/Music"));

		MediaKey a = new MediaKey();
		a.artist = "Katzengold";
		a.album = "Frittenbude";
		Path f = resolver.findFuzzy(a);
		assertEquals("/Users/synox/Music/Frittenbude/Katzengold", f.toString());
	}

	@Test
	public void startsWith1() throws Exception {
		MusicResolver resolver = new MusicResolver(Paths.get("/Users/synox/Music"));

		MediaKey a = new MediaKey();
		a.artist = "Frittenbude";
		a.album = "Katzeng";
		Path f = resolver.findFuzzy(a);
		assertEquals("/Users/synox/Music/Frittenbude/Katzengold", f.toString());
	}

	@Test
	public void startsWith12() throws Exception {
		MusicResolver resolver = new MusicResolver(Paths.get("/Users/synox/Music"));

		MediaKey a = new MediaKey();
		a.artist = "Frittenbude";
		a.album = "Katzengold Volume 1";
		Path f = resolver.findFuzzy(a);
		assertEquals("/Users/synox/Music/Frittenbude/Katzengold", f.toString());
	}

	@Test
	public void testCompilcation() throws Exception {
		MusicResolver resolver = new MusicResolver(Paths.get("/Users/synox/Music"));

		MediaKey a = new MediaKey();
		a.album = "Coyote Ugly";
		Path f = resolver.findFuzzy(a);
		assertEquals("/Users/synox/Music/Compilations/Coyote Ugly", f.toString());
	}

	@Test
	public void testCompilcation_notFound() throws Exception {
		MusicResolver resolver = new MusicResolver(Paths.get("/Users/synox/Music"));

		MediaKey a = new MediaKey();
		a.album = "Aasdf no album with that name";
		Path f = resolver.findFuzzy(a);
		assertNull(f);
	}

	@Test
	public void testName2() throws Exception {
		MusicResolver resolver = new MusicResolver(Paths.get("/Users/synox/Music"));

		MediaKey a = new MediaKey();
		a.artist = "Gavin DeGraw";
		a.album = "Not Over You";
		Path f = resolver.findFuzzy(a);
		assertEquals("/Users/synox/Music/Gavin DeGraw/Not Over You - Single", f.toString());
	}

	@Test
	public void song1() throws Exception {
		MusicResolver resolver = new MusicResolver(Paths.get("/Users/synox/Music"));

		MediaKey a = new MediaKey();
		a.artist = "Frittenbude";
		a.album = "Delfinarium";
		a.song = "Heimatlos";
		Path f = resolver.findFuzzy(a);
		assertEquals("/Users/synox/Music/Frittenbude/Delfinarium (Deluxe Edition)/07 Heimatlos.m4a", f.toString());
	}

	@Test
	public void song2() throws Exception {
		MusicResolver resolver = new MusicResolver(Paths.get("/Users/synox/Music"));

		MediaKey a = new MediaKey();
		a.artist = "Frittenbude";
		a.album = "Delfinarium";
		a.song = "Heimatlos Single Edition";
		Path f = resolver.findFuzzy(a);
		assertEquals("/Users/synox/Music/Frittenbude/Delfinarium (Deluxe Edition)/07 Heimatlos.m4a", f.toString());
	}

}
