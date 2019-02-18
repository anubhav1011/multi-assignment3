package hw3;

import java.util.ArrayList;
import java.util.List;

public class Helper {


    private List<Customer> activeCustomers;

    private List<Machine> machines;

    private List<Cook> cooks;

    private int numOfTables;

    private static Helper helper;


    public Helper(int numOfTables) {

        this.activeCustomers = new ArrayList<>();
        this.numOfTables = numOfTables;


    }


    public void initMachines(int machineCapacity) {

        machines.add(new Machine("Machine Grill", FoodType.burger, machineCapacity));
        machines.add(new Machine("Machine Frier", FoodType.fries, machineCapacity));
        machines.add(new Machine("Machine Star", FoodType.coffee, machineCapacity));

    }

}
