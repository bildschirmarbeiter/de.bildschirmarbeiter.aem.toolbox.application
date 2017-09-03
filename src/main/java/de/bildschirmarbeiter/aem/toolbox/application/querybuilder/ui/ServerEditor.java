package de.bildschirmarbeiter.aem.toolbox.application.querybuilder.ui;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = ServerEditor.class
)
public class ServerEditor extends VBox {

    @Reference
    private volatile QuerybuilderViewModel model;

    private final TextField host = new TextField();

    private final TextField port = new TextField();

    private final TextField username = new TextField();

    private final PasswordField password = new PasswordField();

    public ServerEditor() {
        setSpacing(10);
        final Label title = new Label("Server");
        host.setPromptText("host");
        port.setPromptText("port");
        username.setPromptText("username");
        password.setPromptText("password");
        final HBox fields = new HBox();
        fields.setSpacing(10);
        fields.getChildren().addAll(
            host,
            port,
            username,
            password
        );
        getChildren().addAll(
            title,
            fields
        );
    }

    @Activate
    private void activate() {
        host.textProperty().bindBidirectional(model.host);
        port.textProperty().bindBidirectional(model.port);
        username.textProperty().bindBidirectional(model.username);
        password.textProperty().bindBidirectional(model.password);
    }

    @Deactivate
    private void deactivate() {
        host.textProperty().unbindBidirectional(model.host);
        port.textProperty().unbindBidirectional(model.port);
        username.textProperty().unbindBidirectional(model.username);
        password.textProperty().unbindBidirectional(model.password);
    }

}
