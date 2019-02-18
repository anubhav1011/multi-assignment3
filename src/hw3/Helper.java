package hw3;

import java.util.ArrayList;
import java.util.List;

public class Helper {


    private List<Customer> activeCustomers;

    private List<Machine> machines;

    private List<Cook> cooks;

    private List<Order> orders;

    private int numOfTables;

    private static Helper helper;


    public Helper(int numOfTables) {

        this.activeCustomers = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.numOfTables = numOfTables;


    }


    public void initMachines(int machineCapacity) {

        machines.add(new Machine("Machine Grill", FoodType.burger, machineCapacity));
        machines.add(new Machine("Machine Frier", FoodType.fries, machineCapacity));
        machines.add(new Machine("Machine Star", FoodType.coffee, machineCapacity));

    }

    public List<Customer> getActiveCustomers() {
        return activeCustomers;
    }

    public int getNumOfTables() {
        return numOfTables;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
