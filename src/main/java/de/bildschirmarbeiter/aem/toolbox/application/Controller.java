package de.bildschirmarbeiter.aem.toolbox.application;

import com.google.common.eventbus.Subscribe;
import de.bildschirmarbeiter.aem.toolbox.application.message.LogMessage;
import de.bildschirmarbeiter.aem.toolbox.application.querybuilder.QueryResult;
import de.bildschirmarbeiter.aem.toolbox.application.querybuilder.QueryService;
import de.bildschirmarbeiter.aem.toolbox.application.querybuilder.message.QueryCommand;
import de.bildschirmarbeiter.aem.toolbox.application.querybuilder.message.QueryResultEvent;
import de.bildschirmarbeiter.application.message.spi.MessageService;
import javafx.application.Platform;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true
)
public class Controller {

    @Reference
    private volatile QueryService queryService;

    @Reference
    private volatile MessageService messageService;

    public Controller() {
    }

    @Activate
    private void activate() {
        messageService.register(this);
    }

    @Deactivate
    private void deactivate() {
        messageService.unregister(this);
    }

    @Subscribe
    public void onQueryCommand(final QueryCommand command) {
        new Thread() {
            public void run() {
                try {
                    final QueryResult result = queryService.query(command.getHost(), command.getPort(), command.getUsername(), command.getPassword(), command.getQuery());
                    final QueryResultEvent event = new QueryResultEvent(this, result);
                    sendMessage(event);
                    final String message = String.format("Query result received: %s", result);
                    sendMessage(LogMessage.info(this, message));
                } catch (Exception e) {
                    sendMessage(LogMessage.error(this, e.getMessage()));
                }
            }
        }.start();
    }

    private void sendMessage(final Object event) {
        Platform.runLater(() -> messageService.send(event));
    }

}
