package com.Mr.fix.it.Response;

import lombok.*;

import java.util.List;

import com.Mr.fix.it.DTO.ChatMessageDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessagesResponse
{
    List<ChatMessageDTO> messages;
}
