package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.gateways.FranchiseGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.Mockito.*;

class CreateFranchiseUseCaseTest {

    private FranchiseGateway gateway;
    private CreateFranchiseUseCase useCase;

    @BeforeEach
    void setUp() {
        gateway = Mockito.mock(FranchiseGateway.class);
        useCase = new CreateFranchiseUseCase(gateway);
    }

    @Test
    void shouldCreateFranchise() {
        String name = "KFC";
        Franchise expected = new Franchise("f1", name, Instant.now(), Instant.now());

        when(gateway.createFranchise(name)).thenReturn(Mono.just(expected));

        StepVerifier.create(useCase.execute(name))
                .expectNextMatches(franchise ->
                        franchise.getId().equals("f1") &&
                                franchise.getName().equals("KFC") &&
                                franchise.getCreatedAt() != null &&
                                franchise.getUpdatedAt() != null
                )
                .verifyComplete();

        verify(gateway, times(1)).createFranchise(name);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void shouldPropagateErrorWhenGatewayFails() {
        String name = "KFC";
        RuntimeException error = new RuntimeException("db down");

        when(gateway.createFranchise(name)).thenReturn(Mono.error(error));

        StepVerifier.create(useCase.execute(name))
                .expectErrorMatches(e -> e instanceof RuntimeException && "db down".equals(e.getMessage()))
                .verify();

        verify(gateway, times(1)).createFranchise(name);
        verifyNoMoreInteractions(gateway);
    }
}
