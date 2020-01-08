package no.unit.nva.channel.model.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Collections;

public class FetchJsonTableDataRequestTest {

    @Test
    public void testObjectMapping() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        Selection selection = new Selection("like", Collections.singletonList("%test%"));
        Filter filter = new Filter("Original Tittel", selection);
        FetchJsonTableDataRequest request = new FetchJsonTableDataRequest(
                851,
                1,
                "N",
                10,
                "J",
                ".",
                Collections.singletonList("*"),
                Collections.emptyList(),
                Collections.singletonList(filter)
        );

        objectMapper.readValue(objectMapper.writeValueAsString(request), FetchJsonTableDataRequest.class);
    }

}
