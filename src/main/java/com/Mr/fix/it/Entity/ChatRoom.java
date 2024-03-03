package com.Mr.fix.it.Entity;

import lombok.*;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "chat_room")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatID", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "senderID", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiverID", nullable = false)
    private User receiver;

    @Column(name = "is_deleted_for_sender", nullable = false)
    private Boolean isDeletedForSender;

    @Column(name = "is_deleted_for_receiver", nullable = false)
    private Boolean isDeletedForReceiver;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "chatRoom",
        cascade = CascadeType.ALL
    )
    private List<ChatMessage> chatMessages;
}