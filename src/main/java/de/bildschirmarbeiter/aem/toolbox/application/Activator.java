package de.bildschirmarbeiter.aem.toolbox.application;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import com.github.jknack.handlebars.Handlebars;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private CloseableHttpClient closeableHttpClient;

    private ServiceRegistration<CloseableHttpClient> closeableHttpClientServiceRegistration;

    private ServiceRegistration<Handlebars> handlebarsServiceRegistration;

    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        closeableHttpClient = closeableHttpClient();
        closeableHttpClientServiceRegistration = bundleContext.registerService(CloseableHttpClient.class, closeableHttpClient, null);
        handlebarsServiceRegistration = bundleContext.registerService(Handlebars.class, handlebars(), null);
    }

    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        handlebarsServiceRegistration.unregister();
        closeableHttpClientServiceRegistration.unregister();
        closeableHttpClient.close();
    }

    private CloseableHttpClient closeableHttpClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        final SSLContext sslContext = SSLContextBuilder.create().loadTrustMaterial(null, (x509CertChain, authType) -> true).build();
        final SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.INSTANCE)
            .register("https", sslConnectionSocketFactory)
            .build();
        final PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        return HttpClients.custom()
            .setConnectionManager(httpClientConnectionManager)
            .build();
    }

    private Handlebars handlebars() {
        return new Handlebars();
    }

}
