package co.com.bancolombia.api.dto;

public class CreateProductRequest {
    private String name;
    private int stock;

    public CreateProductRequest() {}

    public String getName() { return name; }
    public int getStock() { return stock; }

    public void setName(String name) { this.name = name; }
    public void setStock(int stock) { this.stock = stock; }
}