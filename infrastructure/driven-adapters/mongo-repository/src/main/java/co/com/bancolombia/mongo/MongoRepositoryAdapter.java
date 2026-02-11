package co.com.bancolombia.mongo;

import co.com.bancolombia.mongo.helper.AdapterOperations;
import co.com.bancolombia.usecase.health.DbHealthPort;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class MongoRepositoryAdapter extends AdapterOperations<
        TestDocument,
        TestDocument,
        String,
        MongoDBRepository
        > implements DbHealthPort {

    public MongoRepositoryAdapter(MongoDBRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> d);
    }

    @Override
    public Mono<Long> count() {
        return repository.count();
    }
}