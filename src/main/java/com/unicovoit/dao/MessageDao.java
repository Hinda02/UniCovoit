package com.unicovoit.dao;

import com.unicovoit.entity.Message;
import com.unicovoit.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageDao extends JpaRepository<Message, Long> {

    /**
     * Find all messages sent by a specific user
     */
    List<Message> findBySender(UserAccount sender);

    /**
     * Find all messages received by a specific user
     */
    List<Message> findByReceiver(UserAccount receiver);

    /**
     * Find all messages sent by a user ID
     */
    List<Message> findBySenderId(Long senderId);

    /**
     * Find all messages received by a user ID
     */
    List<Message> findByReceiverId(Long receiverId);

    /**
     * Find conversation between two users (ordered by sentAt)
     */
    @Query("""
           SELECT m
           FROM Message m
           WHERE (m.sender.id = :user1Id AND m.receiver.id = :user2Id)
              OR (m.sender.id = :user2Id AND m.receiver.id = :user1Id)
           ORDER BY m.sentAt ASC
           """)
    List<Message> findConversation(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    /**
     * Find all unread messages for a specific receiver
     */
    @Query("""
           SELECT m
           FROM Message m
           WHERE m.receiver.id = :receiverId
             AND m.read = false
           ORDER BY m.sentAt DESC
           """)
    List<Message> findUnreadMessages(@Param("receiverId") Long receiverId);

    /**
     * Find all messages related to a specific ride
     */
    List<Message> findByRideId(Long rideId);

    /**
     * Count unread messages for a user
     */
    long countByReceiverIdAndReadFalse(Long receiverId);
}
