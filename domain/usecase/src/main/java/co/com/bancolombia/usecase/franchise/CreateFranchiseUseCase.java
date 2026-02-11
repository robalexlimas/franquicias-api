package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.gateways.FranchiseGateway;
import reactor.core.publisher.Mono;

public class CreateFranchiseUseCase {
    private final FranchiseGateway gateway;

    public CreateFranchiseUseCase(FranchiseGateway gateway) {
        this.gateway = gateway;
    }

    public Mono<Franchise> execute(String name) {
        return gateway.createFranchise(name);
    }
}