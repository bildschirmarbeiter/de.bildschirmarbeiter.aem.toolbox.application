package de.bildschirmarbeiter.aem.toolbox.application.system;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import de.bildschirmarbeiter.aem.toolbox.application.message.LogMessage;
import de.bildschirmarbeiter.application.message.spi.MessageService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = ClipboardService.class
)
public class ClipboardService {

    @Reference
    private volatile MessageService messageService;

    private Clipboard clipboard;

    public ClipboardService() {
    }

    @Activate
    private void activate() {
        clipboard = Clipboard.getSystemClipboard();
    }

    @Deactivate
    private void deactivate() {
        clipboard = null;
    }

    public void copyToClipboard(final String string) {
        final ClipboardContent content = new ClipboardContent();
        content.putString(string);
        clipboard.setContent(content);
        messageService.send(LogMessage.info(this, "content copied to clipboard"));
    }

    public void copyToClipboard(final Iterable<String> strings) {
        final StringBuilder sb = new StringBuilder();
        strings.forEach(string -> sb.append(string).append("\n"));
        copyToClipboard(sb.toString());
    }

}
