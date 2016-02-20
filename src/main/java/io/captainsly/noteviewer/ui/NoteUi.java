package io.captainsly.noteviewer.ui;

import io.captainsly.noteviewer.ui.components.SlidePane;
import io.captainsly.noteviewer.utils.IniHandler;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NoteUi extends Application {

	private int	I_WIDTH;
	private int	I_HEIGHT;

	private SplitPane	splitPane;
	private SlidePane	slidePane;
	private BorderPane	root;
	private GridPane	grid;

	@Override
	public void init() {
		slidePane = new SlidePane(this);

		try {

			int i = IniHandler.readKeyInt("WINDOW", "WIDTH");
			int d = IniHandler.readKeyInt("WINDOW", "HEIGHT");

			if (i != 0) I_WIDTH = i;
			else I_WIDTH = 800;

			if (d != 0) I_HEIGHT = d;
			else I_HEIGHT = 600;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		root = new BorderPane();
		grid = new GridPane();
		splitPane = new SplitPane();
		Scene scene = new Scene(root, I_WIDTH, I_HEIGHT);

		root.setTop(grid);
		root.setCenter(splitPane);

		primaryStage.setScene(scene);
		primaryStage.setTitle("A BETTER Version of Chris' Note program");
		primaryStage.show();
	}

	public void doSlide(boolean doOpen) {
		KeyValue positionKeyValue = new KeyValue(splitPane.getDividers().get(0).positionProperty(), doOpen ? 0.15 : 1);
		KeyValue opacityKeyValue = new KeyValue(slidePane.opacityProperty(), doOpen ? 1 : 0);
		KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.1), positionKeyValue, opacityKeyValue);
		Timeline timeline = new Timeline(keyFrame);
		timeline.setOnFinished(evt -> {
			if (!doOpen) {
				splitPane.getItems().remove(slidePane);
				slidePane.setOpacity(1);
			}
		});
		timeline.play();
	}

	public SlidePane getSlidePane() {
		return slidePane;
	}
	
}
