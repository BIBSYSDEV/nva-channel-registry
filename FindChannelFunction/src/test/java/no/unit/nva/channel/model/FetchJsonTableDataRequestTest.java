package no.unit.nva.channel.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.channel.model.internal.FetchJsonTableDataRequest;
import no.unit.nva.channel.model.internal.Filter;
import no.unit.nva.channel.model.internal.Selection;
import org.junit.Test;

import java.util.Collections;

public class FetchJsonTableDataRequestTest {

    @Test
    public void test() throws JsonProcessingException {

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

        String json = objectMapper.writeValueAsString(request);

        System.out.println(json);

    }

}
