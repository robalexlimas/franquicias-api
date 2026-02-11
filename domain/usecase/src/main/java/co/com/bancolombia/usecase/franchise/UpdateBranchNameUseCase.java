package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.gateways.FranchiseGateway;
import reactor.core.publisher.Mono;

public class UpdateBranchNameUseCase {
    private final FranchiseGateway gateway;

    public UpdateBranchNameUseCase(FranchiseGateway gateway) {
        this.gateway = gateway;
    }

    public Mono<Branch> execute(String branchId, String newName) {
        return gateway.updateBranchName(branchId, newName);
    }
}