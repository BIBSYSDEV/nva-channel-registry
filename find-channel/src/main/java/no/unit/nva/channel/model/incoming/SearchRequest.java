package no.unit.nva.channel.model.incoming;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchRequest {

    private final Integer tableId;
    private final String searchTerm;

    public SearchRequest(
            @JsonProperty(value = "tableId", required = true) Integer tableId,
            @JsonProperty(value = "searchTerm", required = true) String searchTerm
    ) {
        this.tableId = tableId;
        this.searchTerm = searchTerm;
    }

    public Integer getTableId() {
        return tableId;
    }

    public String getSearchTerm() {
        return searchTerm;
    }
}
