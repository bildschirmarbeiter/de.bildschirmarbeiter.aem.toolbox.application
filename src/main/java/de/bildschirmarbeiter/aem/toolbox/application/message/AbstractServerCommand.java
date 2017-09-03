package de.bildschirmarbeiter.aem.toolbox.application.message;

import de.bildschirmarbeiter.application.message.base.AbstractMessage;

public abstract class AbstractServerCommand extends AbstractMessage {

    protected final String host;

    protected final String port;

    protected final String username;

    protected final String password;

    public AbstractServerCommand(final Object source, final String host, final String port, final String username, final String password) {
        super(source);
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
