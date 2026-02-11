package co.com.bancolombia.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "branches")
public class BranchDocument {

    @Id
    private String id;

    private String franchiseId;
    private String name;

    private Instant createdAt;
    private Instant updatedAt;

    public BranchDocument() {}

    public BranchDocument(String id, String franchiseId, String name, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.franchiseId = franchiseId;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getFranchiseId() { return franchiseId; }
    public String getName() { return name; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setId(String id) { this.id = id; }
    public void setFranchiseId(String franchiseId) { this.franchiseId = franchiseId; }
    public void setName(String name) { this.name = name; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}