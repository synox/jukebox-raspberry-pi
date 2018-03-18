package synox.jukebox.musicResolver;

public class MusicResolverException extends RuntimeException {

	public MusicResolverException(String string, Exception e) {
		super(string, e);
	}
}
