package io.captainsly.noteviewer.ui.components.dialogs;

import io.captainsly.noteviewer.ui.NoteUi;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AboutDialog {

	private Alert alert;
	
	public AboutDialog() {	
		alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About");
		alert.setHeaderText("About NoteViewer");
		alert.setContentText("NoteViewer\nVersion: " + NoteUi.VERSION + "\nIcons: icon8.com");
	}
	
	public void showAndWait() {
		alert.showAndWait();
	}
	
}
