package shop.generator;
import shop.*;
import shop.generator.*;

import java.util.ArrayList;
import java.util.List;

public class ShopGeneratorTest {

    public static void main(String[] args) {
        CustomerDatabase test = new CustomerDatabase(5,2,25);
        List<Product> products = new ArrayList<Product>();
        List<Customer> customers = new ArrayList<Customer>();
        products = test.getProductList();
        customers = test.getCustomersList();
        System.out.println(products);

        for (Customer customer : customers) {
            System.out.println(customer.toString() );

        }


    }
}
