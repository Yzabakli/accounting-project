package com.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InvoiceStatus {

    AWAITING_APPROVAL("Awaiting Approval"),
    APPROVED("Approved");

    private final String value;
}
