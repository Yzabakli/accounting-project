package com.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CalculationType {

    FIRST_IN_FIRST_OUT("First in First out"), AVERAGE_RATE("Average rate");

    private final String value;
}
