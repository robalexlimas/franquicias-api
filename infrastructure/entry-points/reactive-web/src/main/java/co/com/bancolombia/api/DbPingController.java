package co.com.bancolombia.api;

import co.com.bancolombia.usecase.health.DbHealthPort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class DbPingController {

    private final DbHealthPort db;

    public DbPingController(DbHealthPort db) {
        this.db = db;
    }

    @GetMapping("/health/db")
    public Mono<Map<String, Object>> ping() {
        return db.count()
                .map(count -> {
                    Map<String, Object> ok = new HashMap<>();
                    ok.put("connected", true);
                    ok.put("collection", "franchises");
                    ok.put("count", count);
                    return ok;
                })
                .onErrorResume(e -> {
                    Map<String, Object> fail = new HashMap<>();
                    fail.put("connected", false);
                    fail.put("error", e.getMessage());
                    return Mono.just(fail);
                });
    }
}