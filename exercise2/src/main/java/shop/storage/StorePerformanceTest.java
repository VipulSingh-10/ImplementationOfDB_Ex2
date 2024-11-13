package shop.storage;

import shop.Customer;
import shop.generator.CustomerDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StorePerformanceTest {

    public static void main(String[] args) throws IOException {
        CustomerDatabase customerDatabase = new CustomerDatabase(10000, 10, 5);
        List<Customer> customers = customerDatabase.getCustomersList();


        KVStoreImpl kvStore = new KVStoreImpl();
        kvStore.open();
        long startKVStore = System.currentTimeMillis();
        for (Customer customer : customers) {
            kvStore.insertCustomer(customer);
        }
        kvStore.commitchanges();
        long endKVStore = System.currentTimeMillis();


        H2StoreImpl h2Store = new H2StoreImpl();
        h2Store.open();
        long startH2Store = System.currentTimeMillis();
        for (Customer customer : customers) {
            h2Store.insertCustomer(customer);
        }

        long endH2Store = System.currentTimeMillis();

        System.out.println("KVstore query all users below : ");
        kvStore.queryAllUsers();

        System.out.println("H2Store query all users below : ");
        h2Store.queryAllUsers();

        System.out.println("Top product in KV Store");
        kvStore.queryTopProduct();

        System.out.println("Top product in H2 Store");
        h2Store.queryTopProduct();

        //clean and close db
        kvStore.close();
        //kvStore.cleanUp();


        h2Store.close();
        //h2Store.cleanUp();

        System.out.println("Performance Results:");
        System.out.println("KVStoreImpl time: " + (endKVStore - startKVStore) + " ms");
        System.out.println("H2StoreImpl time: " + (endH2Store - startH2Store) + " ms");
    }
}
