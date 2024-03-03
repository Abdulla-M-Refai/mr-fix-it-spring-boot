package com.Mr.fix.it.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO
{
    private long id;

    private UserDTO user;

    private String comment;

    private LocalDateTime commentDate;
}
