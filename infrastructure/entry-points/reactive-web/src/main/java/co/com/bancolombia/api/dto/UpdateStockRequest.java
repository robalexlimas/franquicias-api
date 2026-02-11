package co.com.bancolombia.api.dto;

public class UpdateStockRequest {
    private int stock;

    public UpdateStockRequest() {}

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}