package co.com.bancolombia.model.franchise;

import lombok.Getter;
import lombok.AllArgsConstructor;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class Franchise {

    private final String id;
    private final String name;
    private final Instant createdAt;
    private final Instant updatedAt;
}