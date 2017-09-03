package de.bildschirmarbeiter.aem.toolbox.application;

import com.github.jknack.handlebars.Handlebars;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration<Handlebars> handlebarsServiceRegistration;

    private ServiceRegistration<Gson> gsonServiceRegistration;

    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        handlebarsServiceRegistration = bundleContext.registerService(Handlebars.class, handlebars(), null);
        gsonServiceRegistration = bundleContext.registerService(Gson.class, gson(), null);
    }

    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        handlebarsServiceRegistration.unregister();
        gsonServiceRegistration.unregister();
    }

    private Handlebars handlebars() {
        return new Handlebars();
    }

    private Gson gson() {
        return new GsonBuilder().create();
    }

}
