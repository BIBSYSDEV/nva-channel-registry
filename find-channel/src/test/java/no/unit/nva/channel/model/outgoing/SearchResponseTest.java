package no.unit.nva.channel.model.outgoing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.MalformedURLException;
import java.net.URL;
import no.unit.nva.channel.MainHandler;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertNotNull;

public class SearchResponseTest {

    @Test
    public void testObjectMapping() throws JsonProcessingException, MalformedURLException {
        ObjectMapper objectMapper = MainHandler.createObjectMapper();

        Channel channel = new Channel(
                "Original Tittel",
                "Online ISSN",
                "Print ISSN",
                1,
                null,
                new URL("http://example.org/123")
        );

        SearchResponse response = new SearchResponse(Collections.singletonList(channel));

        SearchResponse mappedObject = objectMapper.readValue(objectMapper.writeValueAsString(response),
                SearchResponse.class);
        assertNotNull(mappedObject);
    }

    @Test
    public void testResponseHasNullAsLevel() throws JsonProcessingException, MalformedURLException {
        ObjectMapper objectMapper = MainHandler.createObjectMapper();

        Channel channel = new Channel(
                "Original Tittel",
                "Online ISSN",
                "Print ISSN",
                null,
                true,
                new URL("http://example.org/123")
        );

        SearchResponse response = new SearchResponse(Collections.singletonList(channel));

        SearchResponse mappedObject = objectMapper.readValue(objectMapper.writeValueAsString(response),
                SearchResponse.class);
        assertNotNull(mappedObject);
    }

}
