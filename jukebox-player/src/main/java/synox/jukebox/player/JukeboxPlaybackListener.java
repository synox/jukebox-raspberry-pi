package synox.jukebox.player;

import synox.jukebox.model.Track;

public abstract class JukeboxPlaybackListener {
	public void playbackFinished() {
	}

	public void nextSong(Track track) {
	}
}
