package course.concurrency.m2_async.minPrice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    private ExecutorService executor = Executors.newCachedThreadPool();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        try {
            List<Callable<Double>> tasks = new ArrayList<>(shopIds.size());
            for (Long shopId : shopIds) {
                tasks.add(() -> priceRetriever.getPrice(itemId, shopId));
            }
            return executor.invokeAll(tasks, 2900, TimeUnit.MILLISECONDS).stream()
                    .map(f -> {
                        try {
                            return f.get();
                        } catch (InterruptedException | ExecutionException ex) {
                            System.out.printf("Thread %s was interrupted while future.get().%n", Thread.currentThread().getName());
                            Thread.currentThread().interrupt();
                            return Double.NaN;
                        } catch (CancellationException e) {
                            return Double.NaN;
                        }
                    })
                    .min(Double::compare)
                    .orElse(Double.NaN);
        } catch (InterruptedException e) {
            System.out.printf("Thread %s was interrupted while invokeAll.%n", Thread.currentThread().getName());
            Thread.currentThread().interrupt();
            return Double.NaN;
        }
    }
}
