package pl.szkolaspringa.bookstore.order.domain;

import lombok.Builder;

import javax.validation.constraints.NotBlank;

@Builder
public record Recipient(
        @NotBlank String name, @NotBlank String phone, @NotBlank String street, @NotBlank String city,
        @NotBlank String zipCode, @NotBlank String email) {
}
