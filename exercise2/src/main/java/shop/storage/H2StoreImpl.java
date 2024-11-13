package shop.storage;

import shop.Customer;
import shop.Order;
import shop.Product;

import java.sql.*;

// TODO: Task 2.2 c)
public class H2StoreImpl implements CustomerStore, CustomerStoreQuery {
    private static final String DB_CONNECTION = "jdbc:h2:./customerDB";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    private static Connection connection;
    @Override
    public void open() {
        try {
            connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            connection.setAutoCommit(false);
            createTables();
            System.out.println("DB connection established and tables created.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed in creating database connection.", e);
        }
    }

    private static void createTables() throws SQLException {
        // TODO

        String createCustomer =
                "CREATE TABLE IF NOT EXISTS CUSTOMERS (id INT PRIMARY KEY, name VARCHAR(255), address VARCHAR(255))";
        String createOrder =
                "CREATE TABLE IF NOT EXISTS ORDERS (oid INT PRIMARY KEY, customerId INT, shippingAddress VARCHAR(255), FOREIGN KEY (customerId) REFERENCES CUSTOMERS(id))";
        String createProduct =
                "CREATE TABLE IF NOT EXISTS PRODUCTS (pid INT PRIMARY KEY, pname VARCHAR(255))";
        String createOrderItem =
                "CREATE TABLE IF NOT EXISTS ORDERITEMS (otid INT AUTO_INCREMENT PRIMARY KEY, orderId INT, productId INT, FOREIGN KEY (orderId) REFERENCES ORDERS(oid), FOREIGN KEY (productId) REFERENCES PRODUCTS(pid))";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createCustomer);
            stmt.execute(createOrder);
            stmt.execute(createProduct);
            stmt.execute(createOrderItem);
            connection.commit();
        }
    }

    @Override
    public void insertCustomer(Customer customer) {
        try {
            // Insert customer
            String insertCustomer = "MERGE INTO CUSTOMERS (id, name, address) VALUES (?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(insertCustomer)) {
                ps.setInt(1, customer.getCustomerId());
                ps.setString(2, customer.getUserName());
                ps.setString(3, customer.getAddress());
                ps.executeUpdate();

            }


            for (Order order : customer.getOrders()) {
                String insertOrder = "MERGE INTO ORDERS (oid, customerId, shippingAddress) VALUES (?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(insertOrder)) {
                    ps.setInt(1, order.getOrderId());
                    ps.setInt(2, customer.getCustomerId());
                    ps.setString(3, order.getShippingAddress());
                    ps.executeUpdate();
                    //System.out.println("Order " + order.getOrderId() + " has been inserted");
                }


                for (Product product : order.getItems()) {
                    String insertProduct = "MERGE INTO PRODUCTS (pid, pname) VALUES (?, ?)";
                    try (PreparedStatement ps = connection.prepareStatement(insertProduct)) {
                        ps.setInt(1, product.getProductId());
                        ps.setString(2, product.getName());
                        ps.executeUpdate();
                        //System.out.println("Product " + product.getProductId() + " has been inserted");
                    }

                    String insertOrderItem = "INSERT INTO ORDERITEMS (orderId, productId) VALUES (?, ?)";
                    try (PreparedStatement ps = connection.prepareStatement(insertOrderItem)) {
                        ps.setInt(1, order.getOrderId());
                        ps.setInt(2, product.getProductId());
                        ps.executeUpdate();
                        //System.out.println("Order " + order.getOrderId() + " has been inserted");
                    }
                }
            }
            connection.commit();
            //System.out.println("Customer " + customer.getCustomerId() + " inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw new RuntimeException("Failed to insert customer.", e);
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to close database connection.", e);
        }
    }

    @Override
    public void cleanUp() {
        try {
            close();
            org.h2.tools.DeleteDbFiles.execute(".", "customerDB", true);
            System.out.println("Database cleaned up.");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void queryAllUsers() {
        String query = "SELECT * FROM CUSTOMERS";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println("Customer ID: " + rs.getInt("id") +
                        ", Name: " + rs.getString("name") +
                        ", Address: " + rs.getString("address"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryTopProduct() {
        String query = "SELECT pname, COUNT(*) AS purchase_count FROM ORDERITEMS " +
                "JOIN PRODUCTS ON ORDERITEMS.productId = PRODUCTS.pid " +
                "GROUP BY pname ORDER BY purchase_count DESC LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Top Product: " + rs.getString("pname") +
                        ", Purchases: " + rs.getInt("purchase_count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
