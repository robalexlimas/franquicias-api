package co.com.bancolombia.mongo;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.dto.TopProductByBranch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.gateways.FranchiseGateway;
import co.com.bancolombia.model.product.Product;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Repository
@ConditionalOnProperty(name = "db.provider", havingValue = "mongodb")
public class MongoFranchiseAdapter implements FranchiseGateway {

    private final FranchiseMongoRepository franchiseRepo;
    private final BranchMongoRepository branchRepo;
    private final ProductMongoRepository productRepo;

    public MongoFranchiseAdapter(FranchiseMongoRepository franchiseRepo,
                                 BranchMongoRepository branchRepo,
                                 ProductMongoRepository productRepo) {
        this.franchiseRepo = franchiseRepo;
        this.branchRepo = branchRepo;
        this.productRepo = productRepo;
    }

    @Override
    public Mono<Franchise> createFranchise(String name) {
        Instant now = Instant.now();
        if (name == null || name.isBlank()) {
            return Mono.error(new IllegalArgumentException("Franchise name must not be empty"));
        }

        return franchiseRepo.existsByName(name.trim())
                .flatMap(exists -> exists
                        ? Mono.error(new IllegalStateException("Franchise name already exists"))
                        : franchiseRepo.save(new FranchiseDocument(null, name.trim(), now, now))
                )
                .map(this::toDomain);
    }

    @Override
    public Mono<Branch> addBranch(String franchiseId, String branchName) {
        Instant now = Instant.now();
        if (franchiseId == null || franchiseId.isBlank()) {
            return Mono.error(new IllegalArgumentException("Franchise id must not be empty"));
        }
        if (branchName == null || branchName.isBlank()) {
            return Mono.error(new IllegalArgumentException("Branch name must not be empty"));
        }

        String cleanName = branchName.trim();

        return franchiseRepo.existsById(franchiseId)
                .flatMap(exists -> exists
                        ? branchRepo.existsByFranchiseIdAndName(franchiseId, cleanName)
                        : Mono.error(new IllegalArgumentException("Franchise not found"))
                )
                .flatMap(duplicate -> duplicate
                        ? Mono.error(new IllegalStateException("Branch name already exists for this franchise"))
                        : branchRepo.save(new BranchDocument(null, franchiseId, cleanName, now, now))
                )
                .map(this::toDomain);
    }

