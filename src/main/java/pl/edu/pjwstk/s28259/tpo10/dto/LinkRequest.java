package pl.edu.pjwstk.s28259.tpo10.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public class LinkRequest {
    @Size(min = 5, max = 20, message = "Name must be between 5 and 20 characters")
    private String name;

    @Pattern(regexp = "^https://.*", message = "URL must start with https://")
    @URL(message = "Invalid URL")
    private String targetUrl;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z]{2,})(?=.*\\d{3,})(?=.*[!@#$%^&*]{4,}).{10,}$",
            message = "Password must have at least one lowercase letter, two uppercase letters, three digits, four special characters and be at least 10 characters long")
    private String password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
