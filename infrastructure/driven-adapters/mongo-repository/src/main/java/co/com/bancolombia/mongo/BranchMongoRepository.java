package co.com.bancolombia.mongo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchMongoRepository extends
        ReactiveMongoRepository<BranchDocument, String>,
        ReactiveQueryByExampleExecutor<BranchDocument> {

    Flux<BranchDocument> findAllByFranchiseId(String franchiseId);
    Mono<Boolean> existsByFranchiseIdAndName(String franchiseId, String name);
}