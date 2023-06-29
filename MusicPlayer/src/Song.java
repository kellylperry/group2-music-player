import java.io.Serializable;

public class Song implements Serializable {

    private static final long serialVersionUID = 1L;
    private String title;
    private String artist;
    private String path;

    public Song(String title, String artist, String path) {
        if(title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if(artist == null || artist.isEmpty()) {
            throw new IllegalArgumentException("Artist cannot be null or empty");
        }
        if(path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        
        this.title = title;
        this.artist = artist;
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return title + " - " + artist;
    }
}

