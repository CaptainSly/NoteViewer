package io.captainsly.noteviewer.ui.components.editor;

import static org.fxmisc.richtext.TwoDimensional.Bias.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.AreaFactory;
import org.fxmisc.richtext.Paragraph;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyledTextArea;
import org.reactfx.SuspendableNo;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class Editor extends BorderPane {

	private final StyledTextArea<ParStyle, TextStyle> area = AreaFactory.<ParStyle, TextStyle>styledTextArea(ParStyle.EMPTY, (paragraph, style) -> paragraph.setStyle(style.toCss()), TextStyle.EMPTY.updateFontSize(12).updateFontFamily("Serif").updateTextColor(Color.BLACK), (text, style) -> text.setStyle(style.toCss()));

	private final SuspendableNo updatingToolbar = new SuspendableNo();

	public Editor() {
		CheckBox wrapToggle = new CheckBox("Wrap");
		wrapToggle.setSelected(true);
		area.wrapTextProperty().bind(wrapToggle.selectedProperty());

		Button undoBtn = createButton("undo", area::undo);
		Button redoBtn = createButton("redo", area::redo);
		Button cutBtn = createButton("cut", area::cut);
		Button copyBtn = createButton("copy", area::copy);
		Button pasteBtn = createButton("paste", area::paste);
		Button boldBtn = createButton("bold", this::toggleBold);
		Button italicBtn = createButton("italic", this::toggleItalic);
		Button underlineBtn = createButton("underline", this::toggleUnderline);
		Button strikeBtn = createButton("strikethrough", this::toggleStrikethrough);

		ToggleGroup alignmentGroup = new ToggleGroup();
		ToggleButton alignLeftBtn = createToggleButton(alignmentGroup, "align-left", this::alignLeft);
		ToggleButton alignRightBtn = createToggleButton(alignmentGroup, "align-right", this::alignRight);
		ToggleButton alignCenterBtn = createToggleButton(alignmentGroup, "align-center", this::alignCenter);
		ToggleButton alignJustifyBtn = createToggleButton(alignmentGroup, "align-justify", this::alignJustify);

		ColorPicker paragraphBackgroundPicker = new ColorPicker();
		ColorPicker textColorPicker = new ColorPicker(Color.BLACK);
		ColorPicker backgroundColorPicker = new ColorPicker();

		ComboBox<Integer> sizeCBox = new ComboBox<>(FXCollections.observableArrayList(5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 18, 20, 22, 24, 28, 32, 26, 40, 48, 56, 64, 72));
		sizeCBox.getSelectionModel().select(Integer.valueOf(12));
		ComboBox<String> familyCBox = new ComboBox<>(FXCollections.observableArrayList(Font.getFamilies()));
		familyCBox.getSelectionModel().select("Serif");

		paragraphBackgroundPicker.setTooltip(new Tooltip("Paragraph background"));
		textColorPicker.setTooltip(new Tooltip("Text color"));
		backgroundColorPicker.setTooltip(new Tooltip("Text background"));

		paragraphBackgroundPicker.valueProperty().addListener((o, old, color) -> updateParagraphBackground(color));
		textColorPicker.valueProperty().addListener((o, old, color) -> updateTextColor(color));
		backgroundColorPicker.valueProperty().addListener((o, old, color) -> updateBackgroundColor(color));
		sizeCBox.setOnAction(evt -> updateFontSize(sizeCBox.getValue()));
		familyCBox.setOnAction(evt -> updateFontFamily(familyCBox.getValue()));

		undoBtn.disableProperty().bind(Bindings.not(area.undoAvailableProperty()));
		redoBtn.disableProperty().bind(Bindings.not(area.redoAvailableProperty()));

		undoBtn.setGraphic(addImage("undo-16"));
		redoBtn.setGraphic(addImage("redo-16"));
		cutBtn.setGraphic(addImage("cut-16"));
		pasteBtn.setGraphic(addImage("paste-16"));
		copyBtn.setGraphic(addImage("copy-16"));
		boldBtn.setGraphic(addImage("bold-16"));
		italicBtn.setGraphic(addImage("italic-16"));
		underlineBtn.setGraphic(addImage("underline-16"));
		strikeBtn.setGraphic(addImage("strikethrough-16"));
		alignLeftBtn.setGraphic(addImage("alignLeft-16"));
		alignCenterBtn.setGraphic(addImage("alignCenter-16"));
		alignRightBtn.setGraphic(addImage("alignRight-16"));
		alignJustifyBtn.setGraphic(addImage("alignJustify-16"));
		
		
		BooleanBinding selectionEmpty = new BooleanBinding() {
			{
				bind(area.selectionProperty());
			}

			@Override
			protected boolean computeValue() {
				return area.getSelection().getLength() == 0;
			}

		};

		cutBtn.disableProperty().bind(selectionEmpty);
		copyBtn.disableProperty().bind(selectionEmpty);

		area.beingUpdatedProperty().addListener((o, old, beingUpdated) -> {
			if (!beingUpdated) {
				boolean bold, italic, underline, strike;
				Integer fontSize;
				String fontFamily;
				Color textColor;
				Color backgroundColor;

				IndexRange selection = area.getSelection();
				if (selection.getLength() != 0) {
					StyleSpans<TextStyle> styles = area.getStyleSpans(selection);
					bold = styles.styleStream().anyMatch(s -> s.bold.orElse(false));
					italic = styles.styleStream().anyMatch(s -> s.italic.orElse(false));
					underline = styles.styleStream().anyMatch(s -> s.underline.orElse(false));
					strike = styles.styleStream().anyMatch(s -> s.strikethrough.orElse(false));
					int[] sizes = styles.styleStream().mapToInt(s -> s.fontSize.orElse(-1)).distinct().toArray();
					fontSize = sizes.length == 1 ? sizes[0] : -1;
					String[] families = styles.styleStream().map(s -> s.fontFamily.orElse(null)).distinct().toArray(String[]::new);
					fontFamily = families.length == 1 ? families[0] : null;
					Color[] colors = styles.styleStream().map(s -> s.textColor.orElse(null)).distinct().toArray(Color[]::new);
					textColor = colors.length == 1 ? colors[0] : null;
					Color[] backgrounds = styles.styleStream().map(s -> s.backgroundColor.orElse(null)).distinct().toArray(i -> new Color[i]);
					backgroundColor = backgrounds.length == 1 ? backgrounds[0] : null;
				} else {
					int p = area.getCurrentParagraph();
					int col = area.getCaretColumn();
					TextStyle style = area.getStyleAtPosition(p, col);
					bold = style.bold.orElse(false);
					italic = style.italic.orElse(false);
					underline = style.underline.orElse(false);
					strike = style.strikethrough.orElse(false);
					fontSize = style.fontSize.orElse(-1);
					fontFamily = style.fontFamily.orElse(null);
					textColor = style.textColor.orElse(null);
					backgroundColor = style.backgroundColor.orElse(null);
				}

				int startPar = area.offsetToPosition(selection.getStart(), Forward).getMajor();
				int endPar = area.offsetToPosition(selection.getEnd(), Backward).getMajor();
				List<Paragraph<ParStyle, TextStyle>> pars = area.getParagraphs().subList(startPar, endPar + 1);

				@SuppressWarnings("unchecked")
				Optional<TextAlignment>[] alignments = pars.stream().map(p -> p.getParagraphStyle().alignment).distinct().toArray(Optional[]::new);
				Optional<TextAlignment> alignment = alignments.length == 1 ? alignments[0] : Optional.empty();

				@SuppressWarnings("unchecked")
				Optional<Color>[] paragraphBackgrounds = pars.stream().map(p -> p.getParagraphStyle().backgroundColor).distinct().toArray(Optional[]::new);
				Optional<Color> paragraphBackground = paragraphBackgrounds.length == 1 ? paragraphBackgrounds[0] : Optional.empty();

				updatingToolbar.suspendWhile(() -> {
					if (bold) {
						if (!boldBtn.getStyleClass().contains("pressed")) {
							boldBtn.getStyleClass().add("pressed");
						}
					} else {
						boldBtn.getStyleClass().remove("pressed");
					}

					if (italic) {
						if (!italicBtn.getStyleClass().contains("pressed")) {
							italicBtn.getStyleClass().add("pressed");
						}
					} else {
						italicBtn.getStyleClass().remove("pressed");
					}

					if (underline) {
						if (!underlineBtn.getStyleClass().contains("pressed")) {
							underlineBtn.getStyleClass().add("pressed");
						}
					} else {
						underlineBtn.getStyleClass().remove("pressed");
					}

					if (strike) {
						if (!strikeBtn.getStyleClass().contains("pressed")) {
							strikeBtn.getStyleClass().add("pressed");
						}
					} else {
						strikeBtn.getStyleClass().remove("pressed");
					}

					if (alignment.isPresent()) {
						TextAlignment al = alignment.get();
						switch (al) {
							case LEFT:
								alignmentGroup.selectToggle(alignLeftBtn);
								break;
							case CENTER:
								alignmentGroup.selectToggle(alignCenterBtn);
								break;
							case RIGHT:
								alignmentGroup.selectToggle(alignRightBtn);
								break;
							case JUSTIFY:
								alignmentGroup.selectToggle(alignJustifyBtn);
								break;
						}
					} else {
						alignmentGroup.selectToggle(null);
					}

					paragraphBackgroundPicker.setValue(paragraphBackground.orElse(null));

					if (fontSize != -1) {
						sizeCBox.getSelectionModel().select(fontSize);
					} else {
						sizeCBox.getSelectionModel().clearSelection();
					}

					if (fontFamily != null) {
						familyCBox.getSelectionModel().select(fontFamily);
					} else {
						familyCBox.getSelectionModel().clearSelection();
					}

					if (textColor != null) {
						textColorPicker.setValue(textColor);
					}

					backgroundColorPicker.setValue(backgroundColor);
				});
			}
		});

		HBox panel1 = new HBox(3.0);
		HBox panel2 = new HBox(3.0);
		panel1.getChildren().addAll(wrapToggle, undoBtn, redoBtn, cutBtn, copyBtn, pasteBtn, boldBtn, italicBtn, underlineBtn, strikeBtn, alignLeftBtn, alignCenterBtn, alignRightBtn, alignJustifyBtn, paragraphBackgroundPicker);
		panel2.getChildren().addAll(sizeCBox, familyCBox, textColorPicker, backgroundColorPicker);
		
		VirtualizedScrollPane<StyledTextArea<ParStyle, TextStyle>> vsPane = new VirtualizedScrollPane<>(area);
		VBox vbox = new VBox();
		VBox.setVgrow(vsPane, Priority.ALWAYS);
		vbox.getChildren().addAll(panel1, panel2, vsPane);
		
		setCenter(vbox);
		
	}

	private ToggleButton createToggleButton(ToggleGroup group, String styleClass, Runnable action) {
		ToggleButton button = new ToggleButton();
		button.setToggleGroup(group);
		button.getStyleClass().add(styleClass);
		button.setOnAction(evt -> {
			action.run();
			area.requestFocus();
		});
		button.setPrefWidth(20);
		button.setPrefHeight(20);
		return button;
	}

	private Button createButton(String styleClass, Runnable action) {
		Button button = new Button();
		button.getStyleClass().add(styleClass);
		button.setOnAction(evt -> {
			action.run();
			area.requestFocus();
		});
		button.setPrefWidth(20);
		button.setPrefHeight(20);
		return button;
	}

	private void updateStyleInSelection(Function<StyleSpans<TextStyle>, TextStyle> mixinGetter) {
		IndexRange selection = area.getSelection();
		if (selection.getLength() != 0) {
			StyleSpans<TextStyle> styles = area.getStyleSpans(selection);
			TextStyle mixin = mixinGetter.apply(styles);
			StyleSpans<TextStyle> newStyles = styles.mapStyles(style -> style.updateWith(mixin));
			area.setStyleSpans(selection.getStart(), newStyles);
		}
	}

	private void toggleBold() {
		updateStyleInSelection(spans -> TextStyle.bold(!spans.styleStream().allMatch(style -> style.bold.orElse(false))));
	}

	private void toggleItalic() {
		updateStyleInSelection(spans -> TextStyle.italic(!spans.styleStream().allMatch(style -> style.italic.orElse(false))));
	}

	private void toggleUnderline() {
		updateStyleInSelection(spans -> TextStyle.underline(!spans.styleStream().allMatch(style -> style.underline.orElse(false))));
	}

	private void toggleStrikethrough() {
		updateStyleInSelection(spans -> TextStyle.strikethrough(!spans.styleStream().allMatch(style -> style.strikethrough.orElse(false))));
	}

	private void alignLeft() {
		updateParagraphStyle(ParStyle.alignLeft());
	}

	private void alignRight() {
		updateParagraphStyle(ParStyle.alignRight());
	}

	private void alignCenter() {
		updateParagraphStyle(ParStyle.alignCenter());
	}

	private void alignJustify() {
		updateParagraphStyle(ParStyle.alignJustify());
	}

	public void updateStyleInSelection(TextStyle mixin) {
		IndexRange selection = area.getSelection();
		if (selection.getLength() != 0) {
			StyleSpans<TextStyle> styles = area.getStyleSpans(selection);
			StyleSpans<TextStyle> newStyles = styles.mapStyles(style -> style.updateWith(mixin));
			area.setStyleSpans(selection.getStart(), newStyles);
		}
	}

	private void updateParagraphStyleInSelection(Function<ParStyle, ParStyle> updater) {
		IndexRange selection = area.getSelection();
		int startPar = area.offsetToPosition(selection.getStart(), Forward).getMajor();
		int endPar = area.offsetToPosition(selection.getEnd(), Backward).getMajor();
		for (int i = startPar; i <= endPar; i++) {
			Paragraph<ParStyle, TextStyle> paragraph = area.getParagraph(i);
			area.setParagraphStyle(i, updater.apply(paragraph.getParagraphStyle()));
		}
	}

	private void updateParagraphStyle(ParStyle mixin) {
		updateParagraphStyleInSelection(style -> style.updateWith(mixin));
	}

	private void updateFontSize(Integer size) {
		if (!updatingToolbar.get()) updateStyleInSelection(TextStyle.fontSize(size));
	}

	private void updateFontFamily(String family) {
		if (!updatingToolbar.get()) updateStyleInSelection(TextStyle.fontFamily(family));
	}

	private void updateTextColor(Color color) {
		if (!updatingToolbar.get()) updateStyleInSelection(TextStyle.textColor(color));
	}

	private void updateBackgroundColor(Color color) {
		if (!updatingToolbar.get()) updateStyleInSelection(TextStyle.backgroundColor(color));
	}

	private void updateParagraphBackground(Color color) {
		if (!updatingToolbar.get()) updateParagraphStyle(ParStyle.backgroundColor(color));
	}

	public String getText() {
		return area.getText();
	}

	public void setText(String dtext) {
		area.replaceText(dtext);
	}

	public Node addImage(String img) {
		return new ImageView(new Image(img + ".png"));
	}
	
}
