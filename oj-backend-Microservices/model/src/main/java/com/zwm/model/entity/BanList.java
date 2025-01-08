package com.zwm.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BanList {
    private String word;
    private String category;
    private String explanation;
    private int index;
}
