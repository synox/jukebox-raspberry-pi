package synox.jukebox.printer.model;

import com.google.gson.Gson;

public class Album {
	public String album = "";
	public String artist = "";

	// public int year = 0;

	public String toCode() {
		return new Gson().toJson(this);
	}

	public static Album parse(String s) {
		return new Gson().fromJson(s, Album.class);

	}
}
