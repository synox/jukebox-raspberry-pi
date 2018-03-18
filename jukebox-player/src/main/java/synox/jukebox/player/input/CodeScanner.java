package synox.jukebox.player.input;

public interface CodeScanner {

	public abstract void addCodeListener(CodeInputListener listener);

	public abstract void start();

}