package co.com.bancolombia.model.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class Product {
    private final String id;
    private final String branchId;
    private final String name;
    private final int stock;
    private final Instant createdAt;
    private final Instant updatedAt;
}