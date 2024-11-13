package shop;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {

    /*
     * orderId should be unique for every new Order!
     */
    private int orderId;
    private String shippingAddress;
    /*
     * List containing all items (Products) of an order.
     * For this exercise there should be at least one product in every order!
     */
    private List<Product> items;

    public Order(int orderId, String shippingAddress, List<Product> items) {
        this.orderId = orderId;
        this.shippingAddress = shippingAddress;
        this.items = items;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }
    //added this to only display the order details during ShopGeneratorTest
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", items=" + items +
                '}';
    }

    public List<Product> getItems() {
        return items;

    }
}
