package de.bildschirmarbeiter.aem.toolbox.application.querybuilder;

import java.net.URI;
import java.util.Scanner;

import com.google.common.base.Throwables;
import org.apache.http.HttpHost;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = QueryService.class
)
public class QueryService {

    @Reference
    private volatile QueryResultParser resultParser;

    private final Executor executor = Executor.newInstance();

    private static final String QUERY_PATH = "/bin/querybuilder.json";

    public QueryService() {
    }

    public QueryResult query(final String host, final String port, final String username, final String password, final String query) {
        final HttpHost httpHost = new HttpHost(host);
        executor.auth(httpHost, username, password);
        try {
            final URIBuilder builder = new URIBuilder()
                .setScheme("http") // TODO
                .setHost(host)
                .setPort(Integer.parseInt(port)) // TODO
                .setPath(QUERY_PATH);
            final Scanner scanner = new Scanner(query);
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                final String[] parameter = line.split("=", 2);
                if (parameter.length == 2) {
                    builder.addParameter(parameter[0], parameter[1]);
                }
            }
            final URI uri = builder.build();
            final String result = executor.execute(Request.Get(uri)).returnContent().asString();
            return resultParser.parseResult(result);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

}
