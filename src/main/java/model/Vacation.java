package model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Vacation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private PublicServer server;

    private Integer coveredYear;
    private LocalDate startEnjoymentDate;
    private LocalDate endEnjoymentDate;
    private Integer enjoyedDays;
    private Integer pendingDays;

    public Long getId() {
        return id;
    }

    public PublicServer getServer() {
        return server;
    }

    public void setServer(PublicServer server) {
        this.server = server;
    }

    public Integer getCoveredYear() {
        return coveredYear;
    }

    public void setCoveredYear(Integer coveredYear) {
        this.coveredYear = coveredYear;
    }

    public LocalDate getStartEnjoymentDate() {
        return startEnjoymentDate;
    }

    public void setStartEnjoymentDate(LocalDate startEnjoymentDate) {
        this.startEnjoymentDate = startEnjoymentDate;
    }

    public LocalDate getEndEnjoymentDate() {
        return endEnjoymentDate;
    }

    public void setEndEnjoymentDate(LocalDate endEnjoymentDate) {
        this.endEnjoymentDate = endEnjoymentDate;
    }

    public Integer getEnjoyedDays() {
        return enjoyedDays;
    }

    public void setEnjoyedDays(Integer enjoyedDays) {
        this.enjoyedDays = enjoyedDays;
    }

    public Integer getPendingDays() {
        return pendingDays;
    }

    public void setPendingDays(Integer pendingDays) {
        this.pendingDays = pendingDays;
    }
}