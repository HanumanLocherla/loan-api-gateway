package com.hanuman.gateway.dto;

public record Address(String street,
                      String suite,
                      String city,
                      String zipcode,
                      Geo geo) {
}
