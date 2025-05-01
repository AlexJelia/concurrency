package course.concurrency.m3_shared.auction;

import java.util.concurrent.locks.ReentrantLock;

public class AuctionPessimistic implements Auction {

    private Notifier notifier;
    private ReentrantLock lock = new ReentrantLock();

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    /*
        Чем меньше и быстрее критическая секция - тем выше пропускная способность кода.
        Если проверка на null находится внутри synchronized блока или в зоне оптимистичной блокировки,
        то имеет смысл выставить для поля значение по умолчанию и убрать эту проверку. Так конкурентного кода станет меньше,
        и пропускная способность метода увеличится.
     */
    private volatile Bid latestBid = new Bid(0L, 0L, 0L);

    /*
         Double-check
         В критический блок попадают только те потоки, которые гипотетически могут обновить ставку.
         Так мы значительно сокращаем конкуренцию за критическую секцию. Время выполнения теста снижается в 1.5 - 2 раза.
         Проверка нужна, потому что в результате гонок latestBid может обновиться, и возможен потерянный апдейт
     */
    public boolean propose(Bid bid) {
        if (bid.getPrice() > latestBid.getPrice()) {
            try {
                lock.lock();
                if (bid.getPrice() > latestBid.getPrice()) {
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
}
