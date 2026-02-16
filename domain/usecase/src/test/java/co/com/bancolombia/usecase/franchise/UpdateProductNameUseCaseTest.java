package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.gateways.FranchiseGateway;
import co.com.bancolombia.model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UpdateProductNameUseCaseTest {

    private FranchiseGateway gateway;
    private UpdateProductNameUseCase useCase;

    @BeforeEach
    void setUp() {
        gateway = mock(FranchiseGateway.class);
        useCase = new UpdateProductNameUseCase(gateway);
    }

    @Test
    void shouldUpdateProductName() {
        String productId = "prod-1";
        String newName = "New Product Name";

        Product expected = new Product(productId, "br-1", newName, 10, Instant.now(), Instant.now());

        when(gateway.updateProductName(eq(productId), eq(newName)))
                .thenReturn(Mono.just(expected));

        StepVerifier.create(useCase.execute(productId, newName))
                .expectNext(expected)
                .verifyComplete();

        verify(gateway, times(1)).updateProductName(productId, newName);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void shouldPropagateErrorWhenGatewayFails() {
        String productId = "prod-err";
        String newName = "X";
        RuntimeException error = new RuntimeException("boom");

        when(gateway.updateProductName(eq(productId), eq(newName)))
                .thenReturn(Mono.error(error));

        StepVerifier.create(useCase.execute(productId, newName))
                .expectErrorMatches(e -> e == error)
                .verify();

        verify(gateway, times(1)).updateProductName(productId, newName);
        verifyNoMoreInteractions(gateway);
    }
}
