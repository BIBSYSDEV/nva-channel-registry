package no.unit.nva.channel.model.incoming;

public class SearchRequest {

    private String searchTerm;

    public SearchRequest() {};

    public SearchRequest(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}
