package com.Mr.fix.it.Request;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteChatRequest
{
    @NotBlank(message = "missing chat id")
    @Pattern(regexp = "\\d+", message = "chat id must be a valid number")
    private String chatID;

    public Long getParsedChatID()
    {
        return Long.parseLong(chatID);
    }
}
