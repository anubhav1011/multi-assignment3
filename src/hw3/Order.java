package hw3;

import java.util.List;

public class Order {

    private List<Food> items;
    private int priority;

    public enum OrderStatus {PLACED, DONE}

    ;

    private OrderStatus orderStatus;


    public Order(List<Food> items, int priority) {

        this.items = items;
        this.priority = priority;
        this.orderStatus = null;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<Food> getItems() {
        return items;
    }
}
