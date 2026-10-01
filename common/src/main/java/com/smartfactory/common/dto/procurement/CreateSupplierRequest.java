package com.smartfactory.common.dto.procurement;

import java.math.BigDecimal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSupplierRequest(@NotBlank(message = "Name is required") String name,

                                    @NotNull(message = "Lead time days is required") Integer leadTimeDays,

                                    @NotNull(message = "Rating is required") BigDecimal rating,

                                    @NotBlank(message = "Email is required") @Email(
                                            message = "Must be a valid email format") String contactEmail,

                                    @NotNull(message = "Active status is required") Boolean active) {
}
