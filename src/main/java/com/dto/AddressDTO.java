package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddressDTO {

    private Long id;

    @NotBlank(message = "Required field")
    @Size(min = 2, max = 100, message = "Size should be between 2 and 100")
    private String addressLine1;

    @Size(max = 100, message = "Size should be maximum 100")
    private String addressLine2;

    @NotBlank(message = "Required field")
    @Size(max = 50, message = "Size should be maximum 50")
    private String city;

    @NotBlank(message = "Required field")
    @Size(max = 50, message = "Size should be maximum 50")
    private String state;

    @NotBlank(message = "Required field")
    @Size(max = 50, message = "Size should be maximum 50")
    private String country;

    @NotBlank(message = "Required field")
    @Size(max = 50, message = "Size should be maximum 50")
    private String zipCode;
}
