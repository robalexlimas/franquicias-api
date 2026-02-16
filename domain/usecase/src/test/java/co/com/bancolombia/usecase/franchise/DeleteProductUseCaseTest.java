package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.gateways.FranchiseGateway;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DeleteProductUseCaseTest {

    @Test
    void shouldDeleteProduct() {
        FranchiseGateway gateway = mock(FranchiseGateway.class);
        DeleteProductUseCase useCase = new DeleteProductUseCase(gateway);

        String productId = "prod-1";
        when(gateway.deleteProduct(eq(productId))).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(productId))
                .verifyComplete();

        verify(gateway, times(1)).deleteProduct(productId);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void shouldPropagateErrorWhenGatewayFails() {
        FranchiseGateway gateway = mock(FranchiseGateway.class);
        DeleteProductUseCase useCase = new DeleteProductUseCase(gateway);

        String productId = "prod-1";
        RuntimeException error = new RuntimeException("boom");
        when(gateway.deleteProduct(eq(productId))).thenReturn(Mono.error(error));

        StepVerifier.create(useCase.execute(productId))
                .expectErrorMatches(e -> e == error)
                .verify();

        verify(gateway, times(1)).deleteProduct(productId);
        verifyNoMoreInteractions(gateway);
    }
}
