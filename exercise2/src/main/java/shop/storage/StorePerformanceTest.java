package shop.storage;

import shop.Customer;
import shop.Product;
import shop.generator.CustomerDatabase;

import java.util.ArrayList;
import java.util.List;
import shop.storage.*;
public class StorePerformanceTest {

    public static void main(String[] args) {
        CustomerDatabase customerDatabase = new CustomerDatabase(1000, 10, 5);
//        System.out.println(customerDatabase);
//        List<Product> products = new ArrayList<Product>();
//        List<Customer> customers = new ArrayList<Customer>();
//        products = customerDatabase.getProductList();
//        customers = customerDatabase.getCustomersList();
//        System.out.println(products);
//        for (Customer customer : customers) {
//            System.out.println(customer.toString() );
//
//        }
        KVStoreImpl kvStore = new KVStoreImpl();
        kvStore.open();
        List<Customer> customers = new ArrayList<Customer>();
        customers = customerDatabase.getCustomersList();
        for(Customer customer: customers){
            kvStore.insertCustomer(customer);
        }

        kvStore.queryAllUsers();
        kvStore.queryTopProduct();
    }
}
