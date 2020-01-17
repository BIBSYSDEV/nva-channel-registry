package no.unit.nva.channel.model.internal;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class FetchJsonTableDataRequest {

    @JsonProperty("tabell_id")
    private final Integer tableId;
    @JsonProperty("api_versjon")
    private final Integer apiVersion;
    @JsonProperty("statuslinje")
    private final String statusLine;
    @JsonProperty("begrensning")
    private final Integer limit;
    @JsonProperty("kodetekst")
    private final String codeText;
    @JsonProperty("desimal_separator")
    private final String decimalSeparator;
    @JsonProperty("variabler")
    private final List<String> variables;
    @JsonProperty("sortBy")
    private final List<String> sortBy;
    @JsonProperty("filter")
    private final List<Filter> filter;

    @JsonCreator
    public FetchJsonTableDataRequest(
            @JsonProperty("tabell_id") Integer tableId,
            @JsonProperty("api_versjon") Integer apiVersion,
            @JsonProperty("statuslinje") String statusLine,
            @JsonProperty("begrensning") Integer limit,
            @JsonProperty("kodetekst") String codeText,
            @JsonProperty("desimal_separator") String decimalSeparator,
            @JsonProperty("variabler") List<String> variables,
            @JsonProperty("sortBy") List<String> sortBy,
            @JsonProperty("filter") List<Filter> filter) {
        this.tableId = tableId;
        this.apiVersion = apiVersion;
        this.statusLine = statusLine;
        this.limit = limit;
        this.codeText = codeText;
        this.decimalSeparator = decimalSeparator;
        this.variables = variables;
        this.sortBy = sortBy;
        this.filter = filter;
    }

    public Integer getTableId() {
        return tableId;
    }

    public Integer getApiVersion() {
        return apiVersion;
    }

    public String getStatusLine() {
        return statusLine;
    }

    public Integer getLimit() {
        return limit;
    }

    public String getCodeText() {
        return codeText;
    }

    public String getDecimalSeparator() {
        return decimalSeparator;
    }

    public List<String> getVariables() {
        return variables;
    }

    public List<String> getSortBy() {
        return sortBy;
    }

    public List<Filter> getFilter() {
        return filter;
    }

    public static FetchJsonTableDataRequest create(Integer id, String searchTerm) {
        Selection selection = new Selection("like", Collections.singletonList(searchTerm));
        Filter filter = new Filter("Original Tittel", selection);
        return new FetchJsonTableDataRequest(
                id,
                1,
                "N",
                10,
                "J",
                ".",
                Collections.singletonList("*"),
                Collections.emptyList(),
                Collections.singletonList(filter)
        );
    }
}
