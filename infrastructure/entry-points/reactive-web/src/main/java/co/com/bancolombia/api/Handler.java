package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.CreateBranchRequest;
import co.com.bancolombia.api.dto.CreateFranchiseRequest;
import co.com.bancolombia.api.dto.CreateProductRequest;
import co.com.bancolombia.api.dto.ErrorResponse;
import co.com.bancolombia.api.dto.UpdateNameRequest;
import co.com.bancolombia.api.dto.UpdateStockRequest;
import co.com.bancolombia.usecase.franchise.AddBranchUseCase;
import co.com.bancolombia.usecase.franchise.AddProductUseCase;
import co.com.bancolombia.usecase.franchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.franchise.DeleteProductUseCase;
import co.com.bancolombia.usecase.franchise.TopProductByBranchUseCase;
import co.com.bancolombia.usecase.franchise.UpdateBranchNameUseCase;
import co.com.bancolombia.usecase.franchise.UpdateFranchiseNameUseCase;
import co.com.bancolombia.usecase.franchise.UpdateProductNameUseCase;
import co.com.bancolombia.usecase.franchise.UpdateProductStockUseCase;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class Handler {

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final AddBranchUseCase addBranchUseCase;
    private final AddProductUseCase addProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final TopProductByBranchUseCase topProductByBranchUseCase;

    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;

    public Handler(CreateFranchiseUseCase createFranchiseUseCase,
                   AddBranchUseCase addBranchUseCase,
                   AddProductUseCase addProductUseCase,
                   DeleteProductUseCase deleteProductUseCase,
                   UpdateProductStockUseCase updateProductStockUseCase,
                   TopProductByBranchUseCase topProductByBranchUseCase,
                   UpdateFranchiseNameUseCase updateFranchiseNameUseCase,
                   UpdateBranchNameUseCase updateBranchNameUseCase,
                   UpdateProductNameUseCase updateProductNameUseCase) {
        this.createFranchiseUseCase = createFranchiseUseCase;
        this.addBranchUseCase = addBranchUseCase;
        this.addProductUseCase = addProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.updateProductStockUseCase = updateProductStockUseCase;
        this.topProductByBranchUseCase = topProductByBranchUseCase;
        this.updateFranchiseNameUseCase = updateFranchiseNameUseCase;
        this.updateBranchNameUseCase = updateBranchNameUseCase;
        this.updateProductNameUseCase = updateProductNameUseCase;
    }

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(CreateFranchiseRequest.class)
                .flatMap(body -> createFranchiseUseCase.execute(body.getName()))
                .flatMap(result -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(result))
                .onErrorResume(this::mapError);
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(body -> updateFranchiseNameUseCase.execute(franchiseId, body.getName()))
                .flatMap(result -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(result))
                .onErrorResume(this::mapError);
    }

    public Mono<ServerResponse> addBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return request.bodyToMono(CreateBranchRequest.class)
                .flatMap(body -> addBranchUseCase.execute(franchiseId, body.getName()))
                .flatMap(result -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(result))
                .onErrorResume(this::mapError);
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        String branchId = request.pathVariable("branchId");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(body -> updateBranchNameUseCase.execute(branchId, body.getName()))
                .flatMap(result -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(result))
                .onErrorResume(this::mapError);
    }

    public Mono<ServerResponse> addProduct(ServerRequest request) {
        String branchId = request.pathVariable("branchId");
        return request.bodyToMono(CreateProductRequest.class)
                .flatMap(body -> addProductUseCase.execute(branchId, body.getName(), body.getStock()))
                .flatMap(result -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(result))
                .onErrorResume(this::mapError);
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        String productId = request.pathVariable("productId");
        return request.bodyToMono(UpdateStockRequest.class)
                .flatMap(body -> updateProductStockUseCase.execute(productId, body.getStock()))
                .flatMap(result -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(result))
                .onErrorResume(this::mapError);
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        String productId = request.pathVariable("productId");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(body -> updateProductNameUseCase.execute(productId, body.getName()))
                .flatMap(result -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(result))
                .onErrorResume(this::mapError);
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        String productId = request.pathVariable("productId");
        return deleteProductUseCase.execute(productId)
                .then(ServerResponse.noContent().build())
                .onErrorResume(this::mapError);
    }

    public Mono<ServerResponse> topProductsByBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(topProductByBranchUseCase.execute(franchiseId), Object.class)
                .onErrorResume(this::mapError);
    }

    private Mono<ServerResponse> mapError(Throwable e) {
        int status = 500;

        if (e instanceof IllegalArgumentException) status = 400;
        if (e instanceof IllegalStateException) status = 409;

        ErrorResponse body = new ErrorResponse(status, e.getMessage() == null ? "Unexpected error" : e.getMessage());
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);
    }
}