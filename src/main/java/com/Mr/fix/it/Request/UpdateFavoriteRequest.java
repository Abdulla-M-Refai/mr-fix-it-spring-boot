package com.Mr.fix.it.Request;

import lombok.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFavoriteRequest
{
    @NotBlank(message = "missing worker id")
    @Pattern(regexp = "\\d+", message = "worker id must be a valid number")
    private String workerID;

    @NotBlank(message = "missing favorite state")
    @Pattern(regexp = "\\b(?:true|false)\\b", message = "invalid favorite state")
    private String favoriteState;

    public Long getParsedWorkerID()
    {
        return Long.parseLong(workerID);
    }

    public Boolean getParsedFavoriteState()
    {
        return Boolean.parseBoolean(favoriteState);
    }
}