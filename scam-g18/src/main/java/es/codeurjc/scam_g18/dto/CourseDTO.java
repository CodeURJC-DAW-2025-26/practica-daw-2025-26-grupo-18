package es.codeurjc.scam_g18.dto;

import es.codeurjc.scam_g18.model.Status;

public class CourseDTO {
    private Long id;
    private String title;
    private String shortDescription;
    private Integer priceCents;
    private Status status;
    private String language;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public Integer getPriceCents() { return priceCents; }
    public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}
