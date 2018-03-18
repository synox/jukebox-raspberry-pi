package synox.jukebox.model;

import com.google.gson.Gson;

public class MediaKey {
	@Override
	public String toString() {
		return "MediaKey [album=" + album + ", artist=" + artist + "]";
	}

	public String song = null;
	public String album = "";
	public String artist = "";

	public static String toCode(MediaKey m) {
		return new Gson().toJson(m);
	}

	public static MediaKey parseCode(String code) {
		return new Gson().fromJson(code, MediaKey.class);
	}

}
