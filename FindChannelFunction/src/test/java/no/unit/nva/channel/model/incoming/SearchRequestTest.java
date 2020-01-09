package no.unit.nva.channel.model.incoming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class SearchRequestTest {

    @Test
    public void testObjectMapping() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SearchRequest searchRequest = new SearchRequest(851, "Search!");

        SearchRequest mappedObject = objectMapper.readValue(objectMapper.writeValueAsString(searchRequest), SearchRequest.class);
        assertNotNull(mappedObject);
    }
}
