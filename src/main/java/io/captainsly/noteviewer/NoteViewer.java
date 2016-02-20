package io.captainsly.noteviewer;

import java.io.File;
import java.io.PrintWriter;

import io.captainsly.noteviewer.ui.NoteUi;
import io.captainsly.noteviewer.utils.IniHandler;
import javafx.application.Application;

public class NoteViewer {
	
	private static final File file = new File("config.ini");
	
	public static void main(String[] args) {
		if (!file.exists()) createIni();
		else Application.launch(NoteUi.class, args);
	}
	
	public static void createIni() {
		try {
			File ini = new File("config.ini");
			
			PrintWriter writer = new PrintWriter(ini);
			writer.write("");
			writer.close();
			IniHandler.writekey("WINDOW", "WIDTH", 800);
			IniHandler.writekey("WINDOW", "HEIGHT", 600);
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}

}
