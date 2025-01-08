package com.zwm.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public  class SensitiveData {
    private List<BanList> banList;
    private String highlightedText;
}
