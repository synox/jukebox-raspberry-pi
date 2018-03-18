package synox.jukebox.musicResolver;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import synox.jukebox.model.MediaKey;
import synox.jukebox.model.Track;
import synox.jukebox.player.Playlist;

public class MusicResolver {

	private static final List<String> ACCEPTED_EXTENSTIONS = Arrays.asList(".mp3", ".aif", ".m4a", ".m4p");
	private static final List<String> BLOCKED_DIRECTORIES = Arrays.asList("iTunes");

	private static final Comparator<Path> ORDER_BY_FILENAME = new FilenameComparator();

	private final Path basePath;

	public MusicResolver(Path basePath) {
		this.basePath = basePath;
	}

	public Playlist findByCode(MediaKey key) {
		try {
			Playlist playlist = Playlist.emptyPlaylist();
			Path dirOrFile = findFuzzy(key);
			if (dirOrFile != null) {
				if (Files.isDirectory(dirOrFile)) {
					List<Track> tracks = getTracksInDirectory(dirOrFile);
					playlist.addTracks(tracks);
				} else {
					// is file
					playlist.addTrack(new Track(dirOrFile));
				}
			}
			return playlist;
		} catch (IOException e) {
			throw new MusicResolverException("can not resolve music", e);
		}
	}

	/**
	 * Returns the deepest path. This ensures that the folder with most matches
	 * is chosen. e.g. with matching album and matching arist.
	 *
	 * @param matchedPaths
	 *            list of partial matches
	 * @return the deepest path
	 */
	private Path getDeepestPath(List<Path> matchedPaths) {
		if (matchedPaths.isEmpty()) {
			return null;
		}
		Collections.sort(matchedPaths, new PathDepthComparator());
		return matchedPaths.get(matchedPaths.size() - 1);
	}

	private List<Track> getTracksInDirectory(Path dirOrFile) throws IOException {
		List<Path> files = new ArrayList<>();
		List<Track> tracks = new ArrayList<>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dirOrFile)) {
			for (Path file : directoryStream) {
				files.add(file);
			}
		}
		Collections.sort(files, ORDER_BY_FILENAME);
		for (Path path : files) {
			tracks.add(new Track(path));
		}
		return tracks;
	}

	private boolean isBlockedDir(Path dir) {
		Path fileName = dir.getFileName();
		return BLOCKED_DIRECTORIES.contains(fileName.toString());
	}

	private boolean isMusicFile(Path file) {
		String filenameLowercase = file.getFileName().toString().toLowerCase();
		for (String ext : ACCEPTED_EXTENSTIONS) {
			if (filenameLowercase.endsWith(ext.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	private boolean matchesAlbumOrArtist(Path dir, MediaKey key) {
		String dirname = dir.getFileName().toString();

		if (!isEmpty(key.album)) {
			if (dirname.startsWith(key.album) || key.album.startsWith(dirname)) {
				return true;
			}
		}
		if (!isEmpty(key.artist)) {
			if (dirname.startsWith(key.artist) || key.artist.startsWith(dirname)) {
				return true;
			}
		}
		return false;
	}

	private boolean matchesTrack(Path file, MediaKey key) {
		String filename = file.getFileName().toString();

		// ignore extension
		filename = filename.replaceFirst("[.][^.]+$", "");

		// ignore leading numbering
		filename = filename.replaceFirst("^[0-9]+[ ]+", "");

		if (!isMusicFile(file)) {
			return false;
		}

		if (!isEmpty(key.song)) {
			if (filename.contains(key.song) || key.song.contains(filename)) {
				return true;
			}
		}
		return false;
	}

	protected boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}

	Path findFuzzy(final MediaKey key) {
		try {
			final List<Path> matchedPaths = new ArrayList<>();

			Files.walkFileTree(basePath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					if (dir.equals(basePath)) {
						// scan subtree of base
						return FileVisitResult.CONTINUE;
					}
					if (isBlockedDir(dir)) {
						// ignore blocked dir and subdirs
						return FileVisitResult.SKIP_SUBTREE;
					}

					if (matchesAlbumOrArtist(dir, key)) {
						matchedPaths.add(dir);
						return FileVisitResult.CONTINUE;
					}

					if (dir.getFileName().toString().equalsIgnoreCase("Compilations")) {
						// always scan compilations subtrees
						return FileVisitResult.CONTINUE;
					}

					return FileVisitResult.SKIP_SUBTREE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (matchesTrack(file, key)) {
						matchedPaths.add(file);
					}

					return super.visitFile(file, attrs);
				}
			});

			return getDeepestPath(matchedPaths);
		} catch (IOException e) {
			throw new MusicResolverException("can not walk file tree", e);
		}
	}

}
