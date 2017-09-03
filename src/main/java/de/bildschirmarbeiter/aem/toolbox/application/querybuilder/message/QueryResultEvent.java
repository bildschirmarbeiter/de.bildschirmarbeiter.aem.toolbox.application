package de.bildschirmarbeiter.aem.toolbox.application.querybuilder.message;

import de.bildschirmarbeiter.aem.toolbox.application.querybuilder.QueryResult;
import de.bildschirmarbeiter.application.message.base.AbstractMessage;

public class QueryResultEvent extends AbstractMessage {

    private final QueryResult queryResult;

    public QueryResultEvent(final Object source, final QueryResult queryResult) {
        super(source);
        this.queryResult = queryResult;
    }

    public QueryResult getQueryResult() {
        return queryResult;
    }

}
