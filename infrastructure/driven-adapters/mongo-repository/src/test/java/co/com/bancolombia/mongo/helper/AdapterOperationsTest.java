package co.com.bancolombia.mongo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

class MongoRepositoryAdapterTest {

    @Mock
    private FranchiseMongoRepository franchiseRepo;

    private MongoRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new MongoRepositoryAdapter(franchiseRepo);
    }

    @Test
    void shouldReturnCountFromRepository() {
        when(franchiseRepo.count()).thenReturn(Mono.just(5L));

        StepVerifier.create(adapter.count())
                .expectNext(5L)
                .verifyComplete();
    }

    @Test
    void shouldPropagateErrorFromRepository() {
        RuntimeException error = new RuntimeException("DB error");
        when(franchiseRepo.count()).thenReturn(Mono.error(error));

        StepVerifier.create(adapter.count())
                .expectError(RuntimeException.class)
                .verify();
    }
}
