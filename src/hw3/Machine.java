package hw3;

import java.util.List;

/**
 * A Machine is used to make a particular Food.  Each Machine makes
 * just one kind of Food.  Each machine has a capacity: it can make
 * that many food items in parallel; if the machine is asked to
 * produce a food item beyond its capacity, the requester blocks.
 * Each food item takes at least item.cookTimeMS milliseconds to
 * produce.
 */
public class Machine {
    public final String machineName;
    public final Food machineFoodType;
    public final int capacityIn;
    private Integer currentFoodCount;

    private volatile Object machineCapacityLock = null;

    //YOUR CODE GOES HERE...


    /**
     * The constructor takes at least the name of the machine,
     * the Food item it makes, and its capacity.  You may extend
     * it with other arguments, if you wish.  Notice that the
     * constructor currently does nothing with the capacity; you
     * must add code to make use of this field (and do whatever
     * initialization etc. you need).
     */
    public Machine(String nameIn, Food foodIn, int capacityIn) {
        this.machineName = nameIn;
        this.machineFoodType = foodIn;
        this.capacityIn = capacityIn;
        this.currentFoodCount = 0;
        this.machineCapacityLock = new Object();

        Simulation.logEvent(SimulationEvent.machineStarting(this, foodIn, capacityIn));

        //YOUR CODE GOES HERE...

    }


    /**
     * This method is called by a Cook in order to make the Machine's
     * food item.  You can extend this method however you like, e.g.,
     * you can have it take extra parameters or return something other
     * than Object.  It should block if the machine is currently at full
     * capacity.  If not, the method should return, so the Cook making
     * the call can proceed.  You will need to implement some means to
     * notify the calling Cook when the food item is finished.
     */
    public void makeFood(int numberOfItems, Order order, Food food) throws InterruptedException {

        //YOUR CODE GOES HERE...


        synchronized (machineCapacityLock) {

            while (currentFoodCount + numberOfItems > capacityIn) {

                System.out.println("Machine " + this.machineName + " on maximum capacity");
                machineCapacityLock.wait();
            }

            currentFoodCount += numberOfItems;

            Thread[] cookItems = new Thread[numberOfItems];

            for (int i = 0; i < numberOfItems; i++) {

                CookAnItem cookAnItem = new CookAnItem(this.machineFoodType.cookTimeMS, this.currentFoodCount, order, food);
                cookItems[i] = new Thread(cookAnItem);


            }

            for (int i = 0; i < numberOfItems; i++) {

                cookItems[i].start();
            }
            machineCapacityLock.notifyAll();

        }


    }

    //THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
    private class CookAnItem implements Runnable {

        public CookAnItem(int sleepTime, Integer currentFoodCount, Order order, Food food) {

            this.sleepTime = sleepTime;
            this.order = order;
            this.currentFoodCount = currentFoodCount;
            this.food = food;

        }

        private final int sleepTime;
        public boolean itemCooked = false;
        private Integer currentFoodCount;
        private Order order;
        private Food food;


        public void run() {


            try {

                Simulation.logEvent(SimulationEvent.machineCookingFood(Machine.this, this.food));
                Thread.sleep(sleepTime);
                synchronized (order) {

                    synchronized (Machine.this.machineCapacityLock) {

                        order.setNoOfItemsRemaining(order.getNoOfItemsRemaining() - 1);
                        Machine.this.currentFoodCount--;

                        System.out.println("Machine " + Machine.this + " current food count is " + Machine.this.currentFoodCount);
                        Simulation.logEvent(SimulationEvent.machineDoneFood(Machine.this, food, order.getOrderNum()));
                        Machine.this.machineCapacityLock.notifyAll();
                        order.notifyAll();

                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


//            try {
//
//                synchronized (this) {
//
//                    Thread.sleep(sleepTime);
//                    itemCooked = true;
//
//                    synchronized (Machine.this.currentFoodCount) {
//                        Machine.this.currentFoodCount--;
//
//                    }
//                    this.notify();
//
//                }
//
//
//            } catch (InterruptedException e) {
//
//
//            }
        }
    }


    public String toString() {
        return machineName;
    }
}