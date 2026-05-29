package model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private PublicServer server;

    private String permissionType;
    private LocalDate date;
    private Integer numberOfDays;

    @Column(length = 1000)
    private String justification;

    public Long getId() {
        return id;
    }

    public PublicServer getServer() {
        return server;
    }

    public void setServer(PublicServer server) {
        this.server = server;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(Integer numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }
}