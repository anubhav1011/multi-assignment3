package hw3;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Order {

    private List<Food> items;
    private final Priority priority;
    private OrderStatus orderStatus;

    private int noOfItemsRemaining;
    private Map<String, Long> foodCount;


    private final int orderNum;

    public enum OrderStatus {PLACED, INPROGRESS, DONE}

    public enum Priority {
        ONE(1),
        TWO(2),
        THREE(3);


        private int priority;

        public int getPriorityValue() {
            return priority;
        }

        Priority(int priority) {

            this.priority = priority;
        }

        public static Priority getValue(int priority) {

            return Arrays.stream(Priority.values())
                    .filter(x -> x.getPriorityValue() == priority)
                    .findAny().orElse(null);


//            return Arrays.asList(Priority.values()).stream()
//                    .filter(x -> x.getPriority() == priority)
//                    .findAny().orElse(null);
        }


    }


    public Order(List<Food> items, Priority priority, int orderNum) {

        this.items = items;
        this.priority = priority;
        this.orderStatus = null;
        this.orderNum = orderNum;
        this.noOfItemsRemaining = items.size();

        this.foodCount = items.stream().collect(
                Collectors.groupingBy(Food::getName, Collectors.counting())
        );

    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public Priority getPriority() {
        return priority;
    }

    public List<Food> getItems() {
        return items;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public int getNoOfItemsRemaining() {
        return noOfItemsRemaining;
    }

    public void setNoOfItemsRemaining(int noOfItemsRemaining) {
        this.noOfItemsRemaining = noOfItemsRemaining;
    }

    public Map<String, Long> getFoodCount() {
        return foodCount;
    }

    @Override
    public String toString() {
        return String.valueOf(orderNum);
    }
}
