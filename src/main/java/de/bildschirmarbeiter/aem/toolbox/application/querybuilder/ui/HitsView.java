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
    service = HitsView.class
)
public class HitsView extends VBox {

    @Reference
    private volatile QuerybuilderViewModel model;

    private final ListView<String> list = new ListView<>();

    private final Label success = new Label();

    private final Label results = new Label();

    private final Label total = new Label();

    private final Label more = new Label();

    private final Label offset = new Label();

    public HitsView() {
        setSpacing(10);
        final Label title = new Label("Hits");
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
        getChildren().addAll(
            title,
            list,
            details
        );
    }

    @Activate
    private void activate() {
        list.setItems(model.filteredResult);
        success.textProperty().bind(model.success.asString());
        results.textProperty().bind(model.results.asString());
        total.textProperty().bind(model.total.asString());
        more.textProperty().bind(model.more.asString());
        offset.textProperty().bind(model.offset.asString());
    }

    @Deactivate
    private void deactivate() {
        list.setItems(null);
        success.textProperty().unbind();
        results.textProperty().unbind();
        total.textProperty().unbind();
        more.textProperty().unbind();
        offset.textProperty().unbind();
    }

}
