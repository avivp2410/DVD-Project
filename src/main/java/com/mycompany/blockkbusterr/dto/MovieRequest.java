package com.mycompany.blockkbusterr.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "movieRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class MovieRequest {
    
    @XmlElement(required = true)
    private String title;
    
    @XmlElement(required = true)
    private String releaseDate; // Format: YYYY-MM-DD
    
    @XmlElement(required = true)
    private Integer duration;
    
    @XmlElement(required = true)
    private String genre;
    
    @XmlElement(required = true)
    private Integer quantity;
    
    @XmlElement
    private String description;
    
    @XmlElement
    private String imageUrl;
    
    // Default constructor for JAXB
    public MovieRequest() {}
    
    public MovieRequest(String title, String releaseDate, Integer duration, String genre, Integer quantity) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genre = genre;
        this.quantity = quantity;
    }
    
    public MovieRequest(String title, String releaseDate, Integer duration, String genre, Integer quantity, String description, String imageUrl) {
        this(title, releaseDate, duration, genre, quantity);
        this.description = description;
        this.imageUrl = imageUrl;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getReleaseDate() {
        return releaseDate;
    }
    
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public String getGenre() {
        return genre;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}