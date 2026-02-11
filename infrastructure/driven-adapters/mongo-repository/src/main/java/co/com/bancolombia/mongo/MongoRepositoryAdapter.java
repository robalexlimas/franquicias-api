package co.com.bancolombia.mongo;

import co.com.bancolombia.usecase.health.DbHealthPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@ConditionalOnProperty(name = "db.provider", havingValue = "mongodb")
public class MongoRepositoryAdapter implements DbHealthPort {

    private final FranchiseMongoRepository franchiseRepo;

    public MongoRepositoryAdapter(FranchiseMongoRepository franchiseRepo) {
        this.franchiseRepo = franchiseRepo;
    }

    @Override
    public Mono<Long> count() {
        return franchiseRepo.count();
    }
}