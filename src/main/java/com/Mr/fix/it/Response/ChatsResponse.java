package com.Mr.fix.it.Response;

import lombok.*;

import java.util.List;

import com.Mr.fix.it.DTO.ChatRoomDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatsResponse
{
    private List<ChatRoomDTO> chats;
}
