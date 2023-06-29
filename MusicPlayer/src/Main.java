import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.File;
import javafx.geometry.Insets;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.EqualizerBand;
import javafx.geometry.Orientation;
import javafx.collections.MapChangeListener;





public class Main extends Application {

    // Declare MediaPlayer instance at class level
	private MediaPlayer mediaPlayer;
    private Playlist currentPlaylist;
    private ListView<Song> songList = new ListView<>();
    private int songListIndex = 0;
    private Label titleLabel = new Label();
    private Label artistLabel = new Label();
    private Label albumLabel = new Label();    
    private Slider progressBar;



    // Launch the application
    public static void main(String[] args) {
        launch(args);
    }
    
    private void populateSongList(File directory) {
        // Get a list of all the music files in the selected directory
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".mp3"));

        if (files != null) {
            // Create a new Song object for each file and add it to the songList
            for (File file : files) {
                Song song = new Song(file.getName(), "Unknown Artist", file.getPath());
                songList.getItems().add(song);
            }
        }
    }  

    
    private void setupMediaPlayer(Song song) {
        if(song == null || song.getPath() == null){
            System.out.println("Song or Song path is null"); // Or use a logger to log the error
            return;
        }

        Media sound = new Media(new File(song.getPath()).toURI().toString());
        
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }
        
        mediaPlayer = new MediaPlayer(sound);

        if (mediaPlayer != null) {
            // Add the action that should happen at the end of the media
            mediaPlayer.setOnEndOfMedia(() -> {
                songListIndex++;  // Move to the next song in the list
                if (songListIndex >= songList.getItems().size()) {
                    songListIndex = 0;  // Wrap around to the start of the list
                }
                Song nextSong = songList.getItems().get(songListIndex);
                setupMediaPlayer(nextSong);  // Play the next song
            });
        } else {
            System.out.println("MediaPlayer is not initialized properly"); // Or use a logger to log the error
        }

        sound.getMetadata().addListener((MapChangeListener.Change<? extends String, ? extends Object> c) -> {
            if (c.wasAdded()) {
                if ("title".equals(c.getKey())) {
                    titleLabel.setText("Title: " + c.getValueAdded());
                } else if ("artist".equals(c.getKey())) {
                    artistLabel.setText("Artist: " + c.getValueAdded());
                } else if ("album".equals(c.getKey())) {
                    albumLabel.setText("Album: " + c.getValueAdded());
                }
            }
        });
        
        // Update the progress bar as the song progresses
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!progressBar.isValueChanging()) {  // Don't update if user is dragging the slider knob
                progressBar.setValue(newTime.toSeconds() / mediaPlayer.getTotalDuration().toSeconds() * 100);
            }
        });
        
        // Change song position when user drags the slider knob
        progressBar.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                mediaPlayer.seek(Duration.seconds(progressBar.getValue() / 100 * mediaPlayer.getTotalDuration().toSeconds()));
            }
        });
    }

    
    private Slider createVolumeSlider() {
        Slider volumeSlider = new Slider(0, 100, 50); // start value, end value, initial value
        volumeSlider.setOrientation(Orientation.HORIZONTAL);
        volumeSlider.setPrefWidth(70);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                try {
                    if (mediaPlayer != null) {
                        double volume = new_val.doubleValue() / 100; // scale from 0 to 1
                        mediaPlayer.setVolume(volume);
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    // Show an alert dialog.
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Could not adjust volume.");
                    alert.setContentText("An error occurred while trying to adjust the volume: " + e.getMessage());
                    alert.showAndWait();
                }
            }
        });

        return volumeSlider;
    }





    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set the title of the window
        primaryStage.setTitle("Music Player");

        // Create a new BorderPane layout
        BorderPane layout = new BorderPane();
        
        Label titleLabel = new Label();
        Label artistLabel = new Label();
        Label albumLabel = new Label();
        VBox metadataBox = new VBox(10, titleLabel, artistLabel, albumLabel);
        layout = new BorderPane();
        songList = new ListView<>();
        
        progressBar = new Slider();
        progressBar.setMin(0);
        progressBar.setMax(100);
        
       

        
        

        // Create play, pause and stop buttons
        Button playButton = new Button("Play");
        Button pauseButton = new Button("Pause");
        Button stopButton = new Button("Stop");
        Button addSongButton = new Button("Add Song");
        Button newPlaylistButton = new Button("New Playlist");
        Button loadPlaylistButton = new Button("Load Playlist");
        Button savePlaylistButton = new Button("Save Playlist");
        Button removeSongButton = new Button("Remove Song");
        Button moveSongUpButton = new Button("Move Song Up");
        Button moveSongDownButton = new Button("Move Song Down");
        Button selectDirectoryButton = new Button("Select Directory");
        Button shuffleButton = new Button("Shuffle");
        Button repeatButton = new Button("Repeat");
        Button equalizerButton = new Button("Equalizer");
        
        layout.setLeft(metadataBox);
        
        
       

        // Set the action for the play button
     // Play button
        playButton.setOnAction(event -> {
            try {
                mediaPlayer.play();
            } catch (Exception e) {
                e.printStackTrace();

                // Show an alert dialog
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Could not play song.");
                alert.setContentText("An error occurred while trying to play the song: " + e.getMessage());
                alert.showAndWait();
            }
        });


     // Pause button
        pauseButton.setOnAction(event -> {
            try {
                mediaPlayer.pause();
            } catch (Exception e) {
                e.printStackTrace();

                // Show an alert dialog
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Could not pause song.");
                alert.setContentText("An error occurred while trying to pause the song: " + e.getMessage());
                alert.showAndWait();
            }
        });


     // Stop button
        stopButton.setOnAction(event -> {
            try {
                mediaPlayer.stop();
            } catch (Exception e) {
                e.printStackTrace();

                // Show an alert dialog
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Could not stop song.");
                alert.setContentText("An error occurred while trying to stop the song: " + e.getMessage());
                alert.showAndWait();
            }
        });

        // add song
        addSongButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null && currentPlaylist != null) {
                TextInputDialog dialog = new TextInputDialog("Unknown Artist");
                dialog.setTitle("Add Song");
                dialog.setHeaderText("Enter the Artist's Name");
                dialog.setContentText("Artist:");
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    try {
                        Song song = new Song(file.getName(), result.get(), file.getPath());
                        currentPlaylist.addSong(song);
                        songList.getItems().add(song);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Show an alert dialog
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Could not add song.");
                        alert.setContentText("An error occurred while trying to add the song: " + e.getMessage());
                        alert.showAndWait();
                    }
                }
            }
        });




     // New Playlist button
        newPlaylistButton.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog("New Playlist");
            dialog.setTitle("New Playlist");
            dialog.setHeaderText("Enter a name for your new playlist:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()){
                try {
                    currentPlaylist = new Playlist(result.get());
                    songList.getItems().clear();
                } catch (Exception e) {
                    e.printStackTrace();

                    // Show an alert dialog
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Could not create a new playlist.");
                    alert.setContentText("An error occurred while trying to create a new playlist: " + e.getMessage());
                    alert.showAndWait();
                }
            }
        });


        // Load Playlist button
        loadPlaylistButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    FileInputStream fis = new FileInputStream(file.getPath());
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    currentPlaylist = (Playlist) ois.readObject();
                    songList.getItems().setAll(currentPlaylist.getSongs());
                    ois.close();
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    
                 // Show an alert dialog
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Could not load playlist.");
                    alert.setContentText("An error occurred while trying to load the playlist: " + e.getMessage());
                    alert.showAndWait();
                }
            }
        });
        
     // Save Playlist button
        savePlaylistButton.setOnAction(event -> {
            if (currentPlaylist != null) {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showSaveDialog(primaryStage);
                if (file != null) {
                    try {
                        FileOutputStream fos = new FileOutputStream(file.getPath());
                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                        oos.writeObject(currentPlaylist);
                        oos.close();
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();

                        // Show an alert dialog
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Could not save playlist.");
                        alert.setContentText("An error occurred while trying to save the playlist: " + e.getMessage());
                        alert.showAndWait();
                    }
                }
            }
        });
        
     // Remove Song button
        removeSongButton.setOnAction(event -> {
            Song selectedSong = songList.getSelectionModel().getSelectedItem();
            if (selectedSong != null && currentPlaylist != null) {
                try {
                    currentPlaylist.removeSong(selectedSong);
                    songList.getItems().remove(selectedSong);
                } catch (Exception e) {
                    e.printStackTrace();

                    // Show an alert dialog
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Could not remove song.");
                    alert.setContentText("An error occurred while trying to remove the song: " + e.getMessage());
                    alert.showAndWait();
                }
            }
        });


     // Move Song Up button
        moveSongUpButton.setOnAction(event -> {
            Song selectedSong = songList.getSelectionModel().getSelectedItem();
            if (selectedSong != null && currentPlaylist != null) {
                try {
                    currentPlaylist.moveSongUp(selectedSong);
                    songList.getItems().setAll(currentPlaylist.getSongs());
                } catch (Exception e) {
                    e.printStackTrace();

                    // Show an alert dialog
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Could not move song up.");
                    alert.setContentText("An error occurred while trying to move the song up: " + e.getMessage());
                    alert.showAndWait();
                }
            }
        });


     // Move Song Down button
        moveSongDownButton.setOnAction(event -> {
            Song selectedSong = songList.getSelectionModel().getSelectedItem();
            if (selectedSong != null && currentPlaylist != null) {
                try {
                    currentPlaylist.moveSongDown(selectedSong);
                    songList.getItems().setAll(currentPlaylist.getSongs());
                } catch (Exception e) {
                    e.printStackTrace();

                    // Show an alert dialog
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Could not move song down.");
                    alert.setContentText("An error occurred while trying to move the song down: " + e.getMessage());
                    alert.showAndWait();
                }
            }
        });

        
     // Select Directory button action handler
        selectDirectoryButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Music Directory");

            // Optionally, you can set an initial directory, so that the dialog opens there.
            // directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            File selectedDirectory = directoryChooser.showDialog(primaryStage);

            if (selectedDirectory != null) {
                // Clear the previous song list
                songList.getItems().clear();

                // Populate the song list using the selected directory
                populateSongList(selectedDirectory);
            } else {
                System.out.println("No directory selected");
            }
        });

        
        shuffleButton.setOnAction(event -> {
            FXCollections.shuffle(songList.getItems());
            // Reset the MediaPlayer with the new shuffled list
            // Assuming 'songListIndex' is the index of the current song being played
            Song currentSong = songList.getItems().get(songListIndex);
            setupMediaPlayer(currentSong);
            mediaPlayer.play();
        });
        
     // Repeat status
        AtomicBoolean isRepeat = new AtomicBoolean(false);

        repeatButton.setOnAction(event -> {
            isRepeat.set(!isRepeat.get());  // Toggle the repeat status
        });

     // Assume the song list is not empty, and you want to start with the first song.
     // This would be the place to handle the case where the song list is empty, if necessary.
     if (!songList.getItems().isEmpty()) {
         setupMediaPlayer(songList.getItems().get(0));
     }

     // Now you can safely set the end-of-media action on mediaPlayer
     mediaPlayer.setOnEndOfMedia(() -> {
         if (isRepeat.get()) {
             mediaPlayer.seek(Duration.ZERO);
             mediaPlayer.play();
         } else {
             songListIndex++;  // Advance to the next song
             if (songListIndex < songList.getItems().size()) {
                 setupMediaPlayer(songList.getItems().get(songListIndex));
                 mediaPlayer.play();
             } else {
                 // You've reached the end of the song list, handle this case as you see fit.
                 // For example, you might loop back to the start of the list:
                 songListIndex = 0;
                 setupMediaPlayer(songList.getItems().get(songListIndex));
                 mediaPlayer.play();
             }
         }
     });
        
     // Equalizer button
        equalizerButton.setOnAction(event -> {
            try {
                Stage equalizerStage = new Stage();
                equalizerStage.setTitle("Equalizer");

                VBox vBox = new VBox(10);
                vBox.setPadding(new Insets(10, 10, 10, 10));

                // Add equalizer bands
                if (mediaPlayer != null) {
                    AudioEqualizer audioEqualizer = mediaPlayer.getAudioEqualizer();
                    for (EqualizerBand band : audioEqualizer.getBands()) {
                        Slider slider = new Slider(EqualizerBand.MIN_GAIN, EqualizerBand.MAX_GAIN, band.getGain());
                        slider.setOrientation(Orientation.VERTICAL);
                        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
                            band.setGain(newVal.doubleValue());
                        });
                        vBox.getChildren().add(slider);
                    }
                }

                Scene equalizerScene = new Scene(vBox, 400, 300);
                equalizerStage.setScene(equalizerScene);
                equalizerStage.show();
            } catch (Exception e) {
                e.printStackTrace();

                // Show an alert dialog
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Could not open equalizer.");
                alert.setContentText("An error occurred while trying to open the equalizer: " + e.getMessage());
                alert.showAndWait();
            }
        });
        
        





        // Create a horizontal box with 10px spacing to hold the buttons
        HBox controlBox = new HBox(10);
        // Add the buttons to the box
        controlBox.getChildren().addAll(playButton, pauseButton, stopButton, addSongButton, newPlaylistButton, loadPlaylistButton, savePlaylistButton, shuffleButton, repeatButton, equalizerButton, createVolumeSlider(), songList);
        
        
        VBox controlAndProgress = new VBox(10, controlBox, progressBar);
        layout.setBottom(controlAndProgress);

        // Create a list view to display the list of songs
        ListView<Song> songList = new ListView<>();
        // Create a text field to filter the songs
        TextField searchField = new TextField();

        // Set the search field at the top of the layout
        layout.setTop(searchField);
        // Set the list view in the center of the layout
        layout.setCenter(songList);
      

        // File path to the music file
        String musicFile = "path_to_music_file.mp3";     // Replace with your own file path

        // Load the media file
        try {
            // Create a new media object with the file
            Media sound = new Media(new File(musicFile).toURI().toString());
            // Create a new media player with the media
            mediaPlayer = new MediaPlayer(sound);
        } catch (Exception e) {
            // Print the stack trace and error message if file could not be loaded
            e.printStackTrace();
            System.out.println("Could not load music file: " + e.getMessage());
        }

        // Create a new scene with the layout and size
        Scene scene = new Scene(layout, 800, 600);
        // Set the scene on the stage
        primaryStage.setScene(scene);
        // Show the stage
        primaryStage.show();
    }
}

