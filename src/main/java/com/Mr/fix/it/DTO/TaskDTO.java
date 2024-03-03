package com.Mr.fix.it.DTO;

import lombok.*;

import java.util.List;
import java.time.LocalDateTime;

import com.Mr.fix.it.Entity.Feedback;

import com.Mr.fix.it.Entity.Enum.TaskType;
import com.Mr.fix.it.Entity.Enum.TaskStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO
{
    private long id;

    private UserDTO client;

    private WorkerDTO worker;

    private CategoryDTO category;

    private String locality;

    private double latitude;

    private double longitude;

    private String title;

    private String description;

    private double price;

    private LocalDateTime startDate;

    private LocalDateTime expiryDate;

    private TaskType type;

    private TaskStatus status;

    private List<ImageDTO> taskImgs;

    private List<OfferDTO> offers;

    private Feedback feedback;
}
