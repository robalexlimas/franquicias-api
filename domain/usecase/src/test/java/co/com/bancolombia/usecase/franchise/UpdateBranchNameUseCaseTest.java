package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.gateways.FranchiseGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UpdateBranchNameUseCaseTest {

    private FranchiseGateway gateway;
    private UpdateBranchNameUseCase useCase;

    @BeforeEach
    void setUp() {
        gateway = mock(FranchiseGateway.class);
        useCase = new UpdateBranchNameUseCase(gateway);
    }

    @Test
    void shouldUpdateBranchName() {
        String branchId = "br-1";
        String newName = "New Branch Name";

        Branch expected = new Branch(branchId, "fr-1", newName, Instant.now(), Instant.now());

        when(gateway.updateBranchName(eq(branchId), eq(newName)))
                .thenReturn(Mono.just(expected));

        StepVerifier.create(useCase.execute(branchId, newName))
                .expectNext(expected)
                .verifyComplete();

        verify(gateway, times(1)).updateBranchName(branchId, newName);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void shouldPropagateErrorWhenGatewayFails() {
        String branchId = "br-err";
        String newName = "X";
        RuntimeException error = new RuntimeException("boom");

        when(gateway.updateBranchName(eq(branchId), eq(newName)))
                .thenReturn(Mono.error(error));

        StepVerifier.create(useCase.execute(branchId, newName))
                .expectErrorMatches(e -> e == error)
                .verify();

        verify(gateway, times(1)).updateBranchName(branchId, newName);
        verifyNoMoreInteractions(gateway);
    }
}
