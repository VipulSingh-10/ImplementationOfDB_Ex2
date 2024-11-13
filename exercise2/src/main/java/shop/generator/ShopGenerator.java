package shop.generator;

import shop.Customer;
import shop.Order;
import shop.Product;

import java.util.*;

// TODO: Task 2.2 a)
public class ShopGenerator {
    public static List<Product> generateProducts(int numProducts) {
        List<String> predefinedNames = Arrays.asList(
                "Shampoo", "Soap", "Desktop", "Monitor", "Keyboard",
                "Mouse", "Headphones", "Printer", "Laptop", "Charger",
                "Table", "Chair", "Fan", "Light", "Speaker",
                "Notebook", "Pen", "Pencil", "Eraser", "Bag",
                "Bottle", "Watch", "Phone", "Television", "Camera"
        );
        List<Product> productlist = new ArrayList<Product>();
        for(int i = 0; i < numProducts; i++) {

            int nameindex = i % predefinedNames.size();
            String baseName = predefinedNames.get(nameindex);
            String productname;
            if (i >= predefinedNames.size()) {
                productname = baseName + "New" + (i / predefinedNames.size());
            } else {
                productname = baseName;
            }

            productlist.add(new Product(i, productname));

        }
        return productlist;
    }

    public static List<Order> generateOrders(List<Product> products, int numOrders) {
        List<Order> orders = new ArrayList<>();
        Random random = new Random();
        List<String> shippingAddresses = Arrays.asList(
                "Zimmer 916 Otto-Müller-Str. 41, Gieschehagen, NW 67080",
                "Apt. 677 Walter-Hochapfel-Str. 95a, Nord Matthiasberg, HH 26953",
                "Zimmer 913 Am Quettinger Feld 72, Tillland, BE 84589"
        );

        for (int i = 0; i < numOrders; i++) {
            List<Product> productListPerOrder = new ArrayList<>();
            int numProductsInOrder = random.nextInt(products.size()) + 1;

            for (int j = 0; j < numProductsInOrder; j++) {
                Product randomProduct = products.get(random.nextInt(products.size()));
                productListPerOrder.add(randomProduct);
            }

            String shippingAddress = shippingAddresses.get(random.nextInt(shippingAddresses.size()));
            orders.add(new Order(i, shippingAddress, productListPerOrder));
        }

        return orders;
    }


    public static Customer generateCustomer(List<Product> products, int maxOrders) {
        int OrderNumbers = (int)(Math.random() * maxOrders) + 1;
        List<Order> OrdersForCustomer = generateOrders(products,OrderNumbers);
        int uniqueCustomerId = UUID.randomUUID().hashCode();
        uniqueCustomerId = Math.abs(uniqueCustomerId);
        Customer newCustomer = new Customer(uniqueCustomerId,"TestCustomer","Zimmer 819 Bruchhauser Str. 3, Seidersburg, BY 47383");
        for (Order order : OrdersForCustomer) {
            newCustomer.addOrder(order);
        }


        return newCustomer;
    }

}
