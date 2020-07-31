package de.bildschirmarbeiter.aem.toolbox.application.querybuilder.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import de.bildschirmarbeiter.aem.toolbox.application.system.ClipboardService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = TemplatingView.class
)
public class TemplatingView extends VBox {

    @Reference
    private volatile QuerybuilderViewModel model;

    @Reference
    private volatile ClipboardService clipboardService;

    @Reference
    private volatile TemplateEditor templateEditor;

    @Reference
    private volatile FilterEditor filterEditor;

    private final Label title = new Label("Templating");

    private final ListView<String> output = new ListView<>();

    private final HBox buttons = new HBox();

    public TemplatingView() {
        setPadding(new Insets(5, 5, 5, 5));
        setSpacing(20);
        title.getStyleClass().add("main-title");
        title.setPrefWidth(Double.MAX_VALUE);
        setOnMouseEntered(event -> title.getStyleClass().add("main-focus"));
        setOnMouseExited(event -> title.getStyleClass().remove("main-focus"));
        output.setId("templateOutput");
        final Button button = new Button("copy filtered template output");
        button.setOnAction(event -> clipboardService.copyToClipboard(model.filteredResultList));
        buttons.setSpacing(10);
        buttons.setAlignment(Pos.BASELINE_RIGHT);
        buttons.getChildren().add(button);
    }

    @Activate
    private void activate() {
        output.setItems(model.filteredResultList);
        getChildren().addAll(
            title,
            output,
            templateEditor,
            filterEditor,
            buttons
        );
    }

    @Deactivate
    private void deactivate() {
        output.setItems(null);
        getChildren().clear();
    }

}
