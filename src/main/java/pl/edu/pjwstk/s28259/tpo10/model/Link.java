package pl.edu.pjwstk.s28259.tpo10.model;

import jakarta.persistence.*;

@Entity
public class Link {
    /**
     * unique part of the short url, without the prefix
     */
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "targetUrl")
    private String targetUrl;

    @Column(name = "visits")
    private int visits;


    public Link(){}
    protected Link(String id, String password, String name, String targetUrl){

        this.id = id;
        this.password = password;
        this.name = name;
        this.targetUrl = targetUrl;
        this.visits = 0;
    }


    public void incrementVisits(){
        this.visits++;
    }
    public boolean hasPassword(){
        return !this.password.isEmpty();
    }
    public boolean isPasswordCorrect(String password){
        return this.password.equals(password);
    }






    public String getId() {
        return id;
    }

    protected void setId(String id) {
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
        return "Link{" +
                "id=" + id +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", targetUrl='" + targetUrl + '\'' +
                ", visits=" + visits +
                '}';
    }
}

