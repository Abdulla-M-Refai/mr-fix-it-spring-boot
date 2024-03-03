package com.Mr.fix.it.DTO;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDTO
{
    private Long chatID;

    private UserDTO sender;

    private UserDTO receiver;

    private ChatMessageDTO lastMessage;

    private Integer newMessages;
}
