package co.com.bancolombia.mongo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoFranchiseAdapterTest {

    @Mock FranchiseMongoRepository franchiseRepo;
    @Mock BranchMongoRepository branchRepo;
    @Mock ProductMongoRepository productRepo;

    MongoFranchiseAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new MongoFranchiseAdapter(franchiseRepo, branchRepo, productRepo);
    }

    @Test
    void createFranchise_shouldError_whenNameIsBlank() {
        StepVerifier.create(adapter.createFranchise("   "))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().contains("must not be empty"))
                .verify();

        verifyNoInteractions(franchiseRepo, branchRepo, productRepo);
    }

    @Test
    void createFranchise_shouldError_whenDuplicateName() {
        when(franchiseRepo.existsByName("Nequi")).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.createFranchise("Nequi"))
                .expectErrorMatches(e -> e instanceof IllegalStateException
                        && e.getMessage().contains("already exists"))
                .verify();

        verify(franchiseRepo).existsByName("Nequi");
        verifyNoMoreInteractions(franchiseRepo);
        verifyNoInteractions(branchRepo, productRepo);
    }

    @Test
    void createFranchise_shouldSaveAndMap_whenOk() {
        Instant now = Instant.now();
        when(franchiseRepo.existsByName("Nequi")).thenReturn(Mono.just(false));
        when(franchiseRepo.save(any(FranchiseDocument.class)))
                .thenAnswer(inv -> {
                    FranchiseDocument doc = inv.getArgument(0);
                    return Mono.just(new FranchiseDocument("f-1", doc.getName(), doc.getCreatedAt(), doc.getUpdatedAt()));
                });

        StepVerifier.create(adapter.createFranchise("  Nequi  "))
                .assertNext(fr -> {
                    org.junit.jupiter.api.Assertions.assertEquals("f-1", fr.getId());
                    org.junit.jupiter.api.Assertions.assertEquals("Nequi", fr.getName());                 })
                .verifyComplete();

        verify(franchiseRepo).existsByName("Nequi");
        verify(franchiseRepo).save(any(FranchiseDocument.class));
        verifyNoInteractions(branchRepo, productRepo);
    }

    @Test
    void addBranch_shouldError_whenFranchiseNotFound() {
        when(franchiseRepo.existsById("f-1")).thenReturn(Mono.just(false));

        StepVerifier.create(adapter.addBranch("f-1", "Sucursal 1"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().contains("Franchise not found"))
                .verify();

        verify(franchiseRepo).existsById("f-1");
        verifyNoInteractions(branchRepo, productRepo);
    }

    @Test
    void addBranch_shouldError_whenDuplicateBranchName() {
        when(franchiseRepo.existsById("f-1")).thenReturn(Mono.just(true));
        when(branchRepo.existsByFranchiseIdAndName("f-1", "Sucursal 1")).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.addBranch("f-1", "Sucursal 1"))
                .expectErrorMatches(e -> e instanceof IllegalStateException
                        && e.getMessage().contains("already exists"))
                .verify();

        verify(franchiseRepo).existsById("f-1");
        verify(branchRepo).existsByFranchiseIdAndName("f-1", "Sucursal 1");
        verifyNoMoreInteractions(franchiseRepo, branchRepo);
        verifyNoInteractions(productRepo);
    }

    @Test
    void addBranch_shouldSaveAndMap_whenOk() {
        when(franchiseRepo.existsById("f-1")).thenReturn(Mono.just(true));
        when(branchRepo.existsByFranchiseIdAndName("f-1", "Sucursal 1")).thenReturn(Mono.just(false));
        when(branchRepo.save(any(BranchDocument.class)))
                .thenAnswer(inv -> {
                    BranchDocument doc = inv.getArgument(0);
                    return Mono.just(new BranchDocument("b-1", doc.getFranchiseId(), doc.getName(), doc.getCreatedAt(), doc.getUpdatedAt()));
                });

        StepVerifier.create(adapter.addBranch("f-1", "  Sucursal 1  "))
                .assertNext(b -> {
                    org.junit.jupiter.api.Assertions.assertEquals("b-1", b.getId());
                    org.junit.jupiter.api.Assertions.assertEquals("f-1", b.getFranchiseId());
                    org.junit.jupiter.api.Assertions.assertEquals("Sucursal 1", b.getName());
                })
                .verifyComplete();

        verify(franchiseRepo).existsById("f-1");
        verify(branchRepo).existsByFranchiseIdAndName("f-1", "Sucursal 1");
        verify(branchRepo).save(any(BranchDocument.class));
        verifyNoInteractions(productRepo);
    }

    @Test
    void topProductByBranch_shouldReturnTopProductOrEmptyPerBranch() {
        when(franchiseRepo.existsById("f-1")).thenReturn(Mono.just(true));

        BranchDocument br1 = new BranchDocument("br-1", "f-1", "Centro", Instant.now(), Instant.now());
        BranchDocument br2 = new BranchDocument("br-2", "f-1", "Norte", Instant.now(), Instant.now());
        when(branchRepo.findAllByFranchiseId("f-1")).thenReturn(Flux.just(br1, br2));

        ProductDocument p1 = new ProductDocument("p-1", "br-1", "Burger", 50, Instant.now(), Instant.now());
        when(productRepo.findFirstByBranchIdOrderByStockDesc("br-1")).thenReturn(Mono.just(p1));
        when(productRepo.findFirstByBranchIdOrderByStockDesc("br-2")).thenReturn(Mono.empty()); 
        StepVerifier.create(adapter.topProductByBranch("f-1"))
                .assertNext(t -> {
                    org.junit.jupiter.api.Assertions.assertEquals("br-1", t.getBranchId());
                    org.junit.jupiter.api.Assertions.assertEquals("Centro", t.getBranchName());
                    org.junit.jupiter.api.Assertions.assertEquals("p-1", t.getProductId());
                    org.junit.jupiter.api.Assertions.assertEquals("Burger", t.getProductName());
                    org.junit.jupiter.api.Assertions.assertEquals(50, t.getStock());
                })
                .assertNext(t -> {
                    org.junit.jupiter.api.Assertions.assertEquals("br-2", t.getBranchId());
                    org.junit.jupiter.api.Assertions.assertEquals("Norte", t.getBranchName());
                    org.junit.jupiter.api.Assertions.assertNull(t.getProductId());
                    org.junit.jupiter.api.Assertions.assertNull(t.getProductName());
                    org.junit.jupiter.api.Assertions.assertEquals(0, t.getStock());
                })
                .verifyComplete();

        verify(franchiseRepo).existsById("f-1");
        verify(branchRepo).findAllByFranchiseId("f-1");
        verify(productRepo).findFirstByBranchIdOrderByStockDesc("br-1");
        verify(productRepo).findFirstByBranchIdOrderByStockDesc("br-2");
    }

    @Test
    void addProduct_shouldError_whenBranchIdBlank() {
        StepVerifier.create(adapter.addProduct("  ", "Burger", 10))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().contains("Branch id must not be empty"))
                .verify();

        verifyNoInteractions(franchiseRepo, branchRepo, productRepo);
    }

    @Test
    void addProduct_shouldError_whenProductNameBlank() {
        StepVerifier.create(adapter.addProduct("br-1", "   ", 10))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().contains("Product name must not be empty"))
                .verify();

        verifyNoInteractions(franchiseRepo, branchRepo, productRepo);
    }

    @Test
    void addProduct_shouldError_whenStockNegative() {
        StepVerifier.create(adapter.addProduct("br-1", "Burger", -1))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().contains("Stock must be"))
                .verify();

        verifyNoInteractions(franchiseRepo, branchRepo, productRepo);
    }

    @Test
    void addProduct_shouldSaveAndMap_whenOk() {
        when(branchRepo.existsById("br-1")).thenReturn(Mono.just(true));
        when(productRepo.existsByBranchIdAndName("br-1", "Burger")).thenReturn(Mono.just(false));
        when(productRepo.save(any(ProductDocument.class)))
                .thenAnswer(inv -> {
                    ProductDocument doc = inv.getArgument(0);
                    return Mono.just(new ProductDocument(
                            "p-1", doc.getBranchId(), doc.getName(), doc.getStock(), doc.getCreatedAt(), doc.getUpdatedAt()
                    ));
                });

        StepVerifier.create(adapter.addProduct("br-1", "  Burger  ", 10))
                .assertNext(p -> {
                    org.junit.jupiter.api.Assertions.assertEquals("p-1", p.getId());
                    org.junit.jupiter.api.Assertions.assertEquals("br-1", p.getBranchId());
                    org.junit.jupiter.api.Assertions.assertEquals("Burger", p.getName());
                    org.junit.jupiter.api.Assertions.assertEquals(10, p.getStock());
                })
                .verifyComplete();

        verify(branchRepo).existsById("br-1");
        verify(productRepo).existsByBranchIdAndName("br-1", "Burger");
        verify(productRepo).save(any(ProductDocument.class));
        verifyNoInteractions(franchiseRepo);
    }

    @Test
    void deleteProduct_shouldError_whenProductIdBlank() {
        StepVerifier.create(adapter.deleteProduct("  "))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().contains("Product id must not be empty"))
                .verify();

        verifyNoInteractions(franchiseRepo, branchRepo, productRepo);
    }

    @Test
    void deleteProduct_shouldError_whenNotFound() {
        when(productRepo.existsById("p-1")).thenReturn(Mono.just(false));

        StepVerifier.create(adapter.deleteProduct("p-1"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().contains("Product not found"))
                .verify();

        verify(productRepo).existsById("p-1");
        verifyNoMoreInteractions(productRepo);
        verifyNoInteractions(franchiseRepo, branchRepo);
    }

    @Test
    void deleteProduct_shouldDelete_whenExists() {
        when(productRepo.existsById("p-1")).thenReturn(Mono.just(true));
        when(productRepo.deleteById("p-1")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteProduct("p-1"))
                .verifyComplete();

        verify(productRepo).existsById("p-1");
        verify(productRepo).deleteById("p-1");
        verifyNoInteractions(franchiseRepo, branchRepo);
    }

    @Test
    void updateProductStock_shouldError_whenProductIdBlank() {
        StepVerifier.create(adapter.updateProductStock("  ", 10))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().contains("Product id must not be empty"))
                .verify();

        verifyNoInteractions(franchiseRepo, branchRepo, productRepo);
    }

    @Test
    void updateProductStock_shouldError_whenStockNegative() {
        StepVerifier.create(adapter.updateProductStock("p-1", -1))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().contains("Stock must be"))
                .verify();

        verifyNoInteractions(franchiseRepo, branchRepo, productRepo);
    }

    @Test
    void updateProductStock_shouldError_whenProductNotFound() {
        when(productRepo.findById("p-1")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.updateProductStock("p-1", 10))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().contains("Product not found"))
                .verify();

        verify(productRepo).findById("p-1");
        verifyNoMoreInteractions(productRepo);
        verifyNoInteractions(franchiseRepo, branchRepo);
    }

    @Test
    void updateProductStock_shouldSaveUpdatedStock_whenOk() {
        ProductDocument existing = new ProductDocument(
                "p-1", "br-1", "Burger", 5, Instant.now(), Instant.now()
        );

        when(productRepo.findById("p-1")).thenReturn(Mono.just(existing));
        when(productRepo.save(any(ProductDocument.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(adapter.updateProductStock("p-1", 99))
                .assertNext(p -> {
                    org.junit.jupiter.api.Assertions.assertEquals("p-1", p.getId());
                    org.junit.jupiter.api.Assertions.assertEquals("br-1", p.getBranchId());
                    org.junit.jupiter.api.Assertions.assertEquals("Burger", p.getName());
                    org.junit.jupiter.api.Assertions.assertEquals(99, p.getStock());
                })
                .verifyComplete();

        verify(productRepo).findById("p-1");
        verify(productRepo).save(any(ProductDocument.class));
        verifyNoInteractions(franchiseRepo, branchRepo);
    }
}
