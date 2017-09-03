package de.bildschirmarbeiter.aem.toolbox.application.querybuilder.message;

import de.bildschirmarbeiter.aem.toolbox.application.message.AbstractServerCommand;

public class QueryCommand extends AbstractServerCommand {

    private final String query;

    public QueryCommand(final Object source, final String host, final String port, final String username, final String password, final String query) {
        super(source, host, port, username, password);
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

}
