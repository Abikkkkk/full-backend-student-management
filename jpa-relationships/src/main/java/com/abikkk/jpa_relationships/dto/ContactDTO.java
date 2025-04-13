package com.abikkk.jpa_relationships.dto;

import lombok.Data;

@Data
public class ContactDTO {
    private Long id;
    private String type;
    private String value;
}
