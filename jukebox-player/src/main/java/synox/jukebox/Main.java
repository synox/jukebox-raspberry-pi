package synox.jukebox;

import synox.jukebox.display.GpioControl;
import synox.jukebox.model.MediaKey;
import synox.jukebox.model.Track;
import synox.jukebox.musicResolver.MusicResolver;
import synox.jukebox.player.JavazoomPlayer;
import synox.jukebox.player.JukeboxPlaybackListener;
import synox.jukebox.player.Playlist;
import synox.jukebox.player.input.CodeInputListener;
import synox.jukebox.player.input.CodeScanner;
import synox.jukebox.player.input.QrCodeScanner;

import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {

        final MusicResolver musicLibrary = new MusicResolver(Paths.get("/home/pi/jukebox/music"));
        final JavazoomPlayer player = new JavazoomPlayer();
        final GpioControl userInfoPanel = new GpioControl();
        userInfoPanel.initialize();

        CodeScanner codeScanner = new QrCodeScanner();
        codeScanner.addCodeListener(new CodeInputListener() {
            @Override
            public void newCodeDetected(String code) {
                MediaKey key = MediaKey.parseCode(code);
                Playlist playlist = musicLibrary.findByCode(key);
                if (playlist != null) {
                    player.play(playlist);
                }
            }
        });

        player.addPlaybackListener(new JukeboxPlaybackListener() {
            @Override
            public void nextSong(Track next) {
                userInfoPanel.ledMultiPulse();
            }
        });

        // userInfoPanel.addButtonListener(new GpioPinListenerDigital() {
        // @Override
        // public void
        // handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent
        // event) {
        // System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() +
        // " = " + event.getState());
        // // TODO: does not work yet
        // player.playNext();
        // }
        // });

        codeScanner.start();
    }

}
