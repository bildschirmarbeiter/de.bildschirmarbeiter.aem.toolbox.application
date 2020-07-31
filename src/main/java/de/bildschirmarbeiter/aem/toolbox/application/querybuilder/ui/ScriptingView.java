package de.bildschirmarbeiter.aem.toolbox.application.querybuilder.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import de.bildschirmarbeiter.aem.toolbox.application.system.ClipboardService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = ScriptingView.class
)
public class ScriptingView extends VBox {

    @Reference
    private volatile QuerybuilderViewModel model;

    @Reference
    private volatile ClipboardService clipboardService;

    @Reference
    private volatile ScriptEditor scriptEditor;

    private final Label title = new Label("Scripting");

    private final TextArea output = new TextArea();

    private final HBox buttons = new HBox();

    public ScriptingView() {
        setPadding(new Insets(5, 5, 5, 5));
        setSpacing(20);
        title.getStyleClass().add("main-title");
        title.setPrefWidth(Double.MAX_VALUE);
        setOnMouseEntered(event -> title.getStyleClass().add("main-focus"));
        setOnMouseExited(event -> title.getStyleClass().remove("main-focus"));
        output.setId("scriptOutput");
        final Button button = new Button("copy script output");
        button.setOnAction(event -> clipboardService.copyToClipboard(model.scriptOutput.getValue()));
        buttons.setSpacing(10);
        buttons.setAlignment(Pos.BASELINE_RIGHT);
        buttons.getChildren().add(button);
    }

    @Activate
    private void activate() {
        getChildren().addAll(
            title,
            output,
            scriptEditor,
            buttons
        );
        output.textProperty().bind(model.scriptOutput);
    }

    @Deactivate
    private void deactivate() {
        output.textProperty().unbind();
        getChildren().clear();
    }

}
