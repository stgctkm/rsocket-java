package tutorial.rsocket.publisher;

import io.rsocket.Payload;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.concurrent.Flow;

public class DataPublisher implements Publisher<Payload> {

    private Subscriber<? super Payload> subscriber;

    @Override
    public void subscribe(Subscriber<? super Payload> subscriber) {
        this.subscriber = subscriber;
    }

    public void publish(Payload payload) {
        if (subscriber != null) {
            subscriber.onNext(payload);
        }
    }

    public void complete() {
        if (subscriber != null) {
            subscriber.onComplete();
        }
    }
}
