package de.bildschirmarbeiter.aem.toolbox.application.querybuilder;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

import com.google.common.base.Throwables;
import de.bildschirmarbeiter.aem.toolbox.application.message.LogMessage;
import de.bildschirmarbeiter.application.message.spi.MessageService;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    service = QueryService.class
)
public class QueryService {

    @Reference
    private volatile CloseableHttpClient httpClient;

    @Reference
    private volatile QueryResultParser resultParser;

    @Reference
    private volatile MessageService messageService;

    private static final String QUERY_PATH = "/bin/querybuilder.json";

    public QueryService() {
    }

    private String authHeader(final String username, final String password) {
        final String credentials = String.format("%s:%s", username, password);
        final String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return String.format("Basic %s", encoded);
    }

    public QueryResult query(final String scheme, final String host, final String port, final String username, final String password, final String query) {
        try {
            final URIBuilder builder = new URIBuilder()
                .setScheme(scheme)
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
            messageService.send(LogMessage.info(this, uri.toString()));
            final HttpGet httpGet = new HttpGet(uri);
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, authHeader(username, password));

            try (final CloseableHttpResponse response = httpClient.execute(httpGet)) {
                final InputStream content = response.getEntity().getContent();
                final String json = IOUtils.toString(content, StandardCharsets.UTF_8);
                return resultParser.parseResult(json);
            } finally {
                httpGet.releaseConnection();
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

}
