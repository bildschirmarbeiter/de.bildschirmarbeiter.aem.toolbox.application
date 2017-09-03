package de.bildschirmarbeiter.aem.toolbox.application.querybuilder.ui;

import de.bildschirmarbeiter.aem.toolbox.application.system.ClipboardService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = ResultView.class
)
public class ResultView extends VBox {

    @Reference
    private volatile QuerybuilderViewModel model;

    @Reference
    private volatile ClipboardService clipboardService;

    @Reference
    private volatile TemplateEditor templateEditor;

    @Reference
    private volatile HitsView hitsView;

    @Reference
    private volatile FilterEditor filterEditor;

    private final Label title = new Label("Result");

    private final HBox buttons = new HBox();

    public ResultView() {
        setPadding(new Insets(5, 5, 5, 5));
        setSpacing(20);
        title.getStyleClass().add("main-title");
        title.setPrefWidth(Double.MAX_VALUE);
        setOnMouseEntered(event -> title.getStyleClass().add("main-focus"));
        setOnMouseExited(event -> title.getStyleClass().remove("main-focus"));
        final Button button = new Button("copy hits");
        button.setOnAction(event -> clipboardService.copyToClipboard(model.filteredResult));
        buttons.setSpacing(10);
        buttons.setAlignment(Pos.BASELINE_RIGHT);
        buttons.getChildren().add(button);
    }

    @Activate
    private void activate() {
        getChildren().addAll(
            title,
            hitsView,
            templateEditor,
            filterEditor,
            buttons
        );
    }

    @Deactivate
    private void deactivate() {
        getChildren().clear();
    }

}
