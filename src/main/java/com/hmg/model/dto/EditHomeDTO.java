package com.hmg.model.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class EditHomeDTO {

    @NotEmpty(message = "{not_empty}")
    @NotBlank(message = "{non-whitespace}")
    private String floor;

    @NotEmpty(message = "{not_empty}")
    @NotBlank(message = "{non-whitespace}")
    private String name;

    public EditHomeDTO() {
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
