package es.codeurjc.scam_g18.dto;

import java.time.LocalDateTime;
import es.codeurjc.scam_g18.model.Status;

public class EventDTO {
    private Long id;
    private String title;
    private String description;
    private Integer priceCents;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer capacity;
    private String category;
    private Status status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPriceCents() { return priceCents; }
    public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
