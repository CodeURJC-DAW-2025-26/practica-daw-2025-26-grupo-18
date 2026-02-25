package es.codeurjc.scam_g18.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Table;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(nullable = false)
    private String title;

    private String description;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image image;

    @Column(nullable = false)
    private Integer priceCents;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer capacity;

    private Integer attendeesCount = 0;

    private String category;
    @ManyToMany
    @JoinTable(name = "event_tags", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ElementCollection
    private List<String> speakers = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "event_id")
    private List<EventSession> sessions = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Form binding transient fields
    @Transient
    private String startDateStr;
    @Transient
    private String startTimeStr;
    @Transient
    private String endDateStr;
    @Transient
    private String endTimeStr;
    @Transient
    private String locationName;
    @Transient
    private String locationAddress;
    @Transient
    private String locationCity;
    @Transient
    private String locationCountry;
    @Transient
    private Double price;
    @Transient
    private List<String> sessionTimes;
    @Transient
    private List<String> sessionTitles;
    @Transient
    private List<String> sessionDescriptions;
    @Transient
    private List<String> speakerNames;

    public Event() {
    }

    // Getters and Setters for transient fields
    public String getStartDateStr() {
        return startDateStr;
    }

    public void setStartDateStr(String startDateStr) {
        this.startDateStr = startDateStr;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getEndDateStr() {
        return endDateStr;
    }

    public void setEndDateStr(String endDateStr) {
        this.endDateStr = endDateStr;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    public String getLocationCountry() {
        return locationCountry;
    }

    public void setLocationCountry(String locationCountry) {
        this.locationCountry = locationCountry;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<String> getSessionTimes() {
        return sessionTimes;
    }

    public void setSessionTimes(List<String> sessionTimes) {
        this.sessionTimes = sessionTimes;
    }

    public List<String> getSessionTitles() {
        return sessionTitles;
    }

    public void setSessionTitles(List<String> sessionTitles) {
        this.sessionTitles = sessionTitles;
    }

    public List<String> getSessionDescriptions() {
        return sessionDescriptions;
    }

    public void setSessionDescriptions(List<String> sessionDescriptions) {
        this.sessionDescriptions = sessionDescriptions;
    }

    public List<String> getSpeakerNames() {
        return speakerNames;
    }

    public void setSpeakerNames(List<String> speakerNames) {
        this.speakerNames = speakerNames;
    }

    public Event(User creator, Location location, Image image, String title, String description, Integer priceCents,
            LocalDateTime startDate, LocalDateTime endDate, String category, Status status, Integer capacity) {
        this.creator = creator;
        this.location = location;
        this.image = image;
        this.title = title;
        this.description = description;
        this.priceCents = priceCents;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = category;
        this.status = status;
        this.capacity = capacity; // Valor por defecto, se puede modificar con un setter
        this.attendeesCount = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Integer getPriceCents() {
        return priceCents;
    }

    public void setPriceCents(Integer priceCents) {
        this.priceCents = priceCents;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getCategory() {
        return category;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isPendingReview() {
        return Status.PENDING_REVIEW.equals(this.status);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
        if (this.capacity != null && this.capacity >= 0 && getAttendeesCount() > this.capacity) {
            this.attendeesCount = this.capacity;
        }
    }

    public Integer getAttendeesCount() {
        return attendeesCount == null ? 0 : attendeesCount;
    }

    public void setAttendeesCount(Integer attendeesCount) {
        if (attendeesCount == null || attendeesCount < 0) {
            this.attendeesCount = 0;
            return;
        }

        if (this.capacity != null && this.capacity >= 0) {
            this.attendeesCount = Math.min(attendeesCount, this.capacity);
            return;
        }

        this.attendeesCount = attendeesCount;
    }

    public Integer getRemainingSeats() {
        if (capacity == null || capacity < 0) {
            return 0;
        }
        return Math.max(capacity - getAttendeesCount(), 0);
    }

    public boolean hasAvailableSeats() {
        return getRemainingSeats() > 0;
    }

    public void incrementAttendeesCount() {
        setAttendeesCount(getAttendeesCount() + 1);
    }

    public List<String> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<String> speakers) {
        this.speakers = speakers;
    }

    public List<EventSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<EventSession> sessions) {
        this.sessions = sessions;
    }
}
