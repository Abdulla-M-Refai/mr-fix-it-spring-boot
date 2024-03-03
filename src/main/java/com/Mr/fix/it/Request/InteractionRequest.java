package com.Mr.fix.it.Request;

import com.Mr.fix.it.Request.Enum.InteractionType;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InteractionRequest
{
    @NotBlank(message = "missing room id")
    @Pattern(regexp = "\\d+", message = "room id must be a valid number")
    private String roomID;

    @NotBlank(message = "missing receiver id")
    @Pattern(regexp = "\\d+", message = "receiver id must be a valid number")
    private String receiverID;

    @NotBlank(message = "missing  interaction type")
    private InteractionType type;

    public Long getParsedRoomID()
    {
        return Long.parseLong(roomID);
    }

    public Long getParsedReceiverID()
    {
        return Long.parseLong(receiverID);
    }
}
