package com.unicovoit.dto;

import jakarta.validation.constraints.*;

public class SendMessageDto {

    @NotNull(message = "Le destinataire est obligatoire")
    private Long receiverId;

    private Long rideId; // Optional - for ride-specific messages

    @NotBlank(message = "Le contenu du message est obligatoire")
    @Size(max = 5000, message = "Le message ne doit pas dépasser 5000 caractères")
    private String content;

    public SendMessageDto() {}

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
