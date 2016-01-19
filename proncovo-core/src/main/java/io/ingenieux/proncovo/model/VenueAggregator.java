package io.ingenieux.proncovo.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.foyt.foursquare.api.entities.Category;
import fi.foyt.foursquare.api.entities.CompactVenue;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

public class VenueAggregator {
    @Inject
    public static ObjectMapper objectMapper;

    private Map<String, Category> categories;

    private Map<String, String> catMap;

    public VenueAggregator(Map<String, Category> categories) {
        this.categories = categories;
    }

    public String aggregatePlaces(List<CompactVenue> venues) throws IOException {
        Map<String, Set<Local>> localMap = new LinkedHashMap<String, Set<Local>>();

        for (CompactVenue v : venues) {
            String topCategory = getTopCategory(v);
            Set<Local> localSet = null;

            if (!localMap.containsKey(topCategory)) {
                localSet = new TreeSet<Local>();

                localMap.put(topCategory, localSet);
            } else {
                localSet = localMap.get(topCategory);
            }

            localSet.add(getLocal(v));
        }

        return objectMapper.writeValueAsString(localMap);
    }

    private Local getLocal(CompactVenue v) {
        Local local = new Local();

        local.setId(v.getId());
        double d = v.getLocation().getDistance();
        local.setDistancia((int) d);
        Long c = v.getHereNow().getCount();
        local.setPessoas((int) c.longValue());

        local.setIcone(getIconFor(v));

        if (null == local.getIcone()) {
            int i = 0;

            i++;
        }

        local.setNome(v.getName());

        return local;
    }

    private String getIconFor(CompactVenue v) {
        /*
        String topCategory = getTopCategory(v);

        if (null == catMap) {
            this.catMap = new TreeMap<String, String>();

            for (Category c : this.categories.values()) {
                String id = c.getId(); // .replaceAll("\\.png$", "_64.png");

                catMap.put(c.getName(), id);
            }
        }

        String icon = catMap.get(topCategory);

        if (null == icon)
            icon = v.getCategories()[0].getIcon(); // .replaceAll("\\.png$", "_64.png");
        return icon;
        */
        return null;
    }

    private String getTopCategory(CompactVenue v) {
        for (Category c : v.getCategories()) {
            if (null != c.getParents() && 0 != c.getParents().length)
                return c.getParents()[0];

            return c.getName();
        }

        throw new IllegalStateException();
    }
}
