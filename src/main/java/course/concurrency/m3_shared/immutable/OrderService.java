package course.concurrency.m3_shared.immutable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class OrderService {

    private final ConcurrentHashMap<Long, Order> currentOrders = new ConcurrentHashMap<>();

    public long createOrder(List<Item> items) {
        Order order = new Order(items);
        currentOrders.put(order.getId(), order);
        return order.getId();
    }

    public void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
        Order paid = currentOrders.compute(orderId, (key, o) -> o.withPaymentInfo(paymentInfo));
        if (paid.checkStatus()) {
            deliver(paid);
        }
    }

    public void setPacked(long orderId) {
        Order packed = currentOrders.compute(orderId, (key, o) -> o.packed());
        if (packed.checkStatus()) {
            deliver(packed);
        }
    }

    private void deliver(Order order) {
        /* ... */
        currentOrders.compute(order.getId(), (key, o) -> o.withStatus(Order.Status.DELIVERED));

    }

    public boolean isDelivered(long orderId) {
        return currentOrders.get(orderId).getStatus().equals(Order.Status.DELIVERED);
    }
}
