package hw3;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {
    private final String name;

    private Helper h;

    /**
     * You can feel free modify this constructor.  It must
     * take at least the name, but may take other parameters
     * if you would find adding them useful.
     *
     * @param: the name of the cook
     */
    public Cook(String name) {
        this.name = name;
        this.h = Simulation.getHelper();
    }

    public String toString() {
        return name;
    }

    /**
     * This method executes as follows.  The cook tries to retrieve
     * orders placed by Customers.  For each order, a List<Food>, the
     * cook submits each Food item in the List to an appropriate
     * Machine, by calling makeFood().  Once all machines have
     * produced the desired Food, the order is complete, and the Customer
     * is notified.  The cook can then go to process the next order.
     * If during its execution the cook is interrupted (i.e., some
     * other thread calls the interrupt() method on it, which could
     * raise InterruptedException if the cook is blocking), then it
     * terminates.
     */
    public void run() {

        Simulation.logEvent(SimulationEvent.cookStarting(this));

        try {
            while (true) {

                Order orderToCook = null;

                synchronized (h.getOrders()) {

                    // List<Order> allOrders = h.getOrders();


                    while (getPlacedOrders(h.getOrders()).isEmpty()) {

                        // System.out.println("Cook " + this + " waiting");

                        h.getOrders().wait();

//                        try {
//                            allOrders.wait();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                    }

                    List<Order> newOrders = getPlacedOrders(h.getOrders());
                    System.out.println("New Orders Size " + newOrders.size() + " for " + this);


                    if (newOrders.size() > 1) {

                        System.out.println("More than one orders in cooks hand");
                        orderToCook = getOrdersByPriority(newOrders);

                    } else {
                        orderToCook = newOrders.get(0);
                    }

                    orderToCook.setOrderStatus(Order.OrderStatus.INPROGRESS);

                    h.getOrders().notifyAll();


                }


                // synchronized (orderToCook) {

                Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, orderToCook.getItems(), orderToCook.getOrderNum()));

                List<Food> items = orderToCook.getItems();

                for (Map.Entry<String, Long> stringLongEntry : orderToCook.getFoodCount().entrySet()) {

                    String foodName = stringLongEntry.getKey();

                    Food food = items.stream().filter(f -> f.getName().equals(foodName)).findAny().orElse(null);

                    Machine machine = h.findMAchineforFood(food);
                    machine.makeFood(stringLongEntry.getValue().intValue(), orderToCook, food);
                    Simulation.logEvent(SimulationEvent.cookStartedFood(this, food, orderToCook.getOrderNum()));


                }


                synchronized (orderToCook) {

                    while (!(orderToCook.getNoOfItemsRemaining() == 0)) {

                        //System.out.println("Cook waiting for order " + orderToCook.getOrderNum() + " to cook");

                        orderToCook.wait();
                    }

                    for (Food item : items) {

                        Simulation.logEvent(SimulationEvent.cookFinishedFood(this, item, orderToCook.getOrderNum()));


                    }


                    synchronized (h.getOrders()) {


                        Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, orderToCook.getOrderNum()));

                        orderToCook.setOrderStatus(Order.OrderStatus.DONE);
                        h.getOrders().remove(orderToCook);
                        orderToCook.notify();
                        h.getOrders().notifyAll();

                    }

                    //}

                }

            }
        } catch (InterruptedException e) {
//            // This code assumes the provided code in the Simulation class
//            // that interrupts each cook thread when all customers are done.
//            // You might need to change this if you change how things are
//            // done in the Simulation class.
            Simulation.logEvent(SimulationEvent.cookEnding(this));
        }

    }


    private Order getOrdersByPriority(List<Order> orders) {

        synchronized (orders) {

            List<Order> sortedByPriority = orders.stream()
                    .filter(o -> o.getOrderStatus().equals(Order.OrderStatus.PLACED))
                    .sorted((o1, o2) -> {

                        Integer priorityO1 = o1.getPriority().getPriorityValue();
                        Integer priorityO2 = o2.getPriority().getPriorityValue();

                        return priorityO1.compareTo(priorityO2);


                    }).collect(Collectors.toList());

            return sortedByPriority.get(0);

        }


    }

    private List<Order> getPlacedOrders(List<Order> order) {


        return order.stream().filter(o -> o.getOrderStatus().equals(Order.OrderStatus.PLACED)).collect(Collectors.toList());
    }


}