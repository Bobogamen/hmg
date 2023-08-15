package com.hmg.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class AddBillDTO {

    @NotEmpty(message = "{not_empty}")
    @NotBlank(message = "{non-whitespace}")
    @Size(min = 3, max = 30, message = "{field_length}")
    private String name;

    public AddBillDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
