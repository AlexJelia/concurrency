package course.concurrency.m3_shared.auction;

import java.util.concurrent.locks.ReentrantLock;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private Notifier notifier;
    private ReentrantLock lock = new ReentrantLock();
    private volatile boolean stopped = false;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = new Bid(0L, 0L, 0L);

    public boolean propose(Bid bid) {
        if (bid.getPrice() > latestBid.getPrice() && !stopped) {
            try {
                lock.lock();
                if (bid.getPrice() > latestBid.getPrice() && !stopped) {
                    notifier.sendOutdatedMessage(latestBid);
                    latestBid = bid;
                    return true;
                }
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }

    public Bid stopAuction() {
        try {
            lock.lock();
            stopped = true;
            return latestBid;
        } finally {
            lock.unlock();
        }
    }
}
