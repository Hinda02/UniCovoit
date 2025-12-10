package com.unicovoit.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'expéditeur est obligatoire")
    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserAccount sender;

    @NotNull(message = "Le destinataire est obligatoire")
    @ManyToOne(optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private UserAccount receiver;

    @ManyToOne
    @JoinColumn(name = "ride_id")
    private Ride ride;

    @NotBlank(message = "Le contenu du message est obligatoire")
    @Size(max = 5000, message = "Le message ne doit pas dépasser 5000 caractères")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @PrePersist
    public void prePersist() {
        sentAt = LocalDateTime.now();
    }

    // Getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserAccount getSender() {
        return sender;
    }

    public void setSender(UserAccount sender) {
        this.sender = sender;
    }

    public UserAccount getReceiver() {
        return receiver;
    }

    public void setReceiver(UserAccount receiver) {
        this.receiver = receiver;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
