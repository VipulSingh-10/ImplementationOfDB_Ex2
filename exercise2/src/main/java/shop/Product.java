package shop;

import java.io.Serializable;

public class Product implements Serializable {

    /*
     * productId should be unique for every new Product!
     */
    private int productId;
    private String name;

    public Product(int productId, String name) {
        this.productId = productId;
        this.name = name;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }
    //added this to only display the product details during ShopGeneratorTest
    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                '}';
    }
}
