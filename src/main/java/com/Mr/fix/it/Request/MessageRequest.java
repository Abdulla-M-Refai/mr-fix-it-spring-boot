package com.Mr.fix.it.Request;

import lombok.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

import com.Mr.fix.it.Entity.Enum.MessageType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest
{
    @NotBlank(message = "missing sender id")
    @Pattern(regexp = "\\d+", message = "sender id must be a valid number")
    private String senderID;

    @NotBlank(message = "missing receiver id")
    @Pattern(regexp = "\\d+", message = "receiver id must be a valid number")
    private String receiverID;

    @NotBlank(message = "missing content")
    private String content;

    @NotBlank(message = "missing message type")
    private MessageType type;

    public Long getParsedReceiverID()
    {
        return Long.parseLong(receiverID);
    }

    public Long getParsedSenderID()
    {
        return Long.parseLong(senderID);
    }
}
