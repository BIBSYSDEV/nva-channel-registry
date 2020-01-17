package no.unit.nva.channel.model.outgoing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.channel.MainHandler;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertNotNull;

public class SearchResponseTest {

    @Test
    public void testObjectMapping() throws JsonProcessingException {
        ObjectMapper objectMapper = MainHandler.createObjectMapper();

        Channel channel = new Channel(
                "Original Tittel",
                "Online ISSN",
                "Print ISSN",
                1
        );

        SearchResponse response = new SearchResponse(Collections.singletonList(channel));

        SearchResponse mappedObject = objectMapper.readValue(objectMapper.writeValueAsString(response),
                SearchResponse.class);
        assertNotNull(mappedObject);
    }

    @Test
    public void testResponseHasNullAsLevel() throws JsonProcessingException {
        ObjectMapper objectMapper = MainHandler.createObjectMapper();

        Channel channel = new Channel(
                "Original Tittel",
                "Online ISSN",
                "Print ISSN",
                null
        );

        SearchResponse response = new SearchResponse(Collections.singletonList(channel));

        SearchResponse mappedObject = objectMapper.readValue(objectMapper.writeValueAsString(response),
                SearchResponse.class);
        assertNotNull(mappedObject);
    }

}
