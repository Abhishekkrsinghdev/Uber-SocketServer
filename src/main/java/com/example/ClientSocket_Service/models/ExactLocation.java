package com.example.ClientSocket_Service.models;

import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExactLocation {
    private Double latitude;
    private Double longitude;
}