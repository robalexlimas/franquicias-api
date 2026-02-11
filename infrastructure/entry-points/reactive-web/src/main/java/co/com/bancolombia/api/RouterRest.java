package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.CreateBranchRequest;
import co.com.bancolombia.api.dto.CreateFranchiseRequest;
import co.com.bancolombia.api.dto.CreateProductRequest;
import co.com.bancolombia.api.dto.UpdateNameRequest;
import co.com.bancolombia.api.dto.UpdateStockRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/franchises",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createFranchise",
                    operation = @Operation(
                            operationId = "createFranchise",
                            summary = "Crear una franquicia",
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = CreateFranchiseRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Franquicia creada"),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                                    @ApiResponse(responseCode = "409", description = "Conflicto (duplicado)")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/name",
                    method = RequestMethod.PATCH,
                    beanClass = Handler.class,
                    beanMethod = "updateFranchiseName",
                    operation = @Operation(
                            operationId = "updateFranchiseName",
                            summary = "Actualizar nombre de franquicia",
                            parameters = @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true),
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Franquicia actualizada"),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                                    @ApiResponse(responseCode = "404", description = "No encontrado")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/branches",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "addBranch",
                    operation = @Operation(
                            operationId = "addBranch",
                            summary = "Agregar sucursal a una franquicia",
                            parameters = @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true),
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = CreateBranchRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Sucursal creada"),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                                    @ApiResponse(responseCode = "404", description = "No encontrado"),
                                    @ApiResponse(responseCode = "409", description = "Conflicto (duplicado)")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{branchId}/name",
                    method = RequestMethod.PATCH,
                    beanClass = Handler.class,
                    beanMethod = "updateBranchName",
                    operation = @Operation(
                            operationId = "updateBranchName",
                            summary = "Actualizar nombre de sucursal",
                            parameters = @Parameter(name = "branchId", in = ParameterIn.PATH, required = true),
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Sucursal actualizada"),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                                    @ApiResponse(responseCode = "404", description = "No encontrado")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{branchId}/products",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "addProduct",
                    operation = @Operation(
                            operationId = "addProduct",
                            summary = "Agregar producto a una sucursal",
                            parameters = @Parameter(name = "branchId", in = ParameterIn.PATH, required = true),
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = CreateProductRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Producto creado"),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                                    @ApiResponse(responseCode = "404", description = "No encontrado"),
                                    @ApiResponse(responseCode = "409", description = "Conflicto (duplicado)")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/products/{productId}/stock",
                    method = RequestMethod.PATCH,
                    beanClass = Handler.class,
                    beanMethod = "updateProductStock",
                    operation = @Operation(
                            operationId = "updateProductStock",
                            summary = "Actualizar stock de un producto",
                            parameters = @Parameter(name = "productId", in = ParameterIn.PATH, required = true),
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateStockRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Stock actualizado"),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                                    @ApiResponse(responseCode = "404", description = "No encontrado")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/products/{productId}/name",
                    method = RequestMethod.PATCH,
                    beanClass = Handler.class,
                    beanMethod = "updateProductName",
                    operation = @Operation(
                            operationId = "updateProductName",
                            summary = "Actualizar nombre de un producto",
                            parameters = @Parameter(name = "productId", in = ParameterIn.PATH, required = true),
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Nombre actualizado"),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                                    @ApiResponse(responseCode = "404", description = "No encontrado")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/products/{productId}",
                    method = RequestMethod.DELETE,
                    beanClass = Handler.class,
                    beanMethod = "deleteProduct",
                    operation = @Operation(
                            operationId = "deleteProduct",
                            summary = "Eliminar un producto",
                            parameters = @Parameter(name = "productId", in = ParameterIn.PATH, required = true),
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Eliminado"),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                                    @ApiResponse(responseCode = "404", description = "No encontrado")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/top-products",
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "topProductsByBranch",
                    operation = @Operation(
                            operationId = "topProductsByBranch",
                            summary = "Producto con mayor stock por sucursal de una franquicia",
                            parameters = @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Listado por sucursal"),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                                    @ApiResponse(responseCode = "404", description = "No encontrado")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return nest(path("/api").and(accept(MediaType.APPLICATION_JSON)),
                route(POST("/franchises"), handler::createFranchise)
                        .andRoute(PATCH("/franchises/{franchiseId}/name"), handler::updateFranchiseName)
                        .andRoute(POST("/franchises/{franchiseId}/branches"), handler::addBranch)
                        .andRoute(PATCH("/branches/{branchId}/name"), handler::updateBranchName)
                        .andRoute(POST("/branches/{branchId}/products"), handler::addProduct)
                        .andRoute(PATCH("/products/{productId}/stock"), handler::updateProductStock)
                        .andRoute(PATCH("/products/{productId}/name"), handler::updateProductName)
                        .andRoute(DELETE("/products/{productId}"), handler::deleteProduct)
                        .andRoute(GET("/franchises/{franchiseId}/top-products"), handler::topProductsByBranch)
        );
    }
}