package co.com.bancolombia.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "products")
public class ProductDocument {

    @Id
    private String id;

    private String branchId;
    private String name;
    private Integer stock;

    private Instant createdAt;
    private Instant updatedAt;

    public ProductDocument() {}

    public ProductDocument(String id, String branchId, String name, Integer stock, Instant createdAt, Instant updatedAt) {
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
    public Integer getStock() { return stock; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setId(String id) { this.id = id; }
    public void setBranchId(String branchId) { this.branchId = branchId; }
    public void setName(String name) { this.name = name; }
    public void setStock(Integer stock) { this.stock = stock; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}