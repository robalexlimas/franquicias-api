package co.com.bancolombia.config;

import co.com.bancolombia.model.gateways.FranchiseGateway;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'UseCase' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public FranchiseGateway franchiseGateway() {
            return Mockito.mock(FranchiseGateway.class);
        }
    }
}
