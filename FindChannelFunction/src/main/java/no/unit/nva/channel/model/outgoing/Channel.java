package no.unit.nva.channel.model.outgoing;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Channel {

    @JsonProperty("title")
    private String originalTitle;

    @JsonProperty("onlineIssn")
    private String onlineIssn;

    @JsonProperty("printIssn")
    private String printIssn;

    @JsonProperty("level")
    private Integer level;

    public Channel(String originalTitle, String onlineIssn, String printIssn, Integer level) {
        this.originalTitle = originalTitle;
        this.onlineIssn = onlineIssn;
        this.printIssn = printIssn;
        this.level = level;
    }

    public Channel() {

    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOnlineIssn() {
        return onlineIssn;
    }

    public void setOnlineIssn(String onlineIssn) {
        this.onlineIssn = onlineIssn;
    }

    public String getPrintIssn() {
        return printIssn;
    }

    public void setPrintIssn(String printIssn) {
        this.printIssn = printIssn;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

}
