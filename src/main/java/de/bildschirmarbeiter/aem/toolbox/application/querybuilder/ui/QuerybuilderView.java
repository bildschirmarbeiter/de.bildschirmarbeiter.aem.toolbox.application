package de.bildschirmarbeiter.aem.toolbox.application.querybuilder.ui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import de.bildschirmarbeiter.aem.toolbox.application.ui.MainView;
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

    @Reference
    private volatile TemplatingView templatingView;

    @Reference
    private volatile ScriptingView scriptingView;

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
        final VBox left = new VBox();
        left.getChildren().addAll(queryView, resultView);
        final VBox right = new VBox();
        right.getChildren().addAll(templatingView, scriptingView);
        node.add(left, 0, 0);
        node.add(right, 1, 0);
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
