package no.unit.nva.channel.model.internal;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class FetchJsonTableDataRequest {

    @JsonProperty("tabell_id")
    private Integer tableId;

    @JsonProperty("api_versjon")
    private Integer apiVersion;

    @JsonProperty("statuslinje")
    private String statusLine;

    @JsonProperty("begrensning")
    private Integer limit;

    @JsonProperty("kodetekst")
    private String codeText;

    @JsonProperty("desimal_separator")
    private String decimalSeparator;

    @JsonProperty("variabler")
    private List<String> variables;

    @JsonProperty("sortBy")
    private List<String> sortBy;

    @JsonProperty("filter")
    private List<Filter> filter;

    public FetchJsonTableDataRequest() {
    }

    public FetchJsonTableDataRequest(Integer tableId, Integer apiVersion, String statusLine, Integer limit, String codeText, String decimalSeparator, List<String> variables, List<String> sortBy, List<Filter> filter) {
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

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public Integer getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(Integer apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(String statusLine) {
        this.statusLine = statusLine;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getCodeText() {
        return codeText;
    }

    public void setCodeText(String codeText) {
        this.codeText = codeText;
    }

    public String getDecimalSeparator() {
        return decimalSeparator;
    }

    public void setDecimalSeparator(String decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    public List<String> getVariables() {
        return variables;
    }

    public void setVariables(List<String> variables) {
        this.variables = variables;
    }

    public List<String> getSortBy() {
        return sortBy;
    }

    public void setSortBy(List<String> sortBy) {
        this.sortBy = sortBy;
    }

    public List<Filter> getFilter() {
        return filter;
    }

    public void setFilter(List<Filter> filter) {
        this.filter = filter;
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
