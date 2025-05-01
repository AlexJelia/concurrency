package course.concurrency.m3_shared.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private Notifier notifier;
    private final AtomicReference<Bid> latestBidRef;
    private volatile boolean stopped = false;

    public AuctionStoppableOptimistic(Notifier notifier) {
        latestBidRef = new AtomicReference<>(new Bid(-1l, -1l, -1l));
        this.notifier = notifier;
    }

    public boolean propose(Bid bid) {
        if (stopped) return false;
        Bid currentBid;
        do {
            currentBid = latestBidRef.get();
            if (bid.getPrice() <= currentBid.getPrice()) {
                return false;
            }
        } while (!stopped && !latestBidRef.compareAndSet(currentBid, bid));
        notifier.sendOutdatedMessage(currentBid);
        return true;
    }

    public Bid getLatestBid() {
        return latestBidRef.get();
    }

    public Bid stopAuction() {
        stopped = true;
        return latestBidRef.get();
    }
}
