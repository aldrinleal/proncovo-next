package io.ingenieux.proncovo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

public class ServiceBase {
    @Inject
    protected ObjectMapper objectMapper;

    @Inject
    protected HttpClient httpClient;

    protected String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    protected ObjectNode get(String url) throws ClientProtocolException,
            IOException {
        HttpGet request = new HttpGet(url);

        HttpResponse result = httpClient.execute(request);

        HttpEntity entity = result.getEntity();

        String asString = toString(entity.getContent());

        return objectMapper.readValue(asString, ObjectNode.class);
    }

    protected String toString(InputStream content) throws IOException {
        return IOUtils.toString(content, "UTF-8");
    }
}
