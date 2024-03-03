package com.Mr.fix.it.Entity;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import com.Mr.fix.it.Entity.Enum.MessageType;

@Entity
@Table(name = "chat_message")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "messageID", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chatID", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "senderID", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiverID", nullable = false)
    private User receiver;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "message_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "is_seen_for_receiver", nullable = false)
    private Boolean isSeenForReceiver;

    @Column(name = "is_deleted_for_sender", nullable = false)
    private Boolean isDeletedForSender;

    @Column(name = "is_deleted_for_receiver", nullable = false)
    private Boolean isDeletedForReceiver;
}
