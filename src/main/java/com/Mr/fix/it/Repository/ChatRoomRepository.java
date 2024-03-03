package com.Mr.fix.it.Repository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import com.Mr.fix.it.Entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>
{
    @Query(
        value =
            "SELECT cr FROM ChatRoom cr " +
            "WHERE (" +
                "(cr.sender.id = :senderID AND cr.receiver.id = :receiverID) OR " +
                "(cr.sender.id = :receiverID AND cr.receiver.id = :senderID)" +
            ") "
    )
    Optional<ChatRoom> findBySenderIDAndReceiverID(Long senderID, Long receiverID);

    @Query(
        value =
            "SELECT cr FROM ChatRoom cr " +
            "WHERE (cr.sender.id = :id AND cr.isDeletedForSender = false) OR " +
            "(cr.receiver.id = :id AND cr.isDeletedForReceiver = false)"
    )
    List<ChatRoom> findAllByUserID(Long id);
}