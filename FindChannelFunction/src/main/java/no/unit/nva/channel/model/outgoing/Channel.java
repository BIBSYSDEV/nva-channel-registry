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

    @JsonProperty("openAccess")
    private Boolean openAccess;

    /**
     * Constructor for Channel.
     *
     * @param originalTitle original title
     * @param onlineIssn    online ISSN
     * @param printIssn print ISSN
     * @param level level
     * @param openAccess open access
     */
    public Channel(String originalTitle, String onlineIssn, String printIssn, Integer level, Boolean openAccess) {
        this.originalTitle = originalTitle;
        this.onlineIssn = onlineIssn;
        this.printIssn = printIssn;
        this.level = level;
        this.openAccess = openAccess;
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

    public Boolean getOpenAccess() {
        return openAccess;
    }

    public void setOpenAccess(Boolean openAccess) {
        this.openAccess = openAccess;
    }
}
