package course.concurrency.m3_shared.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private AtomicReference<Bid> latestBidRef = new AtomicReference<>();

    public boolean propose(Bid bid) {
        Bid latestBid = latestBidRef.get();
        if (latestBid == null && latestBidRef.compareAndSet(null, bid)) {
            return true;
        } else {
            latestBid = latestBidRef.get();
        }
        if (bid.getPrice() > latestBid.getPrice()) {
            do {
                latestBid = latestBidRef.get();
                if (latestBid.getPrice() > bid.getPrice())
                    return false;
            } while (!latestBidRef.compareAndSet(latestBid, bid));
            notifier.sendOutdatedMessage(latestBid);
            return true;
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBidRef.get();
    }
}
