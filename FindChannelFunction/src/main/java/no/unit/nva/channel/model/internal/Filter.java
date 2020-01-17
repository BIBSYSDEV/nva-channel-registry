package no.unit.nva.channel.model.internal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Filter {

    @JsonProperty("variabel")
    private final String variabel;
    @JsonProperty("selection")
    private final Selection selection;

    @JsonCreator
    public Filter(
            @JsonProperty("variabel") String variabel,
            @JsonProperty("selection") Selection selection) {
        this.variabel = variabel;
        this.selection = selection;
    }

    public String getVariabel() {
        return variabel;
    }

    public Selection getSelection() {
        return selection;
    }
}
