package com.zwm.model.dto.examine.thirdApi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThirdApiExamine implements Serializable {
    private String message;
    private ThirdApiExamineData data;
}
