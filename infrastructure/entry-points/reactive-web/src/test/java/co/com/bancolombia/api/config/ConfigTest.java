package co.com.bancolombia.api.config;

import co.com.bancolombia.api.Handler;
import co.com.bancolombia.api.RouterRest;
import co.com.bancolombia.api.dto.CreateFranchiseRequest;
import co.com.bancolombia.usecase.franchise.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ConfigTest {

    @Test
    void shouldApplySecurityHeadersOnCreateFranchise() {
        CreateFranchiseUseCase createFranchiseUseCase = mock(CreateFranchiseUseCase.class);
        AddBranchUseCase addBranchUseCase = mock(AddBranchUseCase.class);
        AddProductUseCase addProductUseCase = mock(AddProductUseCase.class);
        DeleteProductUseCase deleteProductUseCase = mock(DeleteProductUseCase.class);
        UpdateProductStockUseCase updateProductStockUseCase = mock(UpdateProductStockUseCase.class);
        TopProductByBranchUseCase topProductByBranchUseCase = mock(TopProductByBranchUseCase.class);
        UpdateFranchiseNameUseCase updateFranchiseNameUseCase = mock(UpdateFranchiseNameUseCase.class);
        UpdateBranchNameUseCase updateBranchNameUseCase = mock(UpdateBranchNameUseCase.class);
        UpdateProductNameUseCase updateProductNameUseCase = mock(UpdateProductNameUseCase.class);

        when(createFranchiseUseCase.execute(anyString()))
                .thenReturn(Mono.empty());

        Handler handler = new Handler(
                createFranchiseUseCase,
                addBranchUseCase,
                addProductUseCase,
                deleteProductUseCase,
                updateProductStockUseCase,
                topProductByBranchUseCase,
                updateFranchiseNameUseCase,
                updateBranchNameUseCase,
                updateProductNameUseCase
        );

        RouterRest routerRest = new RouterRest();
        var routerFunction = routerRest.routerFunction(handler);

        WebTestClient client = WebTestClient
                .bindToRouterFunction(routerFunction)
                .webFilter(new SecurityHeadersConfig())
                .build();

        CreateFranchiseRequest body = new CreateFranchiseRequest();
        body.setName("KFC");

        client.post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");

        verify(createFranchiseUseCase).execute("KFC");
    }
}
