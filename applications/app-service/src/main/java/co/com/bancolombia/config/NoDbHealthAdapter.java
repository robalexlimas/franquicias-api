package co.com.bancolombia.config;

import co.com.bancolombia.usecase.health.DbHealthPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnMissingBean(DbHealthPort.class)
public class NoDbHealthAdapter implements DbHealthPort {
    @Override
    public Mono<Long> count() {
        return Mono.error(new IllegalStateException("No DB provider configured"));
    }
}