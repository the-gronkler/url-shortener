package pl.edu.pjwstk.s28259.tpo10.model;

import jakarta.persistence.*;

import java.util.Random;

@Entity
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "password")
    private String password;
    @Column(name = "name")
    private String name;
    /**
     * unique part of the short url, without the prefix
     */
    @Column(name = "shortUrlId")
    private String shortUrlId;
    @Column(name = "targetUrl")
    private String targetUrl;
    @Column(name = "visits")
    private int visits;


    public Url(){}
    public Url(String password, String name, String targetUrl, String shortUrlId){

        this.password = password;
        this.name = name;
        this.shortUrlId = shortUrlId;
        this.targetUrl = targetUrl;
        this.visits = 0;
    }


    public void incrementVisits(){
        this.visits++;
    }
    public boolean hasPassword(){
        return !this.password.isEmpty();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortUrlId() {
        return shortUrlId;
    }

    public void setShortUrlId(String shortUrlId) {
        this.shortUrlId = shortUrlId;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    @Override
    public String toString() {
        return "Url{" +
                "id=" + id +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", shortUrlId='" + shortUrlId + '\'' +
                ", targetUrl='" + targetUrl + '\'' +
                ", visits=" + visits +
                '}';
    }
}

