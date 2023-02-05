package com.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InvoiceType {

    PURCHASE("Purchase"),
    SALES("Sales");

    private final String value;
}
