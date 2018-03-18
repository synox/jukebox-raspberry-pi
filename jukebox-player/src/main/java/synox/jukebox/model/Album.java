package synox.jukebox.model;

import java.util.ArrayList;
import java.util.List;

public class Album {
	public MediaKey key;

	private final List<Track> songs = new ArrayList<>();

	public void addSong(Track file) {
		songs.add(file);
	}
}
