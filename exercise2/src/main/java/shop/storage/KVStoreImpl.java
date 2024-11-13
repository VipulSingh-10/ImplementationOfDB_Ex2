package shop.storage;
import jdbm.PrimaryHashMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import shop.Customer;
import shop.Order;
import shop.Product;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// TODO: Task 2.2 b)
public class KVStoreImpl implements CustomerStore, CustomerStoreQuery {

    private RecordManager recordManager;
    private PrimaryHashMap<Integer,Customer> CustomersMap;
    @Override
    public void open() {
        try {
            recordManager = RecordManagerFactory.createRecordManager("./customerKVDB");
            String recordName = "customerRecords";
            CustomersMap = recordManager.hashMap(recordName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertCustomer(Customer customer) {
        if (recordManager==null || customer==null) {
        throw new IllegalArgumentException("Record Manager isnt open yet");
        }

        try{
            CustomersMap.put(customer.getCustomerId(), customer);
            recordManager.commit();
            //initially for testing - will delete later
            System.out.println("Customer " + customer.getCustomerId() + " has been inserted");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {

        try{
            if(recordManager!=null) {
                recordManager.close();
                System.out.println("Record manager has been closes");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanUp() {
        try{
            if(recordManager!=null) {
                if (CustomersMap != null) {


                    CustomersMap.clear();
                    CustomersMap = null;
                    System.out.println("Customer records has been cleaned up");
                }
                recordManager.commit();
                recordManager.close();
                System.out.println("Record manager has been cleaned");


                recordManager = null;

            }
            else {
                System.out.println("Record manager is null");
            }
            File dbFile = new File("./customerKVDB");
            if (dbFile.exists()) {
                boolean deleted = dbFile.delete();
                if (deleted) {
                    System.out.println("Database file deleted successfully.");
                } else {
                    System.out.println("Failed to delete the database file.");
                }
            } else {
                System.out.println("Database file does not exist.");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryAllUsers() {
        try {
            if (CustomersMap != null) {

                System.out.println("All customers:");
                CustomersMap.forEach((key, customer) -> {
                    System.out.println("Customer ID: " + key + customer.toString());
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override

    public void queryTopProduct() {
        HashMap<Product, Integer> ProductCountHashmap = new HashMap<>();
        if (CustomersMap != null) {
            CustomersMap.forEach((key, customer) -> {
                List<Order> customerOrderList = customer.getOrders();
                for (Order order : customerOrderList) {
                    List<Product> productList = order.getItems();
                    for (Product product : productList) {
                        ProductCountHashmap.put(product, ProductCountHashmap.getOrDefault(product, 0) + 1);
                    }
                }
            });
        }
        Product topProduct = null;
        int maxCount = 0;

        for (Product product : ProductCountHashmap.keySet()) {
            int count = ProductCountHashmap.get(product);
            if (count > maxCount) {
                maxCount = count;
                topProduct = product;
            }
        }


        System.out.println("Top product: " + topProduct.getName() + " with " + maxCount + " orders.");

    }

}
