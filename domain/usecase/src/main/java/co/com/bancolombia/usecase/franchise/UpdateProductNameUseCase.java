package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.gateways.FranchiseGateway;
import co.com.bancolombia.model.product.Product;
import reactor.core.publisher.Mono;

public class UpdateProductNameUseCase {
    private final FranchiseGateway gateway;

    public UpdateProductNameUseCase(FranchiseGateway gateway) {
        this.gateway = gateway;
    }

    public Mono<Product> execute(String productId, String newName) {
        return gateway.updateProductName(productId, newName);
    }
}