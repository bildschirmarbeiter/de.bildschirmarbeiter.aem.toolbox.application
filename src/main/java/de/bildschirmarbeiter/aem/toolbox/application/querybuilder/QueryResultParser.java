package de.bildschirmarbeiter.aem.toolbox.application.querybuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.osgi.service.component.annotations.Component;

@Component(
    service = QueryResultParser.class
)
public class QueryResultParser {

    private final Gson gson = new GsonBuilder().create();

    public QueryResultParser() {
    }

    public QueryResult parseResult(final String json) {
        return gson.fromJson(json, QueryResult.class);
    }

}
