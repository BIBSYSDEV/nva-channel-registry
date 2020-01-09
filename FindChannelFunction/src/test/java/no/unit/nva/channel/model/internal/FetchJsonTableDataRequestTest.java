package no.unit.nva.channel.model.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.channel.App;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class FetchJsonTableDataRequestTest {

    @Test
    public void testObjectMapping() throws JsonProcessingException {

        ObjectMapper objectMapper = App.createObjectMapper();

        Selection selection = new Selection("like", Collections.singletonList("%test%"));
        Filter filter = new Filter("Original tittel", selection);
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

        FetchJsonTableDataRequest mappedObject = objectMapper.readValue(objectMapper.writeValueAsString(request), FetchJsonTableDataRequest.class);
        Assert.assertNotNull(mappedObject);
    }

}
