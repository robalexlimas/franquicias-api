package co.com.bancolombia.api;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.dto.TopProductByBranch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.usecase.franchise.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;

class HandlerTest {

    private CreateFranchiseUseCase createFranchiseUseCase;
    private AddBranchUseCase addBranchUseCase;
    private AddProductUseCase addProductUseCase;
    private DeleteProductUseCase deleteProductUseCase;
    private UpdateProductStockUseCase updateProductStockUseCase;
    private TopProductByBranchUseCase topProductByBranchUseCase;
    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    private UpdateBranchNameUseCase updateBranchNameUseCase;
    private UpdateProductNameUseCase updateProductNameUseCase;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        createFranchiseUseCase = Mockito.mock(CreateFranchiseUseCase.class);
        addBranchUseCase = Mockito.mock(AddBranchUseCase.class);
        addProductUseCase = Mockito.mock(AddProductUseCase.class);
        deleteProductUseCase = Mockito.mock(DeleteProductUseCase.class);
        updateProductStockUseCase = Mockito.mock(UpdateProductStockUseCase.class);
        topProductByBranchUseCase = Mockito.mock(TopProductByBranchUseCase.class);
        updateFranchiseNameUseCase = Mockito.mock(UpdateFranchiseNameUseCase.class);
        updateBranchNameUseCase = Mockito.mock(UpdateBranchNameUseCase.class);
        updateProductNameUseCase = Mockito.mock(UpdateProductNameUseCase.class);

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

        RouterFunction<ServerResponse> router = new RouterRest().routerFunction(handler);

        client = WebTestClient.bindToRouterFunction(router)
                .configureClient()
                .baseUrl("/api")
                .build();
    }

    @Test
    void createFranchise_success() {
        Instant now = Instant.now();
        Franchise franchise = new Franchise("fr-1", "KFC", now, now);

        Mockito.when(createFranchiseUseCase.execute(eq("KFC")))
                .thenReturn(Mono.just(franchise));

        client.post().uri("/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"KFC\"}")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo("fr-1")
                .jsonPath("$.name").isEqualTo("KFC");
    }

    @Test
    void createFranchise_illegalArgument_mapsTo400() {
        Mockito.when(createFranchiseUseCase.execute(anyString()))
                .thenReturn(Mono.error(new IllegalArgumentException("bad request")));

        client.post().uri("/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"\"}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("bad request");
    }

    @Test
    void createFranchise_illegalState_mapsTo409() {
        Mockito.when(createFranchiseUseCase.execute(anyString()))
                .thenReturn(Mono.error(new IllegalStateException("duplicate")));

        client.post().uri("/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"KFC\"}")
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.message").isEqualTo("duplicate");
    }

    @Test
    void addBranch_success() {
        Instant now = Instant.now();
        Branch branch = new Branch("br-1", "fr-1", "S1", now, now);

        Mockito.when(addBranchUseCase.execute(eq("fr-1"), eq("S1")))
                .thenReturn(Mono.just(branch));

        client.post().uri("/franchises/{id}/branches", "fr-1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"S1\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("br-1")
                .jsonPath("$.franchiseId").isEqualTo("fr-1")
                .jsonPath("$.name").isEqualTo("S1");
    }

    @Test
    void addProduct_success() {
        Instant now = Instant.now();
        Product product = new Product("pr-1", "br-1", "Burger", 10, now, now);

        Mockito.when(addProductUseCase.execute(eq("br-1"), eq("Burger"), eq(10)))
                .thenReturn(Mono.just(product));

        client.post().uri("/branches/{id}/products", "br-1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Burger\",\"stock\":10}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("pr-1")
                .jsonPath("$.branchId").isEqualTo("br-1")
                .jsonPath("$.name").isEqualTo("Burger")
                .jsonPath("$.stock").isEqualTo(10);
    }

    @Test
    void deleteProduct_success_returns204() {
        Mockito.when(deleteProductUseCase.execute(eq("pr-1")))
                .thenReturn(Mono.empty());

        client.delete().uri("/products/{id}", "pr-1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteProduct_notFound_mapsTo400_inCurrentHandler() {
        // Nota: tu Handler hoy mapea IllegalArgumentException -> 400 (no 404)
        Mockito.when(deleteProductUseCase.execute(eq("pr-404")))
                .thenReturn(Mono.error(new IllegalArgumentException("Product not found")));

        client.delete().uri("/products/{id}", "pr-404")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("Product not found");
    }

    @Test
    void updateProductStock_success() {
        Instant now = Instant.now();
        Product updated = new Product("pr-1", "br-1", "Burger", 99, now, now);

        Mockito.when(updateProductStockUseCase.execute(eq("pr-1"), eq(99)))
                .thenReturn(Mono.just(updated));

        client.patch().uri("/products/{id}/stock", "pr-1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("{\"stock\":99}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stock").isEqualTo(99);
    }

    @Test
    void topProductsByBranch_success_returnsArray() {
        TopProductByBranch item1 = new TopProductByBranch("br-1", "S1", "pr-1", "Burger", 50);
        TopProductByBranch item2 = new TopProductByBranch("br-2", "S2", null, null, 0);

        Mockito.when(topProductByBranchUseCase.execute(eq("fr-1")))
                .thenReturn(Flux.just(item1, item2));

        client.get().uri("/franchises/{id}/top-products", "fr-1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].branchId").isEqualTo("br-1")
                .jsonPath("$[0].branchName").isEqualTo("S1")
                .jsonPath("$[0].productId").isEqualTo("pr-1")
                .jsonPath("$[0].stock").isEqualTo(50)
                .jsonPath("$[1].branchId").isEqualTo("br-2")
                .jsonPath("$[1].productId").doesNotExist(); // null => a veces no se serializa, esto cubre ambos casos
    }
}
