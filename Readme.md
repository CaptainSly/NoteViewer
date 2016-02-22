# NoteViewer

A *BETTER* version of a friend's batch note viewer, allows you to view and edit notes, you have inside of a directory that you specify in the config. 


Version 1.0

 ![Fig.2](readmeimages/screenshot-02-main.png)


Total Lines of Code: 1678

Total Number of Classes: 10

Total Fucks Given: -4

----------------------------------------------------

# Releases

Current Release is a tech [demo](https://github.com/CaptainSly/NoteViewer/releases). 

# Dependencies

NoteViewer utilizes the following dependencies

1. [Commons Lang](https://commons.apache.org/proper/commons-lang) - Provides helper utilities for the java.lang API, most notably String manipulation
2. [RichTextFx](https://www.github.com/TomasMikula/RichTextFX)
3. [LiveDirsFx](https://www.github.com/TomasMikula/LiveDirsFX)
4. [Ini4J](https://ini4j.sourceforge.net)


# Features
- File Tree
2. Text Editor
3. Ability to close/open File Tree at ease
4. Ability to switch working directory, and other settings (Yet to be implemented)
5. Added Easter Eggs (Total: 2)

# Known Bugs
- Can't save formatted work | See possible fixes for more info

# TODO/Planned
	TODO
	
# Usage

If NoteViewer fails to open a file, it could be that it's either a directory or not a text document. Make sure your files are named correctly, this doesn't mean that an incorrectly named file won't open, but it lessens the chance of a crash.

# Building
NoteViewer uses gradle as the build tool. Use `gradle build` to buid NoteViewer, results can be found at `build/distributions/NoteViewer`, the app is fixed, so you *should* be able to use it to launch the program.

# Possible Fixes
A way to save formatted work would be to save the css document that is created along with the actual file, and not allow it to be visible in the file tree. Then when opening the text document, opens the css file along with it and apply the style. Only problem would be actually gathering the data to save the document. Might be impossible.
