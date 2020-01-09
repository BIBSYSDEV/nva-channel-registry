package no.unit.nva.channel.model.outgoing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ErrorMessageTest {

    @Test
    public void testObjectMapping() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorMessage errorMessage = new ErrorMessage("Error!");

        ErrorMessage mappedObject = objectMapper.readValue(objectMapper.writeValueAsString(errorMessage), ErrorMessage.class);
        assertNotNull(mappedObject);
    }
}
