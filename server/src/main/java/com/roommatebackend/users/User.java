package com.roommatebackend.users;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table (name = "users")
public class User implements Serializable {

    public User() {}; // not arg constructor

    // Not including userID in the constructor to restrict external control during construction
    public User(String firstname, String lastname, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String hashedPassword;


    // Getters
    public UUID getUserId() {return this.userId;}

    public String getFirstname() {return this.firstname;}

    public String getLastname() {return this.lastname;}

    public String getEmail() {return this.email;}

    // Setters

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

}
