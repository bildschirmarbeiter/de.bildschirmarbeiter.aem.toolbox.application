package de.bildschirmarbeiter.aem.toolbox.application.querybuilder.ui;

import de.bildschirmarbeiter.aem.toolbox.application.querybuilder.ui.QuerybuilderViewModel.FilterMode;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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

    private final TextField editor;

    private final ChoiceBox<FilterMode> filterModes;

    public FilterEditor() {
        setSpacing(10);
        final Label title = new Label("Filter Editor");
        this.filterModes = new ChoiceBox<>();
        final TextField editor = new TextField();
        editor.setPromptText("filter expression");
        this.editor = editor;
        HBox.setHgrow(editor, Priority.ALWAYS);
        final HBox container = new HBox();
        container.setSpacing(10);
        container.getChildren().addAll(
            filterModes,
            editor
        );
        getChildren().addAll(
            title,
            container
        );
    }

    @Activate
    private void activate() {
        editor.textProperty().bindBidirectional(model.filterExpression);
        filterModes.setItems(model.filterModes);
        filterModes.setValue(model.filterMode.getValue());
        model.filterMode.bind(filterModes.getSelectionModel().selectedItemProperty());

    }

    @Deactivate
    private void deactivate() {
        editor.textProperty().unbindBidirectional(model.filterExpression);
        filterModes.setItems(null);
        filterModes.setValue(null);
        model.filterMode.unbind();
    }

}
