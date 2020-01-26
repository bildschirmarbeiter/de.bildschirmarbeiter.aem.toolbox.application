package de.bildschirmarbeiter.aem.toolbox.application.querybuilder.message;

import de.bildschirmarbeiter.application.message.base.AbstractMessage;

public class QueryResultEvent extends AbstractMessage {

    private final String queryResult;

    public QueryResultEvent(final Object source, final String queryResult) {
        super(source);
        this.queryResult = queryResult;
    }

    public String getQueryResult() {
        return queryResult;
    }

}