    @Override
    public Mono<Product> addProduct(String branchId, String productName, int stock) {
        Instant now = Instant.now();
        if (branchId == null || branchId.isBlank()) {
            return Mono.error(new IllegalArgumentException("Branch id must not be empty"));
        }
        if (productName == null || productName.isBlank()) {
            return Mono.error(new IllegalArgumentException("Product name must not be empty"));
        }
        if (stock < 0) {
            return Mono.error(new IllegalArgumentException("Stock must be >= 0"));
        }

        String cleanName = productName.trim();

        return branchRepo.existsById(branchId)
                .flatMap(exists -> exists
                        ? productRepo.existsByBranchIdAndName(branchId, cleanName)
                        : Mono.error(new IllegalArgumentException("Branch not found"))
                )
                .flatMap(duplicate -> duplicate
                        ? Mono.error(new IllegalStateException("Product name already exists for this branch"))
                        : productRepo.save(new ProductDocument(null, branchId, cleanName, stock, now, now))
                )
                .map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteProduct(String productId) {
        if (productId == null || productId.isBlank()) {
            return Mono.error(new IllegalArgumentException("Product id must not be empty"));
        }

        return productRepo.existsById(productId)
                .flatMap(exists -> exists
                        ? productRepo.deleteById(productId)
                        : Mono.error(new IllegalArgumentException("Product not found"))
                );
    }

    @Override
    public Mono<Product> updateProductStock(String productId, int newStock) {
        Instant now = Instant.now();
        if (productId == null || productId.isBlank()) {
            return Mono.error(new IllegalArgumentException("Product id must not be empty"));
        }
        if (newStock < 0) {
            return Mono.error(new IllegalArgumentException("Stock must be >= 0"));
        }

        return productRepo.findById(productId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Product not found")))
                .flatMap(doc -> productRepo.save(
                        new ProductDocument(doc.getId(), doc.getBranchId(), doc.getName(), newStock, doc.getCreatedAt(), now)
                ))
                .map(this::toDomain);
    }

    @Override
    public Flux<TopProductByBranch> topProductByBranch(String franchiseId) {
        if (franchiseId == null || franchiseId.isBlank()) {
            return Flux.error(new IllegalArgumentException("Franchise id must not be empty"));
        }

        return franchiseRepo.existsById(franchiseId)
                .flatMapMany(exists -> exists
                        ? branchRepo.findAllByFranchiseId(franchiseId)
                        : Flux.error(new IllegalArgumentException("Franchise not found"))
                )
                .flatMap(branchDoc ->
                        productRepo.findFirstByBranchIdOrderByStockDesc(branchDoc.getId())
                                .map(prod -> new TopProductByBranch(
                                        branchDoc.getId(),
                                        branchDoc.getName(),
                                        prod.getId(),
                                        prod.getName(),
                                        prod.getStock() == null ? 0 : prod.getStock()
                                ))
                                .switchIfEmpty(Mono.just(new TopProductByBranch(
                                        branchDoc.getId(),
                                        branchDoc.getName(),
                                        null,
                                        null,
                                        0
                                )))
                );
    }

    @Override
    public Mono<Franchise> updateFranchiseName(String franchiseId, String newName) {
        Instant now = Instant.now();
        if (franchiseId == null || franchiseId.isBlank()) {
            return Mono.error(new IllegalArgumentException("Franchise id must not be empty"));
        }
        if (newName == null || newName.isBlank()) {
            return Mono.error(new IllegalArgumentException("New franchise name must not be empty"));
        }

        String cleanName = newName.trim();

        return franchiseRepo.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franchise not found")))
                .flatMap(existing ->
                        franchiseRepo.existsByName(cleanName)
                                .flatMap(dup -> {
                                    if (existing.getName() != null && existing.getName().equals(cleanName)) {
                                        return franchiseRepo.save(new FranchiseDocument(existing.getId(), cleanName, existing.getCreatedAt(), now));
                                    }
                                    return dup
                                            ? Mono.error(new IllegalStateException("Franchise name already exists"))
                                            : franchiseRepo.save(new FranchiseDocument(existing.getId(), cleanName, existing.getCreatedAt(), now));
                                })
                )
                .map(this::toDomain);
    }

    @Override
    public Mono<Branch> updateBranchName(String branchId, String newName) {
        Instant now = Instant.now();
        if (branchId == null || branchId.isBlank()) {
            return Mono.error(new IllegalArgumentException("Branch id must not be empty"));
        }
        if (newName == null || newName.isBlank()) {
            return Mono.error(new IllegalArgumentException("New branch name must not be empty"));
        }

        String cleanName = newName.trim();

        return branchRepo.findById(branchId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Branch not found")))
                .flatMap(existing ->
                        branchRepo.existsByFranchiseIdAndName(existing.getFranchiseId(), cleanName)
                                .flatMap(dup -> {
                                    if (existing.getName() != null && existing.getName().equals(cleanName)) {
                                        return branchRepo.save(new BranchDocument(existing.getId(), existing.getFranchiseId(), cleanName, existing.getCreatedAt(), now));
                                    }
                                    return dup
                                            ? Mono.error(new IllegalStateException("Branch name already exists for this franchise"))
                                            : branchRepo.save(new BranchDocument(existing.getId(), existing.getFranchiseId(), cleanName, existing.getCreatedAt(), now));
                                })
                )
                .map(this::toDomain);
    }

    @Override
    public Mono<Product> updateProductName(String productId, String newName) {
        Instant now = Instant.now();
        if (productId == null || productId.isBlank()) {
            return Mono.error(new IllegalArgumentException("Product id must not be empty"));
        }
        if (newName == null || newName.isBlank()) {
            return Mono.error(new IllegalArgumentException("New product name must not be empty"));
        }

        String cleanName = newName.trim();

        return productRepo.findById(productId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Product not found")))
                .flatMap(existing ->
                        productRepo.existsByBranchIdAndName(existing.getBranchId(), cleanName)
                                .flatMap(dup -> {
                                    if (existing.getName() != null && existing.getName().equals(cleanName)) {
                                        return productRepo.save(new ProductDocument(existing.getId(), existing.getBranchId(), cleanName, existing.getStock(), existing.getCreatedAt(), now));
                                    }
                                    return dup
                                            ? Mono.error(new IllegalStateException("Product name already exists for this branch"))
                                            : productRepo.save(new ProductDocument(existing.getId(), existing.getBranchId(), cleanName, existing.getStock(), existing.getCreatedAt(), now));
                                })
                )
                .map(this::toDomain);
    }

    private Franchise toDomain(FranchiseDocument doc) {
        return new Franchise(doc.getId(), doc.getName(), doc.getCreatedAt(), doc.getUpdatedAt());
    }

    private Branch toDomain(BranchDocument doc) {
        return new Branch(doc.getId(), doc.getFranchiseId(), doc.getName(), doc.getCreatedAt(), doc.getUpdatedAt());
    }

    private Product toDomain(ProductDocument doc) {
        int st = doc.getStock() == null ? 0 : doc.getStock();
        return new Product(doc.getId(), doc.getBranchId(), doc.getName(), st, doc.getCreatedAt(), doc.getUpdatedAt());
    }
}