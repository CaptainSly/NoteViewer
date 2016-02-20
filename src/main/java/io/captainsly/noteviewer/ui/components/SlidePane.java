package io.captainsly.noteviewer.ui.components;

import io.captainsly.noteviewer.ui.NoteUi;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class SlidePane extends BorderPane {
	
	private SlidePaneContent content;
	
	public SlidePane(NoteUi note) {
		Button btnHide = new Button();
		btnHide.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		btnHide.setOnAction((event) -> note.doSlide(false));
		
		content = new SlidePaneContent(widthProperty(), btnHide.widthProperty());
		
		setCenter(content);
		
		setLeft(btnHide);
		
	}
	
}
