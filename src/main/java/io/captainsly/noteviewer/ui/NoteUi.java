package io.captainsly.noteviewer.ui;

import java.io.File;
import java.io.FileWriter;

import org.controlsfx.control.StatusBar;

import io.captainsly.noteviewer.ui.components.FileTreeItem;
import io.captainsly.noteviewer.ui.components.editor.Editor;
import io.captainsly.noteviewer.ui.components.panes.SlidePane;
import io.captainsly.noteviewer.utils.IniHandler;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NoteUi extends Application {

	private int	I_WIDTH;
	private int	I_HEIGHT;

	private Stage		stage;
	private SlidePane	slidePane;
	private SplitPane	splitPane;
	private StatusBar	statusBar;
	private BorderPane	root;
	private GridPane	grid;
	private Editor		text;
	private Label		pathLabel;

	@Override
	public void init() {
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
		stage = primaryStage;
		root = new BorderPane();
		grid = new GridPane();
		splitPane = new SplitPane();
		statusBar = new StatusBar();
		text = new Editor();
		pathLabel = new Label();

		MenuBar menuBar = new MenuBar();
		Menu menuSettings = new Menu();
		Menu menuFile = new Menu();
		MenuItem itemSettings = new MenuItem();
		MenuItem itemAbout = new MenuItem();
		MenuItem itemSave = new MenuItem();
		MenuItem itemExit = new MenuItem();
		Button showPane = new Button();
		Scene scene = new Scene(root, I_WIDTH, I_HEIGHT);

		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setHgap(10);
		grid.setVgap(10);

		root.setTop(grid);
		root.setCenter(splitPane);
		root.setBottom(statusBar);

		statusBar.setText("Loaded Directory: " + IniHandler.readKeyString("SYSTEM", "DIRECTORY"));
		pathLabel.setText(IniHandler.readKeyString("SYSTEM", "DIRECTORY"));

		menuFile.setText("File");
		menuSettings.setText("Settings");

		itemSave.setText("Save");
		itemExit.setText("Exit");

		itemSettings.setText("Settings");
		itemAbout.setText("About");

		menuFile.getItems().addAll(itemSave, itemExit);
		menuSettings.getItems().addAll(itemSettings, itemAbout);

		menuBar.getMenus().addAll(menuFile, menuSettings);

		try {
			if (IniHandler.readKeyString("SYSTEM", "DIRECTORY") == null) {
				DirectoryChooser chooser = new DirectoryChooser();
				chooser.setTitle("Choose Working Directory");
				File selectedDirectory = chooser.showDialog(stage);
				if (selectedDirectory == null) selectedDirectory = chooser.showDialog(stage);
				else IniHandler.writekey("SYSTEM", "DIRECTORY", selectedDirectory.getAbsolutePath());
				pathLabel.setText(selectedDirectory.getAbsolutePath());
				statusBar.setText("Loaded Directory: " + selectedDirectory.getAbsolutePath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Refreshes the Path label
		pathLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() != null && event.getButton() == MouseButton.SECONDARY) try {
					pathLabel.setText(IniHandler.readKeyString("SYSTEM", "DIRECTORY"));
					statusBar.setText("Refreshed label");
				} catch (Exception e) {
					e.printStackTrace();
					statusBar.setText("Somehow, you've made the most simplest of methods crash... Congratulations, this is impressive.");
				}
			}

		});

		itemSave.setOnAction(evt -> {

			String d = text.getText();
			FileChooser chooser = new FileChooser();
			ExtensionFilter extFilter = new ExtensionFilter("Text Files (*.txt)", "*.txt");
			chooser.getExtensionFilters().add(extFilter);
			try {
				chooser.setInitialDirectory(new File(IniHandler.readKeyString("SYSTEM", "DIRECTORY")));
				File file = chooser.showSaveDialog(getStage());
				FileWriter writer = new FileWriter(file + ".txt");
				writer.write(d);
				writer.close();
				statusBar.setText("Saved file at location: " + file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
				statusBar.setText("It seems that saving has failed, or you may have closed the dialog box.");
			}
		}

		);

		itemExit.setOnAction(evt -> {
			stage.close();
		});

		showPane.setGraphic(new ImageView(new Image("open.png")));
		showPane.setOnAction((event) -> {
			openSlidePane();
			statusBar.setText("Opening File Tree");
		});

		grid.add(menuBar, 0, 0);
		grid.add(showPane, 2, 0);
		grid.add(pathLabel, 4, 0);

		String path = IniHandler.readKeyString("SYSTEM", "DIRECTORY");
		TreeView<File> fileView = new TreeView<File>(new FileTreeItem(new File(path)));

		slidePane = new SlidePane(this, fileView);

		fileView.setOnMouseClicked((event) -> {
			if (event.getButton() != null && event.getButton() == MouseButton.MIDDLE) {
				String d = text.getText();
				splitPane.getItems().removeAll(text, slidePane);
				splitPane.getItems().addAll(text, slidePane);
				text.setText(d);
				statusBar.setText("Refreshed file tree");
			}

			if (event.getClickCount() > 42) {
				statusBar.setText("The Answer to life, the universe, and everything");
			}
		});

		splitPane.getItems().addAll(text, slidePane);

		stage.setScene(scene);
		stage.getIcons().addAll(new Image("icon-16.png"), new Image("icon-24.png"), new Image("icon.png"), new Image("icon-96.png"));
		stage.setTitle("A BETTER Version of Chris' Note program");
		stage.show();
	}

	public void doSlide(boolean doOpen) {
		KeyValue positionKeyValue = new KeyValue(splitPane.getDividers().get(0).positionProperty(), doOpen ? 0.45 : 1);
		KeyValue opacitykeyValue = new KeyValue(slidePane.opacityProperty(), doOpen ? 1 : 0);
		KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.1), positionKeyValue, opacitykeyValue);
		Timeline timeline = new Timeline(keyFrame);
		timeline.setOnFinished(evt -> {
			if (!doOpen) {
				splitPane.getItems().remove(slidePane);
				slidePane.setOpacity(1);
			}
		});
		timeline.play();
	}

	public void openSlidePane() {
		if (!splitPane.getItems().contains(slidePane)) {
			splitPane.getItems().add(slidePane);
			doSlide(true);
		}
	}

	public Stage getStage() {
		return stage;
	}

	public void setText(String dtext) {
		text.setText(dtext);
	}

	public void setLabelText(String text) {
		pathLabel.setText(text);
	}

	public StatusBar getStatusBar() {
		return statusBar;
	}

}
