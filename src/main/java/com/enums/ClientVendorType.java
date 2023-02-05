package com.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ClientVendorType {

    CLIENT("Client"), VENDOR("Vendor");

    private final String value;
}
