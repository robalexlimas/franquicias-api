package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.dto.TopProductByBranch;
import co.com.bancolombia.model.gateways.FranchiseGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TopProductByBranchUseCaseTest {

    private FranchiseGateway gateway;
    private TopProductByBranchUseCase useCase;

    @BeforeEach
    void setUp() {
        gateway = Mockito.mock(FranchiseGateway.class);
        useCase = new TopProductByBranchUseCase(gateway);
    }

    @Test
    void shouldReturnTopProductsByBranch() {
        String franchiseId = "fr-1";

        TopProductByBranch item1 =
                new TopProductByBranch("br-1", "Branch 1", "prod-1", "Burger", 50);

        TopProductByBranch item2 =
                new TopProductByBranch("br-2", "Branch 2", "prod-2", "Fries", 30);

        when(gateway.topProductByBranch(eq(franchiseId)))
                .thenReturn(Flux.just(item1, item2));

        StepVerifier.create(useCase.execute(franchiseId))
                .expectNext(item1)
                .expectNext(item2)
                .verifyComplete();

        verify(gateway, times(1)).topProductByBranch(franchiseId);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void shouldPropagateErrorWhenGatewayFails() {
        String franchiseId = "fr-err";
        RuntimeException error = new RuntimeException("boom");

        when(gateway.topProductByBranch(eq(franchiseId)))
                .thenReturn(Flux.error(error));

        StepVerifier.create(useCase.execute(franchiseId))
                .expectErrorMatches(e -> e == error)
                .verify();

        verify(gateway, times(1)).topProductByBranch(franchiseId);
        verifyNoMoreInteractions(gateway);
    }
}
