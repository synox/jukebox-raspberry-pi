package synox.jukebox.player;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import synox.jukebox.model.Track;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class JavazoomPlayer {

	private Playlist currentPlaylist = Playlist.emptyPlaylist();
	private final PlaybackListener javazoomPlayListener = new PlaybackListener() {
		@Override
		public void playbackFinished(PlaybackEvent evt) {
			if (currentPlaylist.hasNext()) {
				playNextTrack(currentPlaylist);
			} else {
				firePlaybackFinished();
			}
		}
	};
	private final List<JukeboxPlaybackListener> listeners = new ArrayList<>();

	private AdvancedPlayer player = null;

	public void addPlaybackListener(JukeboxPlaybackListener listener) {
		this.listeners.add(listener);
	}

	public void play(Playlist playlist) {
		this.currentPlaylist = playlist;
		playNextTrack(currentPlaylist);
	}

	public void playNextTrack(Playlist playlist) {
		if (playlist.hasNext()) {
			Track track = playlist.next();
			playTrackAsync(track);
			fireStartedSong(track);
		}
	}

	private void firePlaybackFinished() {
		for (JukeboxPlaybackListener listener : listeners) {
			listener.playbackFinished();
		}
	}

	private void fireStartedSong(Track track) {
		for (JukeboxPlaybackListener listener : listeners) {
			listener.nextSong(track);
		}
	}

	private void playTrackAsync(final Track nextTrack) {
		Runnable action = new Runnable() {
			@Override
			public void run() {
				try {
					if (player != null) {
						player.close();
					}
					player = new AdvancedPlayer(Files.newInputStream(nextTrack.file));
					player.setPlayBackListener(javazoomPlayListener);
					player.play();
				} catch (JavaLayerException | IOException e) {
					throw new JukeboxPlayerException(e);
				}
			}
		};
		new Thread(action).start();
	}
}