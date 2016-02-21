package io.captainsly.noteviewer.ui.components.panes;

import java.io.File;

import io.captainsly.noteviewer.ui.NoteUi;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class SlidePane extends BorderPane {

	public SlidePane(NoteUi note, TreeView<File> treeView) {
		
		final Button btnHide = new Button();
		btnHide.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		btnHide.setGraphic(new ImageView(new Image("close.png")));
		btnHide.setOnAction((event) ->{ 
			note.doSlide(false);
			note.getStatusBar().setText("Closing File Tree");
		});
	
		StackPane stack = new StackPane();
		stack.getChildren().add(treeView);
		
		setCenter(stack);
		setLeft(btnHide);
		
	}
}
