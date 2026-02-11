package co.com.bancolombia.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopProductByBranch {
    private final String branchId;
    private final String branchName;
    private final String productId;
    private final String productName;
    private final int stock;
}