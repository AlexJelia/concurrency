package course.concurrency.m3_shared.auction;

import java.util.concurrent.locks.ReentrantLock;

public class AuctionPessimistic implements Auction {

    private Notifier notifier;
    private ReentrantLock lock = new ReentrantLock();

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid;

    public boolean propose(Bid bid) {
        try {
            lock.lock();
            if (latestBid == null) {
                this.latestBid = bid;
                return true;
            }
            if (bid.getPrice() > latestBid.getPrice()) {
                notifier.sendOutdatedMessage(latestBid);
                latestBid = bid;
                return true;
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
