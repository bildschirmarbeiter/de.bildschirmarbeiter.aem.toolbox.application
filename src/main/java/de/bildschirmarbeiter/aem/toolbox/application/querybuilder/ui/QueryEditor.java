package de.bildschirmarbeiter.aem.toolbox.application.querybuilder.ui;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = QueryEditor.class
)
public class QueryEditor extends VBox {

    @Reference
    private volatile QuerybuilderViewModel model;

    private final TextArea editor = new TextArea();

    public QueryEditor() {
        setSpacing(10);
        final Label title = new Label("Query Editor");
        editor.setId("queryEditor");
        getChildren().addAll(
            title,
            editor
        );
    }

    @Activate
    private void activate() {
        editor.textProperty().bindBidirectional(model.query);
    }

    @Deactivate
    private void deactivate() {
        editor.textProperty().unbindBidirectional(model.query);
    }

}
