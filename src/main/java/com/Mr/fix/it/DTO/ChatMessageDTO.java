package com.Mr.fix.it.DTO;

import lombok.*;

import java.time.LocalDateTime;

import com.Mr.fix.it.Entity.Enum.MessageType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO
{
    private Long roomID;

    private Long senderID;

    private Long receiverID;

    private String content;

    private MessageType type;

    private LocalDateTime timestamp;

    private Boolean seen;
}
