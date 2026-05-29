package model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class AdministrativeSituation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private PublicServer server;

    private String situationType;
    private LocalDate startDate;
    private LocalDate endDate;

    @Column(length = 1000)
    private String description;

    private String administrativeActId;

    public Long getId() {
        return id;
    }

    public PublicServer getServer() {
        return server;
    }

    public void setServer(PublicServer server) {
        this.server = server;
    }

    public String getSituationType() {
        return situationType;
    }

    public void setSituationType(String situationType) {
        this.situationType = situationType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdministrativeActId() {
        return administrativeActId;
    }

    public void setAdministrativeActId(String administrativeActId) {
        this.administrativeActId = administrativeActId;
    }
}