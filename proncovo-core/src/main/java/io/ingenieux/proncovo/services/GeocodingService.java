package io.ingenieux.proncovo.services;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.ingenieux.proncovo.model.LookupResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeocodingService extends ServiceBase {
    final Logger logger = LoggerFactory.getLogger(GeocodingService.class);

    public LookupResult lookupAddress(String addr) throws Exception {
        String url = String
                .format("http://maps.googleapis.com/maps/api/geocode/json?address=%s&sensor=false",
                        encode(addr));

        ObjectNode result = get(url);

        if (logger.isDebugEnabled()) {
            logger.debug("addr: {}, result: {}", addr, result);
        }

        LookupResult lookupResult = new LookupResult();

        try {
            Double[] serviceResult = parseLocationResult(result);

            lookupResult.setStatus("OK");
            lookupResult.setLatLng(serviceResult);
        } catch (IllegalStateException e) {
            lookupResult.setStatus(e.getMessage());
        } catch (Exception e) {
            lookupResult.setStatus("INTERNAL ERROR" + e.getMessage());
        }

        return lookupResult;
    }

    Double[] parseLocationResult(ObjectNode result) throws Exception {
        if (!result.get("status").isTextual()
                || !"OK".equals(result.get("status").textValue()))
            throw new IllegalStateException("ERROR: Invalid Search Result");

        ArrayNode resultArray = ((ArrayNode) result.get("results"));

        if (1 != resultArray.size()) {
            int len = resultArray.size();

            if (0 == len) {
                throw new IllegalStateException("ERROR: No address found");
            } else {
                throw new IllegalStateException(
                        "ERROR: No single address found");
            }
        }

        ObjectNode addressNode = (ObjectNode) resultArray.get(0);

        ObjectNode locationNode = (ObjectNode) addressNode.get("geometry").get(
                "location");

        Double lat = locationNode.get("lat").asDouble();
        Double lng = locationNode.get("lng").asDouble();

        return new Double[]{lat, lng};
    }

}
