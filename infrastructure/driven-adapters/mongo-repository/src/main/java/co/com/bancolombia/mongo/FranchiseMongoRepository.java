package co.com.bancolombia.mongo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import reactor.core.publisher.Mono;

public interface FranchiseMongoRepository extends
        ReactiveMongoRepository<FranchiseDocument, String>,
        ReactiveQueryByExampleExecutor<FranchiseDocument> {

    Mono<Boolean> existsByName(String name);
}