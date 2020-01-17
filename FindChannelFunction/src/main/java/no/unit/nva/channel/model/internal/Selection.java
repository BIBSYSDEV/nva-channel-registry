package no.unit.nva.channel.model.internal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Selection {

    private final String filter;
    private final List<String> values;

    @JsonCreator
    public Selection(
            @JsonProperty("filter") String filter,
            @JsonProperty("values") List<String> values) {
        this.filter = filter;
        this.values = values;
    }

    public String getFilter() {
        return filter;
    }

    public List<String> getValues() {
        return values;
    }
}
