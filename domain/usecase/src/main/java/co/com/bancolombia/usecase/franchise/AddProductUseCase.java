package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.gateways.FranchiseGateway;
import reactor.core.publisher.Mono;

public class AddProductUseCase {
    private final FranchiseGateway gateway;

    public AddProductUseCase(FranchiseGateway gateway) {
        this.gateway = gateway;
    }

    public Mono<Product> execute(String branchId, String productName, int stock) {
        return gateway.addProduct(branchId, productName, stock);
    }
}