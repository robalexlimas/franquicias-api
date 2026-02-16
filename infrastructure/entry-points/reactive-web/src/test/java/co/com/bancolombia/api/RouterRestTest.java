package co.com.bancolombia.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

class RouterRestTest {

    private WebTestClient client;
    private Handler handler;

    @BeforeEach
    void setUp() {
        handler = Mockito.mock(Handler.class);

        Mockito.when(handler.createFranchise(Mockito.any()))
                .thenReturn(ServerResponse.ok().build());
        Mockito.when(handler.updateFranchiseName(Mockito.any()))
                .thenReturn(ServerResponse.ok().build());
        Mockito.when(handler.addBranch(Mockito.any()))
                .thenReturn(ServerResponse.ok().build());
        Mockito.when(handler.updateBranchName(Mockito.any()))
                .thenReturn(ServerResponse.ok().build());
        Mockito.when(handler.addProduct(Mockito.any()))
                .thenReturn(ServerResponse.ok().build());
        Mockito.when(handler.updateProductStock(Mockito.any()))
                .thenReturn(ServerResponse.ok().build());
        Mockito.when(handler.updateProductName(Mockito.any()))
                .thenReturn(ServerResponse.ok().build());
        Mockito.when(handler.deleteProduct(Mockito.any()))
                .thenReturn(ServerResponse.noContent().build());
        Mockito.when(handler.topProductsByBranch(Mockito.any()))
                .thenReturn(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("[]"));

        RouterFunction<ServerResponse> router = new RouterRest().routerFunction(handler);

        client = WebTestClient.bindToRouterFunction(router)
                .configureClient()
                .baseUrl("/api")
                .build();
    }

    @Test
    void shouldRouteCreateFranchise() {
        client.post().uri("/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"KFC\"}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldRouteUpdateFranchiseName() {
        client.patch().uri("/franchises/{id}/name", "fr-1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Nuevo\"}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldRouteAddBranch() {
        client.post().uri("/franchises/{id}/branches", "fr-1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Sucursal 1\"}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldRouteUpdateBranchName() {
        client.patch().uri("/branches/{id}/name", "br-1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Sucursal Nueva\"}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldRouteAddProduct() {
        client.post().uri("/branches/{id}/products", "br-1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Burger\",\"stock\":10}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldRouteUpdateProductStock() {
        client.patch().uri("/products/{id}/stock", "pr-1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("{\"stock\":99}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldRouteUpdateProductName() {
        client.patch().uri("/products/{id}/name", "pr-1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Burger XL\"}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldRouteDeleteProduct() {
        client.delete().uri("/products/{id}", "pr-1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldRouteTopProductsByBranch() {
        client.get().uri("/franchises/{id}/top-products", "fr-1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturn404ForUnknownRoute() {
        client.get().uri("/unknown")
                .exchange()
                .expectStatus().isNotFound();
    }
}
