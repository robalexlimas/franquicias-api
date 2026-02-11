package co.com.bancolombia.usecase.health;

import reactor.core.publisher.Mono;

public interface DbHealthPort {
    Mono<Long> count();
}