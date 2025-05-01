package course.concurrency.m3_shared.auction;

import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private Notifier notifier;
    private final AtomicMarkableReference<Bid> latestBidRef;

    public AuctionStoppableOptimistic(Notifier notifier) {
        latestBidRef = new AtomicMarkableReference<>(new Bid(-1l, -1l, -1l), false);
        this.notifier = notifier;
    }

    public boolean propose(Bid bid) {
        Bid currentBid;
        do {
            if (latestBidRef.isMarked()) {
                return false;
            }
            currentBid = latestBidRef.getReference();
            if (bid.getPrice() <= currentBid.getPrice()) {
                return false;
            }
        } while (!latestBidRef.compareAndSet(currentBid, bid, false, false));
        notifier.sendOutdatedMessage(currentBid);
        return true;
    }

    public Bid getLatestBid() {
        return latestBidRef.getReference();
    }

    public Bid stopAuction() {
        Bid latest;
        do {
            latest = latestBidRef.getReference();
        } while (!latestBidRef.attemptMark(latest, true));
        return latest;
    }
}
