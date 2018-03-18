package synox.jukebox.player;

import synox.jukebox.model.Track;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Playlist implements Iterator<Track> {
	public static Playlist emptyPlaylist() {
		return new Playlist();
	}

	int indexOfCurrentTrack = 0;
	List<Track> tracks = new ArrayList<>();

	public void addTrack(Track track) {
		tracks.add(track);
	}

	public void addTracks(List<Track> tracks) {
		this.tracks.addAll(tracks);
	}

	@Override
	public boolean hasNext() {
		if (indexOfCurrentTrack <= tracks.size() - 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Track next() {
		return tracks.get(indexOfCurrentTrack++);
	}

	@Override
	public void remove() {
		throw new RuntimeException("not supported");
	}

}
