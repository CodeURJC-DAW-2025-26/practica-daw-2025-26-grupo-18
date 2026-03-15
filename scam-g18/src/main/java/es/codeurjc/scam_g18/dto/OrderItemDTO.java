package es.codeurjc.scam_g18.dto;

public class OrderItemDTO {
    private Long id;
    private CourseDTO course;
    private EventDTO event;
    private Integer priceAtPurchaseCents;
    private String priceInEuros;
    private boolean isSubscription;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CourseDTO getCourse() { return course; }
    public void setCourse(CourseDTO course) { this.course = course; }

    public EventDTO getEvent() { return event; }
    public void setEvent(EventDTO event) { this.event = event; }

    public Integer getPriceAtPurchaseCents() { return priceAtPurchaseCents; }
    public void setPriceAtPurchaseCents(Integer priceAtPurchaseCents) { this.priceAtPurchaseCents = priceAtPurchaseCents; }

    public String getPriceInEuros() { return priceInEuros; }
    public void setPriceInEuros(String priceInEuros) { this.priceInEuros = priceInEuros; }

    public boolean getIsSubscription() { return isSubscription; }
    // Mustache template requires an isSubscription() method 
    public boolean isSubscription() { return isSubscription; }
    public void setIsSubscription(boolean subscription) { this.isSubscription = subscription; }
}
