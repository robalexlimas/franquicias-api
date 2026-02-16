package co.com.bancolombia.mongo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MongoFranchiseAdapterUpdateNamesTest {

    FranchiseMongoRepository franchiseRepo;
    BranchMongoRepository branchRepo;
    ProductMongoRepository productRepo;

    MongoFranchiseAdapter adapter;

    @BeforeEach
    void setUp() {
        franchiseRepo = mock(FranchiseMongoRepository.class);
        branchRepo = mock(BranchMongoRepository.class);
        productRepo = mock(ProductMongoRepository.class);
        adapter = new MongoFranchiseAdapter(franchiseRepo, branchRepo, productRepo);
    }

    // -------------------------
    // updateFranchiseName
    // -------------------------

    @Test
    void updateFranchiseName_shouldFail_whenIdBlank() {
        StepVerifier.create(adapter.updateFranchiseName("  ", "New"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().contains("Franchise id must not be empty"))
                .verify();
    }

    @Test
    void updateFranchiseName_shouldFail_whenNameBlank() {
        StepVerifier.create(adapter.updateFranchiseName("f-1", " "))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().contains("New franchise name must not be empty"))
                .verify();
    }

    @Test
    void updateFranchiseName_shouldFail_whenNotFound() {
        when(franchiseRepo.findById("f-1")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.updateFranchiseName("f-1", "New"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().contains("Franchise not found"))
                .verify();

        verify(franchiseRepo).findById("f-1");
        verifyNoMoreInteractions(franchiseRepo);
        verifyNoInteractions(branchRepo, productRepo);
    }

    @Test
    void updateFranchiseName_shouldAllowSameName_andSave() {
        Instant created = Instant.parse("2025-01-01T00:00:00Z");
        Instant updated = Instant.parse("2025-01-02T00:00:00Z");
        FranchiseDocument existing = new FranchiseDocument("f-1", "ACME", created, updated);

        when(franchiseRepo.findById("f-1")).thenReturn(Mono.just(existing));
        // Igual se llama existsByName, pero la rama "same name" debe ignorar el dup y guardar.
        when(franchiseRepo.existsByName("ACME")).thenReturn(Mono.just(true));
        when(franchiseRepo.save(any(FranchiseDocument.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(adapter.updateFranchiseName("f-1", " ACME "))
                .assertNext(fr -> {
                    // solo validamos lo esencial
                    org.junit.jupiter.api.Assertions.assertEquals("f-1", fr.getId());
                    org.junit.jupiter.api.Assertions.assertEquals("ACME", fr.getName());
                })
                .verifyComplete();

        verify(franchiseRepo).findById("f-1");
        verify(franchiseRepo).existsByName("ACME");
        verify(franchiseRepo).save(argThat(doc ->
                doc.getId().equals("f-1")
                        && doc.getName().equals("ACME")
                        && doc.getCreatedAt().equals(created)
                        && doc.getUpdatedAt() != null
        ));
    }

    @Test
    void updateFranchiseName_shouldFail_whenDuplicateName() {
        Instant created = Instant.parse("2025-01-01T00:00:00Z");
        FranchiseDocument existing = new FranchiseDocument("f-1", "OLD", created, created);

        when(franchiseRepo.findById("f-1")).thenReturn(Mono.just(existing));
        when(franchiseRepo.existsByName("NEW")).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.updateFranchiseName("f-1", " NEW "))
                .expectErrorMatches(e -> e instanceof IllegalStateException
                        && e.getMessage().contains("Franchise name already exists"))
                .verify();

        verify(franchiseRepo).findById("f-1");
        verify(franchiseRepo).existsByName("NEW");
        verify(franchiseRepo, never()).save(any());
    }

    @Test
    void updateFranchiseName_shouldSave_whenNotDuplicate() {
        Instant created = Instant.parse("2025-01-01T00:00:00Z");
        FranchiseDocument existing = new FranchiseDocument("f-1", "OLD", created, created);

        when(franchiseRepo.findById("f-1")).thenReturn(Mono.just(existing));
        when(franchiseRepo.existsByName("NEW")).thenReturn(Mono.just(false));
        when(franchiseRepo.save(any(FranchiseDocument.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(adapter.updateFranchiseName("f-1", " NEW "))
                .assertNext(fr -> org.junit.jupiter.api.Assertions.assertEquals("NEW", fr.getName()))
                .verifyComplete();

        verify(franchiseRepo).save(argThat(doc -> doc.getName().equals("NEW")));
    }

    // -------------------------
    // updateBranchName
    // -------------------------

    @Test
    void updateBranchName_shouldFail_whenIdBlank() {
        StepVerifier.create(adapter.updateBranchName(" ", "X"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().contains("Branch id must not be empty"))
                .verify();
    }

    @Test
    void updateBranchName_shouldFail_whenNameBlank() {
        StepVerifier.create(adapter.updateBranchName("b-1", " "))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().contains("New branch name must not be empty"))
                .verify();
    }

    @Test
    void updateBranchName_shouldFail_whenNotFound() {
        when(branchRepo.findById("b-1")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.updateBranchName("b-1", "NEW"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().contains("Branch not found"))
                .verify();

        verify(branchRepo).findById("b-1");
        verify(branchRepo, never()).save(any());
    }

    @Test
    void updateBranchName_shouldAllowSameName_andSave() {
        Instant created = Instant.parse("2025-01-01T00:00:00Z");
        BranchDocument existing = new BranchDocument("b-1", "f-1", "BRANCH", created, created);

        when(branchRepo.findById("b-1")).thenReturn(Mono.just(existing));
        when(branchRepo.existsByFranchiseIdAndName("f-1", "BRANCH")).thenReturn(Mono.just(true));
        when(branchRepo.save(any(BranchDocument.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(adapter.updateBranchName("b-1", " BRANCH "))
                .assertNext(br -> org.junit.jupiter.api.Assertions.assertEquals("BRANCH", br.getName()))
                .verifyComplete();

        verify(branchRepo).save(argThat(doc -> doc.getName().equals("BRANCH")));
    }

    @Test
    void updateBranchName_shouldFail_whenDuplicateInFranchise() {
        Instant created = Instant.parse("2025-01-01T00:00:00Z");
        BranchDocument existing = new BranchDocument("b-1", "f-1", "OLD", created, created);

        when(branchRepo.findById("b-1")).thenReturn(Mono.just(existing));
        when(branchRepo.existsByFranchiseIdAndName("f-1", "NEW")).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.updateBranchName("b-1", " NEW "))
                .expectErrorMatches(e -> e instanceof IllegalStateException
                        && e.getMessage().contains("Branch name already exists for this franchise"))
                .verify();

        verify(branchRepo, never()).save(any());
    }

    @Test
    void updateBranchName_shouldSave_whenNotDuplicate() {
        Instant created = Instant.parse("2025-01-01T00:00:00Z");
        BranchDocument existing = new BranchDocument("b-1", "f-1", "OLD", created, created);

        when(branchRepo.findById("b-1")).thenReturn(Mono.just(existing));
        when(branchRepo.existsByFranchiseIdAndName("f-1", "NEW")).thenReturn(Mono.just(false));
        when(branchRepo.save(any(BranchDocument.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(adapter.updateBranchName("b-1", " NEW "))
                .assertNext(br -> org.junit.jupiter.api.Assertions.assertEquals("NEW", br.getName()))
                .verifyComplete();

        verify(branchRepo).save(argThat(doc -> doc.getName().equals("NEW")));
    }

    // -------------------------
    // updateProductName
    // -------------------------

    @Test
    void updateProductName_shouldFail_whenIdBlank() {
        StepVerifier.create(adapter.updateProductName(" ", "X"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().contains("Product id must not be empty"))
                .verify();
    }

    @Test
    void updateProductName_shouldFail_whenNameBlank() {
        StepVerifier.create(adapter.updateProductName("p-1", " "))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().contains("New product name must not be empty"))
                .verify();
    }

    @Test
    void updateProductName_shouldFail_whenNotFound() {
        when(productRepo.findById("p-1")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.updateProductName("p-1", "NEW"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException
                        && e.getMessage().contains("Product not found"))
                .verify();

        verify(productRepo).findById("p-1");
        verify(productRepo, never()).save(any());
    }

    @Test
    void updateProductName_shouldAllowSameName_andSave() {
        Instant created = Instant.parse("2025-01-01T00:00:00Z");
        ProductDocument existing = new ProductDocument("p-1", "b-1", "PROD", 10, created, created);

        when(productRepo.findById("p-1")).thenReturn(Mono.just(existing));
        when(productRepo.existsByBranchIdAndName("b-1", "PROD")).thenReturn(Mono.just(true));
        when(productRepo.save(any(ProductDocument.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(adapter.updateProductName("p-1", " PROD "))
                .assertNext(p -> org.junit.jupiter.api.Assertions.assertEquals("PROD", p.getName()))
                .verifyComplete();

        verify(productRepo).save(argThat(doc -> doc.getName().equals("PROD")));
    }

    @Test
    void updateProductName_shouldFail_whenDuplicateInBranch() {
        Instant created = Instant.parse("2025-01-01T00:00:00Z");
        ProductDocument existing = new ProductDocument("p-1", "b-1", "OLD", 10, created, created);

        when(productRepo.findById("p-1")).thenReturn(Mono.just(existing));
        when(productRepo.existsByBranchIdAndName("b-1", "NEW")).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.updateProductName("p-1", " NEW "))
                .expectErrorMatches(e -> e instanceof IllegalStateException
                        && e.getMessage().contains("Product name already exists for this branch"))
                .verify();

        verify(productRepo, never()).save(any());
    }

    @Test
    void updateProductName_shouldSave_whenNotDuplicate() {
        Instant created = Instant.parse("2025-01-01T00:00:00Z");
        ProductDocument existing = new ProductDocument("p-1", "b-1", "OLD", 10, created, created);

        when(productRepo.findById("p-1")).thenReturn(Mono.just(existing));
        when(productRepo.existsByBranchIdAndName("b-1", "NEW")).thenReturn(Mono.just(false));
        when(productRepo.save(any(ProductDocument.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(adapter.updateProductName("p-1", " NEW "))
                .assertNext(p -> org.junit.jupiter.api.Assertions.assertEquals("NEW", p.getName()))
                .verifyComplete();

        verify(productRepo).save(argThat(doc -> doc.getName().equals("NEW")));
    }
}
