package io.ingenieux.proncovo.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Named;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Guice;
import com.google.inject.Inject;

import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.Category;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;
import io.ingenieux.proncovo.di.CoreModule;
import io.ingenieux.proncovo.model.VenueAggregator;
import org.apache.commons.lang3.SerializationUtils;

import static java.util.Arrays.asList;

public class FoursquareService extends ServiceBase {
    @Inject
    @Named("proncovo.consumerKey")
    String clientId;

    @Inject
    @Named("proncovo.consumerSecret")
    String clientSecret;

    private Map<String, Category> categories;

    public ObjectNode getTotal(Double lat, Double lng /*, String accessToken */)
            throws Exception {
        // TODO What about AccessToken limits btw?
        FoursquareApi api = new FoursquareApi(clientId, clientSecret, "");

        if (null == categories)
            categories = loadCategories();

        String ll = lat + "," + lng;

        List<CompactVenue> venueList = categories.values().parallelStream().flatMap(cat -> {
            Map<String, String> params = new LinkedHashMap<>();

            params.put("ll", ll);
            params.put("intent", "browse");
            params.put("limit", "50");
            params.put("categoryId", cat.getId());
            params.put("radius", "10000");

            for (int i = 0; i < 4; i++) {
                try {
                    Result<VenuesSearchResult> browseResult = api.venuesSearch(params);

                    final CompactVenue[] venues = browseResult.getResult().getVenues();

                    Arrays.sort(venues, ((o1, o2) -> o1.getLocation().getDistance().compareTo(o2.getLocation().getDistance())));

                    return Stream.of(venues);
                } catch (FoursquareApiException e) {
                    if (3 == i)
                        throw new RuntimeException(e);
                }
            }

            return Stream.empty();
        }).collect(Collectors.<CompactVenue>toList());

        return parseResult(venueList);
    }

    ObjectNode parseResult(List<CompactVenue> venueList)
        throws IOException, FoursquareApiException {
        VenueAggregator venueAggregator = new VenueAggregator(loadCategories());

        return (ObjectNode) objectMapper.readTree(venueAggregator
                .aggregatePlaces(venueList));
    }

    public Map<String, Category> loadCategories()
            throws FoursquareApiException {
        if (null == this.categories) {
            final InputStream is = getClass().getClassLoader().getResourceAsStream("categories.ser");

            if (null != is) {
                return SerializationUtils.deserialize(is);
            }

            FoursquareApi api = new FoursquareApi(this.clientId, clientSecret,
                    null);
            Result<Category[]> venuesCategories = api.venuesCategories();
            categories = new TreeMap<String, Category>();
            for (Category c : venuesCategories.getResult()) {
                categories.put(c.getId(), c);
            }
        }

        return categories;
    }

    public static void main(String[] args) throws Exception {
        FoursquareService service = Guice.createInjector(new CoreModule()).getInstance(FoursquareService.class);

        SerializationUtils.serialize((Serializable) service.loadCategories(), new FileOutputStream("src/main/resources/categories.ser"));
    }
}
