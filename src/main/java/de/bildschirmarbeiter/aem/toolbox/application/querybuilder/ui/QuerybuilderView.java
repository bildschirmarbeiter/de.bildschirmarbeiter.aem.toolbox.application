package de.bildschirmarbeiter.aem.toolbox.application.querybuilder.ui;

import de.bildschirmarbeiter.aem.toolbox.application.ui.MainView;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = MainView.class,
    property = {
        "service.ranking:Integer=1"
    }
)
public class QuerybuilderView implements MainView {

    @Reference
    private volatile QueryView queryView;

    @Reference
    private volatile ResultView resultView;

    private final GridPane node;

    private static final String TITLE = "Query Builder Client";

    public QuerybuilderView() {
        final GridPane pane = new GridPane();
        pane.setPadding(new Insets(5, 5, 5, 5));
        pane.setHgap(20);
        pane.setVgap(10);
        final ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(50);
        pane.getColumnConstraints().addAll(columnConstraints, columnConstraints);
        this.node = pane;
    }

    @Activate
    private void activate() {
        node.add(queryView, 0, 0);
        node.add(resultView, 1, 0);
    }

    @Deactivate
    private void deactivate() {
        node.getChildren().clear();
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public Node getNode() {
        return node;
    }

}
