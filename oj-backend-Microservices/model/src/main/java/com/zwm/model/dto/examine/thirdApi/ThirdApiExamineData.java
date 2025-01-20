package com.zwm.model.dto.examine.thirdApi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public  class ThirdApiExamineData {
    private List<ThirdApiBanList> banList;
    private String highlightedText;
}
