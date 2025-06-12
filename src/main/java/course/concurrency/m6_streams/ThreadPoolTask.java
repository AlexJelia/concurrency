package course.concurrency.m6_streams;

import java.util.Queue;
import java.util.concurrent.*;

public class ThreadPoolTask {

    // Task #1
    // Делаем так, ибо Executor вызывает take() который под капотом takeFirst() - поэтому переопределяем
    private class ReversedBlockingQueue<E> extends LinkedBlockingDeque<E> {
        @Override
        public E take() throws InterruptedException {
            return super.takeLast();
        }
    }

    public ThreadPoolExecutor getLifoExecutor() {
        return new ThreadPoolExecutor(1, 1,
                0, TimeUnit.MILLISECONDS,
                new ReversedBlockingQueue<>());
    }

    // Task #2
    // ArrayBlockingQueue(8) не подойдет тк 8 задач сразу пойдут на исполнение и еще 8 встанут в очередь
    // А необходимо сразу отклонять
    public ThreadPoolExecutor getRejectExecutor() {
        return new ThreadPoolExecutor(
                8, 8, 0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(), new ThreadPoolExecutor.DiscardPolicy()
        );
    }


}
