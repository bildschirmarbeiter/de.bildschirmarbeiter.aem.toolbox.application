package de.bildschirmarbeiter.aem.toolbox.application.querybuilder.ui;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = FilterEditor.class
)
public class FilterEditor extends VBox {

    @Reference
    private volatile QuerybuilderViewModel model;

    private TextField editor;

    public FilterEditor() {
        setSpacing(10);
        final Label title = new Label("Filter Editor");
        final TextField editor = new TextField();
        editor.setPromptText("filter");
        this.editor = editor;
        getChildren().addAll(
            title,
            editor
        );
    }

    @Activate
    private void activate() {
        editor.textProperty().bindBidirectional(model.filter);
    }

    @Deactivate
    private void deactivate() {
        editor.textProperty().unbindBidirectional(model.filter);
    }

}
