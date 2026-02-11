package co.com.bancolombia.model.gateways;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.dto.TopProductByBranch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.product.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseGateway {

    Mono<Franchise> createFranchise(String name);
    Mono<Branch> addBranch(String franchiseId, String branchName);
    Mono<Product> addProduct(String branchId, String productName, int stock);
    Mono<Void> deleteProduct(String productId);
    Mono<Product> updateProductStock(String productId, int newStock);
    Flux<TopProductByBranch> topProductByBranch(String franchiseId);

    Mono<Franchise> updateFranchiseName(String franchiseId, String newName);
    Mono<Branch> updateBranchName(String branchId, String newName);
    Mono<Product> updateProductName(String productId, String newName);
}