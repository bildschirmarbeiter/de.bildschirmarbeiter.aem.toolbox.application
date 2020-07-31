package de.bildschirmarbeiter.aem.toolbox.application.querybuilder.ui;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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

    private final Label title = new Label("Result");

    private final Label success = new Label();

    private final Label results = new Label();

    private final Label total = new Label();

    private final Label more = new Label();

    private final Label offset = new Label();

    private final ListView<String> result = new ListView<>();

    public ResultView() {
        setSpacing(10);
        title.getStyleClass().add("main-title");
        title.setPrefWidth(Double.MAX_VALUE);
        setOnMouseEntered(event -> title.getStyleClass().add("main-focus"));
        setOnMouseExited(event -> title.getStyleClass().remove("main-focus"));
        result.setId("resultOutput");
        final Label successTitle = new Label("success:");
        final Label resultsTitle = new Label("results:");
        final Label totalTitle = new Label("total:");
        final Label moreTitle = new Label("more:");
        final Label offsetTitle = new Label("offset:");
        final HBox details = new HBox();
        details.setSpacing(10);
        details.getChildren().addAll(
            successTitle,
            success,
            resultsTitle,
            results,
            totalTitle,
            total,
            moreTitle,
            more,
            offsetTitle,
            offset
        );
        result.setEditable(false);
        getChildren().addAll(
            title,
            details,
            result
        );
    }

    @Activate
    private void activate() {
        success.textProperty().bind(model.success.asString());
        results.textProperty().bind(model.results.asString());
        total.textProperty().bind(model.total.asString());
        more.textProperty().bind(model.more.asString());
        offset.textProperty().bind(model.offset.asString());
        result.setItems(model.lines);
    }

    @Deactivate
    private void deactivate() {
        success.textProperty().unbind();
        results.textProperty().unbind();
        total.textProperty().unbind();
        more.textProperty().unbind();
        offset.textProperty().unbind();
        result.setItems(null);
    }

}
