package pl.edu.pjwstk.s28259.tpo10.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LinkDto {
    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String targetUrl;

    /**
     * id with the prefix. Actual usable url for the end user
     */
    @JsonProperty
    private String redirectUrl;

    @JsonProperty
    private int visits;

    public LinkDto(){}

    public LinkDto(String id, String name, String targetUrl, String redirectUrl, int visits) {
        this.id = id;
        this.name = name;
        this.targetUrl = targetUrl;
        this.redirectUrl = redirectUrl;
        this.visits = visits;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }
}
