package co.com.bancolombia.mongo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import reactor.core.publisher.Mono;

@Configuration
@ConditionalOnProperty(name = "db.provider", havingValue = "mongodb")
public class MongoIndexesConfig {

    @Bean
    public Mono<Void> ensureIndexes(ReactiveMongoTemplate template) {
        return template.indexOps(FranchiseDocument.class)
                .ensureIndex(new Index().on("name", Sort.Direction.ASC).unique())
                .then(template.indexOps(BranchDocument.class)
                        .ensureIndex(new Index().on("franchiseId", Sort.Direction.ASC)))
                .then(template.indexOps(BranchDocument.class)
                        .ensureIndex(new Index().on("franchiseId", Sort.Direction.ASC)
                                .on("name", Sort.Direction.ASC).unique()))
                .then(template.indexOps(ProductDocument.class)
                        .ensureIndex(new Index().on("branchId", Sort.Direction.ASC)))
                .then(template.indexOps(ProductDocument.class)
                        .ensureIndex(new Index().on("branchId", Sort.Direction.ASC)
                                .on("name", Sort.Direction.ASC).unique()))
                .then(template.indexOps(ProductDocument.class)
                        .ensureIndex(new Index().on("branchId", Sort.Direction.ASC)
                                .on("stock", Sort.Direction.DESC)))
                .then();
    }
}