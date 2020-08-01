package de.bildschirmarbeiter.aem.toolbox.application;

import com.google.common.eventbus.Subscribe;
import de.bildschirmarbeiter.aem.toolbox.application.message.LogMessage;
import de.bildschirmarbeiter.aem.toolbox.application.querybuilder.QueryService;
import de.bildschirmarbeiter.aem.toolbox.application.querybuilder.message.QueryCommand;
import de.bildschirmarbeiter.aem.toolbox.application.querybuilder.message.QueryResultEvent;
import de.bildschirmarbeiter.application.message.spi.MessageService;
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
                    final String result = queryService.query(command.getScheme(), command.getHost(), command.getPort(), command.getUsername(), command.getPassword(), command.getQuery());
                    messageService.send(LogMessage.info(this, "Query result received."));
                    final QueryResultEvent event = new QueryResultEvent(this, result);
                    messageService.send(event);
                } catch (Exception e) {
                    messageService.send(LogMessage.error(this, e.getMessage()));
                }
            }
        }.start();
    }

}
