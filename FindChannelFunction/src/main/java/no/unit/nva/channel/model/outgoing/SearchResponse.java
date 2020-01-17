package no.unit.nva.channel.model.outgoing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SearchResponse {

    private final List<Channel> results;

    @JsonCreator
    public SearchResponse(
            @JsonProperty("results") List<Channel> results) {
        this.results = results;
    }

    public List<Channel> getResults() {
        return results;
    }
}
