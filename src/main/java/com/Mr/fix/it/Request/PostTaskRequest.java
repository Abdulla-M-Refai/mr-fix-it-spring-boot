package com.Mr.fix.it.Request;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import com.Mr.fix.it.Validator.Annotation.ValidCategory;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PostTaskRequest extends TaskRequest
{
    @ValidCategory(message = "invalid or missing category")
    private String category;

    @NotBlank(message = "missing price")
    @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "price must be a valid number")
    private String price;

    public Double getParsedPrice()
    {
        return Double.parseDouble(price);
    }
}
