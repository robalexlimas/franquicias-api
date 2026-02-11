package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.gateways.FranchiseGateway;
import reactor.core.publisher.Mono;

public class UpdateFranchiseNameUseCase {
    private final FranchiseGateway gateway;

    public UpdateFranchiseNameUseCase(FranchiseGateway gateway) {
        this.gateway = gateway;
    }

    public Mono<Franchise> execute(String franchiseId, String newName) {
        return gateway.updateFranchiseName(franchiseId, newName);
    }
}