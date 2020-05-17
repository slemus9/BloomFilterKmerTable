package customRxFuncions;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.ObservableOperator;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.observers.DisposableObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BufferUntil<A> implements ObservableOperator<List<A>, A> {

    final Function<A, Boolean> predicate;

    public BufferUntil(Function<A, Boolean> predicate) {
        this.predicate = predicate;
    }


    @Override
    public @NonNull Observer<? super A> apply(@NonNull Observer<? super List<A>> observer) throws Throwable {
        return new BufferUntilObserver(observer);
    }

    private class BufferUntilObserver extends DisposableObserver<A> {

        final Observer<? super List<A>> current;
        List<A> buffer = new ArrayList<>();

        private BufferUntilObserver(Observer<? super List<A>> current) {
            this.current = current;
        }

        @Override
        public void onNext(@NonNull A a) {
            buffer.add(a);
            if (predicate.apply(a)) {
                current.onNext(buffer);
                buffer = new ArrayList<>();
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            buffer = new ArrayList<>();
            current.onError(e);
        }

        @Override
        public void onComplete() {
            List<A> xs = buffer;
            buffer = new ArrayList<>();
            if (!xs.isEmpty()) {
                current.onNext(xs);
            }
            current.onComplete();
        }
    }
}

