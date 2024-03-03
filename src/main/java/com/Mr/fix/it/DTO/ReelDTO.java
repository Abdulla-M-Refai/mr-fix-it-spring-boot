package com.Mr.fix.it.DTO;

import lombok.*;

import java.util.List;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReelDTO
{
    private Long id;

    private WorkerDTO worker;

    private String video;

    private int totalLikes;

    private boolean isLiked;

    private List<CommentDTO> comments;

    private LocalDateTime postDate;
}
