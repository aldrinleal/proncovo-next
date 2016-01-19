package io.ingenieux.proncovo;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.ingenieux.proncovo.di.CoreModule;
import io.ingenieux.proncovo.model.LookupResult;
import io.ingenieux.proncovo.services.GeocodingService;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;

public class GeocodingServiceTest {
    @Inject
    GeocodingService geo;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new CoreModule());

        injector.injectMembers(this);
    }

    @Test
    public void testGeolocationService() throws Exception {
        final LookupResult lookupResult = geo.lookupAddress("Cl 45a Sur # 39b 226 Envigado Antioquia");

        System.err.println(lookupResult);

        assertThat(lookupResult, is(not(nullValue())));
        assertThat(lookupResult.getLatLng()[0], is(equalTo(6.1619109d)));
        assertThat(lookupResult.getLatLng()[1], is(equalTo(-75.5915535d)));
    }
}
