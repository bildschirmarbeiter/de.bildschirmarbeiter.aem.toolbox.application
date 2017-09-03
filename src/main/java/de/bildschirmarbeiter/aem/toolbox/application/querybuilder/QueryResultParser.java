package de.bildschirmarbeiter.aem.toolbox.application.querybuilder;

import com.google.gson.Gson;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = QueryResultParser.class
)
public class QueryResultParser {

    @Reference
    private volatile Gson gson;

    public QueryResultParser() {
    }

    public QueryResult parseResult(final String json) {
        return gson.fromJson(json, QueryResult.class);
    }

}
