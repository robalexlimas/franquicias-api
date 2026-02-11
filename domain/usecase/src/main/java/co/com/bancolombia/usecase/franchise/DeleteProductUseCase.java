package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.gateways.FranchiseGateway;
import reactor.core.publisher.Mono;

public class DeleteProductUseCase {
    private final FranchiseGateway gateway;

    public DeleteProductUseCase(FranchiseGateway gateway) {
        this.gateway = gateway;
    }

    public Mono<Void> execute(String productId) {
        return gateway.deleteProduct(productId);
    }
}