package io.captainsly.noteviewer.ui.components;

import java.io.File;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class SlidePaneContent {

	private final DoubleBinding widthBinding;

	public SlidePaneContent(ReadOnlyDoubleProperty widthPanel, ReadOnlyDoubleProperty widthButton) {
		widthBinding = widthPanel.subtract(widthButton).subtract(25);
		buildFileSystemBrowser();
	}

	private TreeView buildFileSystemBrowser() {
		TreeItem<File> root = createNode(new File("/"));
		return new TreeView<File>(root);
	}

	private TreeItem<File> createNode(File f) {
		return new TreeItem<File>(f) {
			private boolean isLeaf;
			private boolean isFirstTimeChildren = true;
			private boolean isFirstTimeLeaf = true;
			
			@Override
			public ObservableList<TreeItem<File>> getChildren() {
				if (isFirstTimeChildren) isFirstTimeChildren = false;
				return super.getChildren();
			}
			
			@Override
			public boolean isLeaf() {
				if (isFirstTimeLeaf) {
					isFirstTimeLeaf = false;
					File f = (File) getValue();
					isLeaf = f.isFile();
				}
				return isLeaf;
			}
			
			public ObservableList<TreeItem<File>> buildChildren(TreeItem<File> TreeItem) {
				File f = TreeItem.getValue();
				if (f != null && f.isDirectory()) {
					File[] files = f.listFiles();
					if (files != null) {
						ObservableList<TreeItem<File>> children = FXCollections.observableArrayList();
						
						for (File childFile : files)
							children.add(createNode(childFile));
						
						return children;
					}
				}
				return FXCollections.emptyObservableList();
			}
			
		};
	}

}
