package com.zwm.model.dto.examine.thirdApi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThirdApiBanList {
    private String word;
    private String category;
    private String explanation;
    private int index;
}
