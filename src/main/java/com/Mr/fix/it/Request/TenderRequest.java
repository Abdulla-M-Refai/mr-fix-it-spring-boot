package com.Mr.fix.it.Request;

import lombok.*;

import com.Mr.fix.it.Validator.Annotation.ValidCategory;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TenderRequest extends TaskRequest
{
    @ValidCategory(message = "invalid or missing category")
    private String category;
}
