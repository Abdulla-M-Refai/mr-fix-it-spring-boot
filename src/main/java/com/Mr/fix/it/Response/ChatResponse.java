package com.Mr.fix.it.Response;

import lombok.*;

import com.Mr.fix.it.DTO.ChatRoomDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse
{
    private ChatRoomDTO chat;
}
