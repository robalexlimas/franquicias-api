package co.com.bancolombia.mongo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductMongoRepository extends
        ReactiveMongoRepository<ProductDocument, String>,
        ReactiveQueryByExampleExecutor<ProductDocument> {

    Flux<ProductDocument> findAllByBranchId(String branchId);
    Mono<Boolean> existsByBranchIdAndName(String branchId, String name);

    // Para "producto con mayor stock por sucursal"
    Mono<ProductDocument> findFirstByBranchIdOrderByStockDesc(String branchId);
}