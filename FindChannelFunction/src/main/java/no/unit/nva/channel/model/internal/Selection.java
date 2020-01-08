package no.unit.nva.channel.model.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Selection {

    @JsonProperty("filter")
    private String filter;

    @JsonProperty("values")
    private List<String> values;

    public Selection() {
    }

    public Selection(String filter, List<String> values) {
        this.filter = filter;
        this.values = values;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
