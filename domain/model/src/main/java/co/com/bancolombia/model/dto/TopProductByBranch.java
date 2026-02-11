package co.com.bancolombia.model.dto;

public class TopProductByBranch {
    private final String branchId;
    private final String branchName;
    private final String productId;
    private final String productName;
    private final int stock;

    public TopProductByBranch(String branchId, String branchName, String productId, String productName, int stock) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.productId = productId;
        this.productName = productName;
        this.stock = stock;
    }

    public String getBranchId() { return branchId; }
    public String getBranchName() { return branchName; }
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getStock() { return stock; }
}