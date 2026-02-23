package es.codeurjc.scam_g18.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String longDescription;

    @ManyToMany
    @JoinTable(name = "course_tags", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(
        name = "course_learning_points",
        joinColumns = @JoinColumn(name = "course_id") 
    )
    @Column(name = "learning_point") 
    private List<String> learningPoints = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
        name = "course_prerequisites",  // tabla auxiliar
        joinColumns = @JoinColumn(name = "course_id") // columna que une con Course
    )
    @Column(name = "prerequisite") // columna que guarda cada String
    private List<String> prerequisites = new ArrayList<>();


    private String language;

    private Integer durationMinutes;

    @Column(nullable = false)
    private Integer priceCents;

    private Integer subscribersNumber;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image image;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Module> modules = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public Course() {
    }

    public Course(User creator, String title, String shortDescription, String longDescription, String language, Integer durationMinutes,
            Integer priceCents, Status status, Set<Tag> tags, List<Module> modules, List<Review> reviews,
            List<String> learningPoints, List<String> prerequisites) {
        this.creator = creator;
        this.title = title;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.language = language;
        this.durationMinutes = durationMinutes;
        this.priceCents = priceCents;
        this.status = status;
        this.tags = tags;
        this.reviews = reviews;
        this.prerequisites = prerequisites;
        this.createdAt = LocalDateTime.now();
        this.subscribersNumber = 0;
        this.learningPoints = learningPoints;

        if (modules != null) {
        modules.stream()
            .sorted((m1, m2) -> m1.getOrderIndex().compareTo(m2.getOrderIndex()))
            .forEach(this::addModule);
        }
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
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

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public void addTag(Tag tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Tag cannot be null");
        }
        this.tags.add(tag);
    }


    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getPriceCents() {
        return priceCents;
    }

    public void setPriceCents(Integer priceCents) {
        this.priceCents = priceCents;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public void addModule(Module module) {
        module.setCourse(this);
        this.modules.add(module);
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Integer getSubscribersNumber() {
        return subscribersNumber;
    }
    
    public void setSubscribersNumber(Integer subscribersNumber) {
        this.subscribersNumber = subscribersNumber;
    }

    public List<String> getLearningPoints() {
        return learningPoints;
    }

    public void setLearningPoints(List<String> learningPoints) {
        this.learningPoints = learningPoints;
    }

    public List<String> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<String> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public void removeModuleById(Long moduleId) {
        this.modules.removeIf(m -> m.getId().equals(moduleId));
    }

    public void removeReviewById(Long reviewId) {
        reviews.removeIf(review -> review.getId().equals(reviewId));
    }

    public void removeTagById(Long tagId) {
        tags.removeIf(tag -> tag.getId().equals(tagId));
    }



}
