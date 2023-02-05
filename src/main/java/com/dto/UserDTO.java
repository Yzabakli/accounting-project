package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {

    private Long id;

    @NotBlank(message = "Required field")
    private String username;

    @NotBlank(message = "Required field")
    private String password;

    @NotBlank(message = "Required field")
    private String firstname;

    @NotBlank(message = "Required field")
    private String lastname;

    @NotBlank(message = "Required field")
    private String phone;
    private boolean enabled;

    @NotNull(message = "Required field")
    private RoleDTO role;

    @NotNull(message = "Required field")
    private CompanyDTO company;

    private boolean isOnlyAdmin;

    private String confirmPassword;
}
