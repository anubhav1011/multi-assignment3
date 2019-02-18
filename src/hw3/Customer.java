package hw3;

import java.util.List;

import hw3.Order;

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order.  When running, an
 * customer attempts to enter the coffee shop (only successful if the
 * coffee shop has a free table), place its order, and then leave the
 * coffee shop when the order is complete.
 */
public class Customer implements Runnable {
    //JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
    private final String name;
    //private final List<Food> order;
    private final int orderNum;

    private final Order order;

    private static Helper h;


    private static int runningCounter = 0;

    /**
     * You can feel free modify this constructor.  It must take at
     * least the name and order but may take other parameters if you
     * would find adding them useful.
     */
    public Customer(String name, List<Food> items) {
        this.name = name;
        this.order = new Order(items, 1);
        this.orderNum = ++runningCounter;
        this.h = Simulation.getHelper();
    }

    public String toString() {
        return name;
    }

    /**
     * This method defines what an Customer does: The customer attempts to
     * enter the coffee shop (only successful when the coffee shop has a
     * free table), place its order, and then leave the coffee shop
     * when the order is complete.
     */
    public void run() {
        //YOUR CODE GOES HERE...

        Simulation.logEvent(SimulationEvent.customerStarting(this));

        synchronized (h.getActiveCustomers()) {


            while (h.getActiveCustomers().size() == h.getNumOfTables()) {

                try {
                    h.getActiveCustomers().wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            h.getActiveCustomers().add(this);
            Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));
            h.getActiveCustomers().notifyAll();

        }


        synchronized (h.getOrders()) {

            Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, this.order.getItems(), orderNum));
            this.order.setOrderStatus(Order.OrderStatus.PLACED);
            h.getOrders().add(this.order);
            h.getOrders().notifyAll();

        }


        synchronized (this.order) {
            while (this.order.getOrderStatus() != Order.OrderStatus.DONE) {
                try {
                    this.order.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}