package no.unit.nva.channel.model.outgoing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Collections;

public class SearchResponseTest {

    @Test
    public void test() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        Channel channel = new Channel(
                "Original Tittel",
                "Online ISSN",
                "Print ISSN",
                1,
                "Forlag"
        );

        SearchResponse response = new SearchResponse(Collections.singletonList(channel));

        String json = objectMapper.writeValueAsString(response);

        System.out.println(json);
    }

    @Test
    public void testResponseHasNullAsLevel() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        Channel channel = new Channel(
                "Original Tittel",
                "Online ISSN",
                "Print ISSN",
                null,
                "Forlag"
        );

        SearchResponse response = new SearchResponse(Collections.singletonList(channel));

        String json = objectMapper.writeValueAsString(response);

        System.out.println(json);
    }

}
