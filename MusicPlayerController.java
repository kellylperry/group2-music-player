import java.awt.Button;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MusicPlayerController implements Initializable	{
	
	@FXML
	private Pane pane;
	private Label songLabel;
	private Button buttonPlay, buttonNext, buttonOpen;
	@FXML
	private Slider sliderVolume, songProgressBar;
	
	private ListView songList;
	private Media media;
	private MediaPlayer mediaPlayer;
	private File directory;
	private File[] files;
	private ArrayList<File> songs;
	
	private int songNumber=0;
	private Timer timer;
	private TimerTask task;
	private boolean isRunning, isRepeat, isShuffle;
	
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		songs = new ArrayList<File>();
		directory = new File("music");
		files = directory.listFiles();
		if(files != null)	{
			for(File file : files)	{
				songs.add(file);
			}
		}
		media = new Media(songs.get(songNumber).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		
		//songLabel.setText(songs.get(songNumber).getName());
		
		sliderVolume.valueProperty().addListener(new ChangeListener<Number>()	{
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				mediaPlayer.setVolume(sliderVolume.getValue() * 0.01);
			}
			
		});
		songProgressBar.valueProperty().addListener(new ChangeListener<Number>()	{
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				songProgressBar.setOnMouseClicked(e -> mediaPlayer.seek(javafx.util.Duration.seconds(songProgressBar.getValue())));
			}
			
		});
		
	}
	public void timerBegin()	{
		timer = new Timer();
		task = new TimerTask() 	{
			public void run()	{
				isRunning = true;
				double current = mediaPlayer.getCurrentTime().toSeconds();
				double end = media.getDuration().toSeconds();
				songProgressBar.setValue(current);
				if(current/end == 1)	{
					timerCancel();
					mediaPlayer.stop();
					buttonNextClicked();
				}
			}
		};
		timer.scheduleAtFixedRate(task, 0, 1000);
	}
	public void timerCancel()	{
		isRunning = false;
		timer.cancel();
	}
	
	public void buttonPlayClicked()	{
		timerBegin();
		mediaPlayer.setVolume(sliderVolume.getValue() * 0.01);
		mediaPlayer.play();
	}
	public void buttonPauseClicked()	{
		timerCancel();
		mediaPlayer.pause();
	}
	public void playSong()	{
			mediaPlayer.stop();
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
		
	}
    public void buttonNextClicked()	{
    	timerCancel();
    	timerBegin();
    	if(songNumber < songs.size() -1)	{
			songNumber++;
		}
    	else {
    		songNumber=0;
    	}
    	playSong();
    	buttonPlayClicked();
    }
    public void buttonPreviousClicked()	{
    	timerCancel();
    	timerBegin();
    	if(songNumber == 0)	{
			songNumber=songs.size()-1;
		}
    	else {
    		songNumber--;
    	}
    	playSong();
    	buttonPlayClicked();
    }
    public void buttonOpenClicked()	{
    	openFile();
    }
    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Media Files", "*.mp4", "*.m4a", "*.mp3");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(null);
        songs.add(file);
    }
} 