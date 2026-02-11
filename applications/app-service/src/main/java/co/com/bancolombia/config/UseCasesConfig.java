package co.com.bancolombia.config;

import co.com.bancolombia.model.gateways.FranchiseGateway;
import co.com.bancolombia.usecase.franchise.AddBranchUseCase;
import co.com.bancolombia.usecase.franchise.AddProductUseCase;
import co.com.bancolombia.usecase.franchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.franchise.DeleteProductUseCase;
import co.com.bancolombia.usecase.franchise.TopProductByBranchUseCase;
import co.com.bancolombia.usecase.franchise.UpdateBranchNameUseCase;
import co.com.bancolombia.usecase.franchise.UpdateFranchiseNameUseCase;
import co.com.bancolombia.usecase.franchise.UpdateProductNameUseCase;
import co.com.bancolombia.usecase.franchise.UpdateProductStockUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

        @Bean
        CreateFranchiseUseCase createFranchiseUseCase(FranchiseGateway gateway) {
                return new CreateFranchiseUseCase(gateway);
        }

        @Bean
        AddBranchUseCase addBranchUseCase(FranchiseGateway gateway) {
                return new AddBranchUseCase(gateway);
        }

        @Bean
        AddProductUseCase addProductUseCase(FranchiseGateway gateway) {
                return new AddProductUseCase(gateway);
        }

        @Bean
        DeleteProductUseCase deleteProductUseCase(FranchiseGateway gateway) {
                return new DeleteProductUseCase(gateway);
        }

        @Bean
        UpdateProductStockUseCase updateProductStockUseCase(FranchiseGateway gateway) {
                return new UpdateProductStockUseCase(gateway);
        }

        @Bean
        TopProductByBranchUseCase topProductByBranchUseCase(FranchiseGateway gateway) {
                return new TopProductByBranchUseCase(gateway);
        }

        @Bean
        UpdateFranchiseNameUseCase updateFranchiseNameUseCase(FranchiseGateway gateway) {
                return new UpdateFranchiseNameUseCase(gateway);
        }

        @Bean
        UpdateBranchNameUseCase updateBranchNameUseCase(FranchiseGateway gateway) {
                return new UpdateBranchNameUseCase(gateway);
        }

        @Bean
        UpdateProductNameUseCase updateProductNameUseCase(FranchiseGateway gateway) {
                return new UpdateProductNameUseCase(gateway);
        }
}