package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.gateways.FranchiseGateway;
import reactor.core.publisher.Mono;

public class AddBranchUseCase {
    private final FranchiseGateway gateway;

    public AddBranchUseCase(FranchiseGateway gateway) {
        this.gateway = gateway;
    }

    public Mono<Branch> execute(String franchiseId, String branchName) {
        return gateway.addBranch(franchiseId, branchName);
    }
}