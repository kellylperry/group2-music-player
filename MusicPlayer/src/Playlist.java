import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;


public class Playlist implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
    private List<Song> songs;
    private static HashSet<String> existingNames = new HashSet<>();

    public Playlist(String name) throws Exception {
        if (existingNames.contains(name)) {
            throw new Exception("Playlist name already exists!");
        }
        existingNames.add(name);
        this.name = name;
        this.songs = new ArrayList<>();
    }
    public void addSong(Song song) {
        songs.add(song);
    }
    
    public void removeSong(Song song) {
        if (!songs.contains(song)) {
            throw new IllegalArgumentException("Song not found in the playlist");
        }
        songs.remove(song);
    }
    
    public void renamePlaylist(String newName) throws Exception {
        if (existingNames.contains(newName)) {
            throw new Exception("Playlist name already exists!");
        }
        // Remove old name and add new name
        existingNames.remove(this.name);
        existingNames.add(newName);
        this.name = newName;
    }

    
    public void moveSongUp(Song song) {
        int index = songs.indexOf(song);
        if (index == -1) {
            throw new IllegalArgumentException("Song not found in the playlist");
        }
        if (index > 0) {
            songs.remove(index);
            songs.add(index - 1, song);
        }
    }

    public void moveSongDown(Song song) {
        int index = songs.indexOf(song);
        if (index == -1) {
            throw new IllegalArgumentException("Song not found in the playlist");
        }
        if (index < songs.size() - 1) {
            songs.remove(index);
            songs.add(index + 1, song);
        }
    }



    public List<Song> getSongs() {
        return songs;
    }

    public String getName() {
        return name;
    }
}

