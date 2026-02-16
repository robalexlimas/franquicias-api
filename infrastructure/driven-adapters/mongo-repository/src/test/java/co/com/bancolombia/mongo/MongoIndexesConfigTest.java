package co.com.bancolombia.mongo;

import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.ReactiveIndexOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MongoIndexesConfigTest {

    @Test
    void ensureIndexes_shouldCreateAllIndexes() {
        ReactiveMongoTemplate template = mock(ReactiveMongoTemplate.class);

        ReactiveIndexOperations franchiseOps = mock(ReactiveIndexOperations.class);
        ReactiveIndexOperations branchOps = mock(ReactiveIndexOperations.class);
        ReactiveIndexOperations productOps = mock(ReactiveIndexOperations.class);

        when(template.indexOps(FranchiseDocument.class)).thenReturn(franchiseOps);
        when(template.indexOps(BranchDocument.class)).thenReturn(branchOps);
        when(template.indexOps(ProductDocument.class)).thenReturn(productOps);

        // ensureIndex -> Mono<String>
        when(franchiseOps.ensureIndex(any())).thenReturn(Mono.just("idx_franchise_name"));
        when(branchOps.ensureIndex(any())).thenReturn(Mono.just("idx_branch_any"));
        when(productOps.ensureIndex(any())).thenReturn(Mono.just("idx_product_any"));

        MongoIndexesConfig config = new MongoIndexesConfig();

        StepVerifier.create(config.ensureIndexes(template))
                .verifyComplete();

        // 1 index en Franchise
        verify(franchiseOps, times(1)).ensureIndex(any());

        // 2 index en Branch
        verify(branchOps, times(2)).ensureIndex(any());

        // 3 index en Product
        verify(productOps, times(3)).ensureIndex(any());

        verifyNoMoreInteractions(franchiseOps, branchOps, productOps);
    }
}
