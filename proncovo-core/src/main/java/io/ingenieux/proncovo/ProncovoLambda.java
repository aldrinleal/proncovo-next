package io.ingenieux.proncovo;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.ingenieux.lambada.runtime.LambadaFunction;
import io.ingenieux.proncovo.di.CoreModule;
import io.ingenieux.proncovo.model.LookupResult;
import io.ingenieux.proncovo.services.FoursquareService;
import io.ingenieux.proncovo.services.GeocodingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProncovoLambda {
    final Logger logger = LoggerFactory.getLogger(ProncovoLambda.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    GeocodingService geo;

    @Inject
    FoursquareService foursquare;

    @LambadaFunction(name="pcv_direction_lookup", memorySize = 512, description="Venue Lookup by Address", timeout=15)
    public void invokeLookup(InputStream inputStream, OutputStream outputStream, Context ctx) throws Exception {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "32");

        Guice.createInjector(new CoreModule()).injectMembers(this);

        String address = objectMapper.readValue(inputStream, String.class);

        logger.info("address: {}", address);

        LookupResult lookupResult = geo.lookupAddress(address);

        logger.info("lookupResult: {}", lookupResult);

        ObjectNode result = foursquare.getTotal(lookupResult.getLatLng()[0], lookupResult.getLatLng()[1]);

        logger.info("result: {}", result);

        objectMapper.writeValue(outputStream, result);
    }

}
