package io.captainsly.noteviewer.ui.components.dialogs;

import java.io.File;

import io.captainsly.noteviewer.ui.NoteUi;
import io.captainsly.noteviewer.utils.IniHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class SettingsDialog {

	public void show(NoteUi note) {
		Stage stage = new Stage();
		BorderPane bp = new BorderPane();
		Scene scene = new Scene(bp, 730, 320);

		Button chooseButton = new Button();
		Button cancelButton = new Button();
		Button acceptButton = new Button();

		GridPane grid = new GridPane();
		GridPane grid2 = new GridPane();

		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setHgap(20);
		grid.setVgap(20);
		grid2.setPadding(new Insets(10, 10, 10, 10));
		grid2.setHgap(20);
		grid2.setVgap(20);
		
		Label currentDirLabel = new Label();
		try {
			currentDirLabel.setText(IniHandler.readKeyString("SYSTEM", "DIRECTORY"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		TextField newDir = new TextField();
		newDir.setEditable(false);

		chooseButton.setText("...");
		chooseButton.setOnAction(e -> {
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setTitle("Choose Working Directory");
			File selectedDirectory = chooser.showDialog(stage);
			newDir.setText(selectedDirectory.getAbsolutePath());
		});

		acceptButton.setText("Okay");
		acceptButton.setOnAction(e -> {
			try {
				IniHandler.writekey("SYSTEM", "DIRECTORY", newDir.getText() != null ? newDir.getText() : currentDirLabel.getText());
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			note.getStatusBar().setText("Changed Working Directory to: " + newDir.getText());
			stage.close();
		});
		
		cancelButton.setText("Cancel");
		cancelButton.setOnAction(e -> stage.close());

		grid.add(currentDirLabel, 0, 0);
		grid.add(newDir, 2, 0);
		grid.add(chooseButton, 4, 0);

		grid2.add(acceptButton, 0, 0);
		grid2.add(cancelButton, 1, 0);
		
		bp.setCenter(grid);
		bp.setBottom(grid2);

		stage.setScene(scene);
		stage.setTitle("Settings");
		stage.getIcons().addAll(new Image("icon-16.png"), new Image("icon-24.png"), new Image("icon.png"), new Image("icon-96.png"));
		stage.show();
	}

}
