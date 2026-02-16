package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.gateways.FranchiseGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UpdateFranchiseNameUseCaseTest {

    private FranchiseGateway gateway;
    private UpdateFranchiseNameUseCase useCase;

    @BeforeEach
    void setUp() {
        gateway = mock(FranchiseGateway.class);
        useCase = new UpdateFranchiseNameUseCase(gateway);
    }

    @Test
    void shouldUpdateFranchiseName() {
        String franchiseId = "fr-1";
        String newName = "New Name";

        Franchise expected = new Franchise(franchiseId, newName, Instant.now(), Instant.now());

        when(gateway.updateFranchiseName(eq(franchiseId), eq(newName)))
                .thenReturn(Mono.just(expected));

        StepVerifier.create(useCase.execute(franchiseId, newName))
                .expectNext(expected)
                .verifyComplete();

        verify(gateway, times(1)).updateFranchiseName(franchiseId, newName);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void shouldPropagateErrorWhenGatewayFails() {
        String franchiseId = "fr-err";
        String newName = "X";
        RuntimeException error = new RuntimeException("boom");

        when(gateway.updateFranchiseName(eq(franchiseId), eq(newName)))
                .thenReturn(Mono.error(error));

        StepVerifier.create(useCase.execute(franchiseId, newName))
                .expectErrorMatches(e -> e == error)
                .verify();

        verify(gateway, times(1)).updateFranchiseName(franchiseId, newName);
        verifyNoMoreInteractions(gateway);
    }
}
