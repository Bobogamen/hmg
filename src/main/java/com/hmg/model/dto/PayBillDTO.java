package com.hmg.model.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class PayBillDTO {

    private  long billId;

    @Positive(message = "{positive_number}")
    private Double billValue;

    @NotEmpty(message = "{not_empty}")
    @NotBlank(message = "{non-whitespace}")
    private String billDocumentNumber;

    @NotNull(message = "{not_valid_date}")
    @PastOrPresent(message = "{not_future_date}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate billDocumentDate;

    public PayBillDTO() {
    }

    public long getBillId() {
        return billId;
    }

    public void setBillId(long billId) {
        this.billId = billId;
    }

    public Double getBillValue() {
        return billValue;
    }

    public void setBillValue(Double billValue) {
        this.billValue = billValue;
    }

    public String getBillDocumentNumber() {
        return billDocumentNumber;
    }

    public void setBillDocumentNumber(String billDocumentNumber) {
        this.billDocumentNumber = billDocumentNumber;
    }

    public LocalDate getBillDocumentDate() {
        return billDocumentDate;
    }

    public void setBillDocumentDate(LocalDate billDocumentDate) {
        this.billDocumentDate = billDocumentDate;
    }
}
