package co.com.bancolombia.model.branch;

import java.time.Instant;

public class Branch {
    private final String id;
    private final String franchiseId;
    private final String name;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Branch(String id, String franchiseId, String name, Instant createdAt, Instant updatedAt) {
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
}