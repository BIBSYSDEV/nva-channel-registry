package no.unit.nva.channel.model.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Filter {

    @JsonProperty("variabel")
    private String variabel;

    @JsonProperty("selection")
    private Selection selection;

    public Filter() {
    }

    public Filter(String variabel, Selection selection) {
        this.variabel = variabel;
        this.selection = selection;
    }

    public String getVariabel() {
        return variabel;
    }

    public void setVariabel(String variabel) {
        this.variabel = variabel;
    }

    public Selection getSelection() {
        return selection;
    }

    public void setSelection(Selection selection) {
        this.selection = selection;
    }
}
