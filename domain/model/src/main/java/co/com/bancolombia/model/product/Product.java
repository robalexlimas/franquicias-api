package co.com.bancolombia.model.product;

import java.time.Instant;

public class Product {
    private final String id;
    private final String branchId;
    private final String name;
    private final int stock;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Product(String id, String branchId, String name, int stock, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.branchId = branchId;
        this.name = name;
        this.stock = stock;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getBranchId() { return branchId; }
    public String getName() { return name; }
    public int getStock() { return stock; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}