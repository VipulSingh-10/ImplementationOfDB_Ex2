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
import java.util.Map;

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

        CustomersMap.put(customer.getCustomerId(), customer);

        //initially for testing - will delete later
        //System.out.println("Customer " + customer.getCustomerId() + " has been inserted");
    }
    public void commitchanges() throws IOException {
        recordManager.commit();
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
        if (CustomersMap == null || CustomersMap.isEmpty()) {
            System.out.println("No customer data available to query.");
            return;
        }

        Map<Integer, Integer> productCountMap = new HashMap<>();
        Map<Integer, String> productNamesMap = new HashMap<>();


        CustomersMap.forEach((key, customer) -> {
            List<Order> orders = customer.getOrders();
            for (Order order : orders) {
                for (Product product : order.getItems()) {
                    int productId = product.getProductId();
                    productCountMap.put(productId, productCountMap.getOrDefault(productId, 0) + 1);
                    productNamesMap.putIfAbsent(productId, product.getName());
                }
            }
        });

        int topProductId = -1;
        int maxCount = 0;

        for (Map.Entry<Integer, Integer> entry : productCountMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                topProductId = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        if (topProductId != -1) {
            String topProductName = productNamesMap.get(topProductId);
            System.out.println("Top product: " + topProductName + " with " + maxCount + " orders.");
        } else {
            System.out.println("No products found.");
        }
    }

}
