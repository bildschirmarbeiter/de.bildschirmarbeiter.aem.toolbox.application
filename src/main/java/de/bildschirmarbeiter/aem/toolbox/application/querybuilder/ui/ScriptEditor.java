package de.bildschirmarbeiter.aem.toolbox.application.querybuilder.ui;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = ScriptEditor.class
)
public class ScriptEditor extends VBox {

    @Reference
    private volatile QuerybuilderViewModel model;

    private final TextArea editor = new TextArea();

    public ScriptEditor() {
        setSpacing(10);
        final Label title = new Label("Script Editor");
        editor.setId("scriptEditor");
        getChildren().addAll(
            title,
            editor
        );
    }

    @Activate
    private void activate() {
        editor.textProperty().bindBidirectional(model.script);
    }

    @Deactivate
    private void deactivate() {
        editor.textProperty().unbindBidirectional(model.script);
    }

}
