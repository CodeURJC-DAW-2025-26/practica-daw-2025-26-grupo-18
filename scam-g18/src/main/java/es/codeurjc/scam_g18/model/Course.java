package es.codeurjc.scam_g18.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
public class Course {
    
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id = null;
    
    private int price;

    private long creatorId;

    private String title;

    private String content;

    private String languaje;

    private int duration;

    private int subscribedUser;

    private int rating;

    private boolean adminAccepted;

    private long adminId;

    private boolean published;

    private LocalDateTime createdAt;

    @OneToOne
	private Image_Course image;

    public Course() {        
    }

    
}
