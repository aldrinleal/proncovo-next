package io.ingenieux.proncovo.di;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.name.Names;
import io.ingenieux.proncovo.model.VenueAggregator;
import io.ingenieux.proncovo.services.FoursquareService;
import io.ingenieux.proncovo.services.GeocodingService;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.nnsoft.guice.rocoto.configuration.ConfigurationModule;

import javax.inject.Singleton;
import java.net.URI;

public class CoreModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new CfgModule());

        bind(FoursquareService.class).in(Singleton.class);
        bind(GeocodingService.class).in(Singleton.class);

        requestStaticInjection(VenueAggregator.class);

        bind(HttpClient.class).to(DefaultHttpClient.class);
    }

    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    private class CfgModule extends ConfigurationModule {
        @Override
        protected void bindConfigurations() {
            bindProperties(URI.create("classpath:/proncovo.properties"));
        }
    }
}
