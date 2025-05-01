package course.concurrency.m3_shared.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;
    private final AtomicReference<Bid> latestBidRef;

    public AuctionOptimistic(Notifier notifier) {
        latestBidRef = new AtomicReference<>(new Bid(-1l, -1l, -1l));
        this.notifier = notifier;
    }

    public boolean propose(Bid bid) {
        Bid currentBid;
        do {
            currentBid = latestBidRef.get();
            if (bid.getPrice() <= currentBid.getPrice()) {
                return false;
            }
        } while (!latestBidRef.compareAndSet(currentBid, bid));
        notifier.sendOutdatedMessage(currentBid);
        return true;
    }

    public Bid getLatestBid() {
        return latestBidRef.get();
    }
}
