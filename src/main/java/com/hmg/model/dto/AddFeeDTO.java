package com.hmg.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class AddFeeDTO {

    @NotEmpty(message = "{not_empty}")
    @NotBlank(message = "{non-whitespace}")
    @Size(min = 3, max = 30, message = "{field_length}")
    private String name;

    @Positive(message = "{positive_number}")
    private Double value;

    public AddFeeDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
