package com.unicovoit.service;

import com.unicovoit.dao.MessageDao;
import com.unicovoit.dao.RideDao;
import com.unicovoit.dao.UserAccountDao;
import com.unicovoit.dto.SendMessageDto;
import com.unicovoit.entity.Message;
import com.unicovoit.entity.Ride;
import com.unicovoit.entity.UserAccount;
import com.unicovoit.exception.ResourceNotFoundException;
import com.unicovoit.exception.ValidationException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class MessageService {

    private final MessageDao messageDao;
    private final UserAccountDao userAccountDao;
    private final RideDao rideDao;

    public MessageService(MessageDao messageDao, UserAccountDao userAccountDao, RideDao rideDao) {
        this.messageDao = messageDao;
        this.userAccountDao = userAccountDao;
        this.rideDao = rideDao;
    }

    /**
     * Send a message
     */
    @Transactional
    public Message sendMessage(@Valid SendMessageDto dto, UserAccount sender) {
        if (sender == null) {
            throw new ValidationException("L'expéditeur est obligatoire.");
        }

        // Get receiver
        UserAccount receiver = userAccountDao.findById(dto.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", dto.getReceiverId()));

        // Check if sender is trying to send message to themselves
        if (sender.getId().equals(receiver.getId())) {
            throw new ValidationException("Vous ne pouvez pas vous envoyer un message à vous-même.");
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(dto.getContent());
        message.setRead(false);

        // Optionally associate with a ride
        if (dto.getRideId() != null) {
            Ride ride = rideDao.findById(dto.getRideId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trajet", dto.getRideId()));
            message.setRide(ride);
        }

        return messageDao.save(message);
    }

    /**
     * Mark a message as read
     */
    @Transactional
    public void markAsRead(Long messageId, UserAccount receiver) {
        Message message = messageDao.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", messageId));

        // Check if user is the receiver
        if (!message.getReceiver().getId().equals(receiver.getId())) {
            throw new ValidationException("Vous n'êtes pas autorisé à marquer ce message comme lu.");
        }

        message.setRead(true);
        messageDao.save(message);
    }

    /**
     * Mark all messages in a conversation as read
     */
    @Transactional
    public void markConversationAsRead(Long otherUserId, UserAccount currentUser) {
        List<Message> unreadMessages = messageDao.findUnreadMessages(currentUser.getId());

        unreadMessages.stream()
                .filter(m -> m.getSender().getId().equals(otherUserId))
                .forEach(m -> {
                    m.setRead(true);
                    messageDao.save(m);
                });
    }

    /**
     * Get conversation between two users
     */
    @Transactional(readOnly = true)
    public List<Message> getConversation(Long user1Id, Long user2Id) {
        return messageDao.findConversation(user1Id, user2Id);
    }

    /**
     * Get all messages sent by a user
     */
    @Transactional(readOnly = true)
    public List<Message> getSentMessages(Long senderId) {
        return messageDao.findBySenderId(senderId);
    }

    /**
     * Get all messages received by a user
     */
    @Transactional(readOnly = true)
    public List<Message> getReceivedMessages(Long receiverId) {
        return messageDao.findByReceiverId(receiverId);
    }

    /**
     * Get all unread messages for a user
     */
    @Transactional(readOnly = true)
    public List<Message> getUnreadMessages(Long receiverId) {
        return messageDao.findUnreadMessages(receiverId);
    }

    /**
     * Get unread message count for a user
     */
    @Transactional(readOnly = true)
    public long getUnreadMessageCount(Long receiverId) {
        return messageDao.countByReceiverIdAndReadFalse(receiverId);
    }

    /**
     * Get all messages related to a ride
     */
    @Transactional(readOnly = true)
    public List<Message> getRideMessages(Long rideId) {
        return messageDao.findByRideId(rideId);
    }

    /**
     * Delete a message (only by sender)
     */
    @Transactional
    public void deleteMessage(Long messageId, UserAccount sender) {
        Message message = messageDao.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", messageId));

        // Check if user is the sender
        if (!message.getSender().getId().equals(sender.getId())) {
            throw new ValidationException("Vous n'êtes pas autorisé à supprimer ce message.");
        }

        messageDao.delete(message);
    }
}
