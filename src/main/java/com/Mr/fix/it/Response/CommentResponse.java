package com.Mr.fix.it.Response;

import lombok.*;

import com.Mr.fix.it.DTO.CommentDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse
{
    private CommentDTO comment;
}
