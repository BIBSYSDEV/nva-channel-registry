package no.unit.nva.channel.model.outgoing;

import java.util.List;

public class SearchResponse {

    private List<Channel> results;

    public SearchResponse() {
    }

    public SearchResponse(List<Channel> results) {
        this.results = results;
    }

    public List<Channel> getResults() {
        return results;
    }

    public void setResults(List<Channel> results) {
        this.results = results;
    }
}