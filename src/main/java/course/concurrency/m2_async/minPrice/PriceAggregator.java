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
        List<CompletableFuture<Double>> tasks = shopIds.stream()
                .map(
                        shopId ->
                                CompletableFuture.supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), executor)
                                        .exceptionally(ex -> {
                                            System.out.println("Error occurred: " + ex.getMessage());
                                            return Double.NaN;
                                        })
                                        .completeOnTimeout(Double.NaN, 2950, TimeUnit.MILLISECONDS)
                )
                .toList();
        return tasks.stream()
                .mapToDouble(CompletableFuture::join)
                .filter(d -> !Double.isNaN(d))
                .min()
                .orElse(Double.NaN);
    }
}
