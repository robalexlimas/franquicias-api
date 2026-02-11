package co.com.bancolombia.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class CreateProductRequest {
    private String name;
    private int stock;
}