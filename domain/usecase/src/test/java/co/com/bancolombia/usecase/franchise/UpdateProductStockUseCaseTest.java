package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.gateways.FranchiseGateway;
import co.com.bancolombia.model.product.Product;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UpdateProductStockUseCaseTest {

    @Test
    void shouldUpdateProductStock() {
        FranchiseGateway gateway = mock(FranchiseGateway.class);
        UpdateProductStockUseCase useCase = new UpdateProductStockUseCase(gateway);

        String productId = "prod-1";
        int newStock = 25;

        Product expected = new Product(
                productId,
                "branch-1",
                "Burger",
                newStock,
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-02T00:00:00Z")
        );

        when(gateway.updateProductStock(eq(productId), eq(newStock)))
                .thenReturn(Mono.just(expected));

        StepVerifier.create(useCase.execute(productId, newStock))
                .expectNext(expected)
                .verifyComplete();

        verify(gateway, times(1)).updateProductStock(productId, newStock);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void shouldPropagateErrorWhenGatewayFails() {
        FranchiseGateway gateway = mock(FranchiseGateway.class);
        UpdateProductStockUseCase useCase = new UpdateProductStockUseCase(gateway);

        String productId = "prod-1";
        int newStock = 25;

        RuntimeException error = new RuntimeException("boom");
        when(gateway.updateProductStock(eq(productId), eq(newStock)))
                .thenReturn(Mono.error(error));

        StepVerifier.create(useCase.execute(productId, newStock))
                .expectErrorMatches(e -> e == error)
                .verify();

        verify(gateway, times(1)).updateProductStock(productId, newStock);
        verifyNoMoreInteractions(gateway);
    }
}
