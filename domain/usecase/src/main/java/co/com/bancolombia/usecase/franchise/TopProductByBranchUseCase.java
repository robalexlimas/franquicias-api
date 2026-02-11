package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.dto.TopProductByBranch;
import co.com.bancolombia.model.gateways.FranchiseGateway;
import reactor.core.publisher.Flux;

public class TopProductByBranchUseCase {
    private final FranchiseGateway gateway;

    public TopProductByBranchUseCase(FranchiseGateway gateway) {
        this.gateway = gateway;
    }

    public Flux<TopProductByBranch> execute(String franchiseId) {
        return gateway.topProductByBranch(franchiseId);
    }
}