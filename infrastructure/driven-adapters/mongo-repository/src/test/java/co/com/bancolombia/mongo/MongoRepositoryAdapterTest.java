package co.com.bancolombia.mongo.helper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reactivecommons.utils.ObjectMapper;
import org.reactivecommons.utils.ObjectMapperImp;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;

import static org.mockito.Mockito.*;

class AdapterOperationsTest {

    static class Entity {
        final String id;
        final String name;
        Entity(String id, String name) { this.id = id; this.name = name; }
        public String getId() { return id; }
        public String getName() { return name; }
    }

    static class Data {
        String id;
        String name;
        public Data() {}
        public Data(String id, String name) { this.id = id; this.name = name; }
        public String getId() { return id; }
        public String getName() { return name; }
        public void setId(String id) { this.id = id; }
        public void setName(String name) { this.name = name; }
    }

    interface Repo extends ReactiveCrudRepository<Data, String>, ReactiveQueryByExampleExecutor<Data> {}

    static class TestAdapter extends AdapterOperations<Entity, Data, String, Repo> {
        TestAdapter(Repo repository, ObjectMapper mapper, Function<Data, Entity> toEntityFn) {
            super(repository, mapper, toEntityFn);
        }
    }

    @Test
    void saveShouldMapAndDelegateToRepository() {
        Repo repo = Mockito.mock(Repo.class);
        ObjectMapper mapper = new ObjectMapperImp();

        when(repo.save(any(Data.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        TestAdapter adapter = new TestAdapter(repo, mapper, d -> new Entity(d.getId(), d.getName()));

        StepVerifier.create(adapter.save(new Entity("1", "A")))
                .expectNextMatches(e -> e.getId().equals("1") && e.getName().equals("A"))
                .verifyComplete();

        verify(repo).save(any(Data.class));
    }

    @Test
    void findByIdShouldMapResult() {
        Repo repo = Mockito.mock(Repo.class);
        ObjectMapper mapper = new ObjectMapperImp();

        when(repo.findById("1")).thenReturn(Mono.just(new Data("1", "A")));

        TestAdapter adapter = new TestAdapter(repo, mapper, d -> new Entity(d.getId(), d.getName()));

        StepVerifier.create(adapter.findById("1"))
                .expectNextMatches(e -> e.getId().equals("1") && e.getName().equals("A"))
                .verifyComplete();
    }

    @Test
    void findAllShouldMapResults() {
        Repo repo = Mockito.mock(Repo.class);
        ObjectMapper mapper = new ObjectMapperImp();

        when(repo.findAll()).thenReturn(Flux.just(new Data("1", "A"), new Data("2", "B")));

        TestAdapter adapter = new TestAdapter(repo, mapper, d -> new Entity(d.getId(), d.getName()));

        StepVerifier.create(adapter.findAll())
                .expectNextMatches(e -> e.getId().equals("1"))
                .expectNextMatches(e -> e.getId().equals("2"))
                .verifyComplete();
    }

    @Test
    void deleteByIdShouldDelegate() {
        Repo repo = Mockito.mock(Repo.class);
        ObjectMapper mapper = new ObjectMapperImp();

        when(repo.deleteById("1")).thenReturn(Mono.empty());

        TestAdapter adapter = new TestAdapter(repo, mapper, d -> new Entity(d.getId(), d.getName()));

        StepVerifier.create(adapter.deleteById("1"))
                .verifyComplete();

        verify(repo).deleteById("1");
    }
}
