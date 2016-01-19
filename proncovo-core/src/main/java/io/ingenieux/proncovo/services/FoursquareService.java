package io.ingenieux.proncovo.services;

import java.io.IOException;
import java.util.*;

import javax.inject.Named;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.Category;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;
import io.ingenieux.proncovo.model.VenueAggregator;

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

        List<CompactVenue> venueList = new ArrayList<CompactVenue>();

        String ll = lat + "," + lng;
        for (String categoryId : this.categories.keySet()) {
            Map<String, String> params = new LinkedHashMap<>();

            params.put("ll", ll);
            params.put("intent", "browse");
            params.put("limit", "50");
            params.put("category", categoryId);
            params.put("radius", "10000");

            Result<VenuesSearchResult> browseResult = api.venuesSearch(params);

            if (200 != browseResult.getMeta().getCode())
                continue;

            CompactVenue[] venuesArray = browseResult.getResult().getVenues();

            venueList.addAll(Arrays.asList(venuesArray));
        }

        return parseResult(venueList);
    }

    ObjectNode parseResult(List<CompactVenue> venueList)
        throws IOException, FoursquareApiException {
        VenueAggregator venueAggregator = new VenueAggregator(loadCategories());

        return (ObjectNode) objectMapper.readTree(venueAggregator
                .aggregatePlaces(venueList));
    }

    protected Map<String, Category> loadCategories()
            throws FoursquareApiException {
        if (null == this.categories) {
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
}
