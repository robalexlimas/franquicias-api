package co.com.bancolombia.model.branch;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class Branch {
    private final String id;
    private final String franchiseId;
    private final String name;
    private final Instant createdAt;
    private final Instant updatedAt;
}