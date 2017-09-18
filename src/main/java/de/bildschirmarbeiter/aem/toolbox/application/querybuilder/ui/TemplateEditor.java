package de.bildschirmarbeiter.aem.toolbox.application.querybuilder.ui;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = TemplateEditor.class
)
public class TemplateEditor extends VBox {

    @Reference
    private volatile QuerybuilderViewModel model;

    private final TextField editor;

    public TemplateEditor() {
        setSpacing(10);
        final Label title = new Label("Template Editor");
        final TextField editor = new TextField();
        editor.setId("templateEditor");
        editor.setPromptText("Handlebars template");
        this.editor = editor;
        getChildren().addAll(
            title,
            editor
        );
    }

    @Activate
    private void activate() {
        editor.textProperty().bindBidirectional(model.template);
    }

    @Deactivate
    private void deactivate() {
        editor.textProperty().unbindBidirectional(model.template);
    }

}
