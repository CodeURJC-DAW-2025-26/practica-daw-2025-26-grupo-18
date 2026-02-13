package es.codeurjc.scam_g18.model;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;


@Entity(name = "UserTable")
public class User {

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String userName;

    private String userPassword;

    private String genre;

    private String age;

    private String country;

    private String userEmail;

    private LocalDateTime createdAt;

    @OneToOne
    private Image_User image;
    
    private LocalDateTime subscriptionDate;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;
    
    public User() {
	}

    public User(String userName, String userPassword, String genre, String age, String country, String userEmail,
            LocalDateTime createdAt, String... roles) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.genre = genre;
        this.age = age;
        this.country = country;
        this.userEmail = userEmail;
        this.createdAt = createdAt;
        this.roles = List.of(roles);
    }
    
    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setUserEncodedPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Image_User getUserImage() {
        return image;
    }

    public void setUserImage(Image_User userImage) {
        this.image = userImage;
    }

    public LocalDateTime getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(LocalDateTime subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void addRole(String role) {
        if (!this.roles.contains(role)) {
            this.roles.add(role);
        }
    }

    public void removeRole(String role) {
        this.roles.remove(role);
    }

    public boolean hasRole(String role) {
        return this.roles.contains(role);
    }

}
