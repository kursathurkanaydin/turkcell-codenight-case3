package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "local_users")
public class User {
    //user_id, name, city, segment
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "city")
    private String city;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "risk_profile_id")
    private RiskProfile riskProfile;

    @Column(name = "segment")
    private String segment;

    @Column(name = "is_blocked")
    private boolean isBlocked = false;

    public User() {
    }

    public User(String id, String name, String city, String segment) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.segment = segment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public RiskProfile getRiskProfile() {
        return riskProfile;
    }

    public void setRiskProfile(RiskProfile riskProfile) {
        this.riskProfile = riskProfile;
    }
}
