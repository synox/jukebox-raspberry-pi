package synox.jukebox.musicResolver;

import java.nio.file.Path;
import java.util.Comparator;

final class PathDepthComparator implements Comparator<Path> {
	@Override
	public int compare(Path o1, Path o2) {
		return Integer.valueOf(o1.getNameCount()).compareTo(o2.getNameCount());
	}
}