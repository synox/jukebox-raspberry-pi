package synox.jukebox.model;

import java.nio.file.Path;

public class Track {
	public Path file;
	public String name;

	public Track(Path file) {
		this.file = file;
	}
}
