package com.hmg.model.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class AddExpenseDTO {

    @NotEmpty(message = "{not_empty}")
    @NotBlank(message = "{non-whitespace}")
    @Size(min = 3, max = 30, message = "{field_length}")
    private String name;

    @Positive(message = "{positive_number}")
    private Double value;

    @NotEmpty(message = "{not_empty}")
    @NotBlank(message = "{non-whitespace}")
    private String documentNumber;

    @NotNull(message = "{not_valid_date}")
    @PastOrPresent(message = "{not_future_date}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate documentDate;

    public AddExpenseDTO() {
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

    public void setValue(Double value) {
        this.value = value;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public LocalDate getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(LocalDate documentDate) {
        this.documentDate = documentDate;
    }
}
