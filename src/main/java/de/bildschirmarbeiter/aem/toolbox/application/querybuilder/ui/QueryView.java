package de.bildschirmarbeiter.aem.toolbox.application.querybuilder.ui;

import de.bildschirmarbeiter.aem.toolbox.application.message.LogMessage;
import de.bildschirmarbeiter.aem.toolbox.application.querybuilder.message.QueryCommand;
import de.bildschirmarbeiter.application.message.spi.MessageService;
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
    service = QueryView.class
)
public class QueryView extends VBox {

    @Reference
    private volatile QuerybuilderViewModel model;

    @Reference
    private volatile MessageService messageService;

    @Reference
    private volatile ServerEditor serverEditor;

    @Reference
    private volatile QueryEditor queryEditor;

    private final Label title = new Label("Query");

    private final HBox buttons = new HBox();

    public QueryView() {
        setPadding(new Insets(5, 5, 5, 5));
        setSpacing(20);
        title.getStyleClass().add("main-title");
        title.setPrefWidth(Double.MAX_VALUE);
        setOnMouseEntered(event -> title.getStyleClass().add("main-focus"));
        setOnMouseExited(event -> title.getStyleClass().remove("main-focus"));
        final Button button = new Button("query");
        button.setOnAction(event -> query());
        buttons.setSpacing(10);
        buttons.setAlignment(Pos.BASELINE_RIGHT);
        buttons.getChildren().add(button);
    }

    @Activate
    private void activate() {
        getChildren().addAll(
            title,
            serverEditor,
            queryEditor,
            buttons
        );
    }

    @Deactivate
    private void deactivate() {
        getChildren().clear();
    }

    // TODO
    private void query() {
        final String host = model.host.get();
        final String port = model.port.get();
        final String username = model.username.get();
        final String password = model.password.get();
        final String query = model.query.get();
        if (host == null) {
            messageService.send(LogMessage.error(this, "host is null"));
        } else if (port == null) {
            messageService.send(LogMessage.error(this, "port is null"));
        } else if (username == null) {
            messageService.send(LogMessage.error(this, "username is null"));
        } else if (password == null) {
            messageService.send(LogMessage.error(this, "password is null"));
        } else if (query == null) {
            messageService.send(LogMessage.error(this, "query is null"));
        } else {
            messageService.send(LogMessage.info(this, String.format("running query on host %s", host)));
            final QueryCommand command = new QueryCommand(this, host, port, username, password, query);
            messageService.send(command);
        }
    }

}
