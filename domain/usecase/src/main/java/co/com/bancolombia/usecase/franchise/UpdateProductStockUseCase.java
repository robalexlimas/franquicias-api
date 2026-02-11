package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.gateways.FranchiseGateway;
import co.com.bancolombia.model.product.Product;
import reactor.core.publisher.Mono;

public class UpdateProductStockUseCase {
    private final FranchiseGateway gateway;

    public UpdateProductStockUseCase(FranchiseGateway gateway) {
        this.gateway = gateway;
    }

    public Mono<Product> execute(String productId, int newStock) {
        return gateway.updateProductStock(productId, newStock);
    }
}