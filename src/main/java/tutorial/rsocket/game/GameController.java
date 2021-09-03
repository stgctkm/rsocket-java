package tutorial.rsocket.game;

import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.MessageFormat;
import java.util.List;


public class GameController implements Publisher<Payload> {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private final String playerName;
    private final List<Long> shots;
    private Subscriber<? super Payload> subscriber;
    private boolean truce = false;

    int SHOT_COUNT = 10;

    public GameController(String playerName) {
        this.playerName = playerName;
        this.shots = generateShotList();
    }

    /**
     * Create a random list of time intervals, 0-1000ms
     *
     * @return List of time intervals
     */
    private List<Long> generateShotList() {
        return Flux.range(1, SHOT_COUNT)
                .map(x -> (long) Math.ceil(Math.random() * 1000))
                .collectList()
                .block();
    }

    @Override
    public void subscribe(Subscriber<? super Payload> subscriber) {
        this.subscriber = subscriber;
        fireAtWill();
    }

    private void fireAtWill() {
        new Thread(() -> {
            for (Long shotDelay : shots) {
                try {
                    Thread.sleep(shotDelay);
                } catch (Exception e) {

                }

                if (truce) {
                    break;
                }
                logger.info("{}: bang!", playerName);
                System.out.println(MessageFormat.format("{0}: bang!", playerName));
                subscriber.onNext(DefaultPayload.create("bang!"));
            }
            if (!truce) {
                logger.info("{}: I give up!", playerName);
                System.out.println(MessageFormat.format("{0}: I give up!", playerName));
                subscriber.onNext(DefaultPayload.create("I give up."));
            }
            subscriber.onComplete();
        }).start();

    }

    /**
     * Process events from the opponent
     *
     * @param payload Payload received from the rSocket
     */
    public void processPayload(Payload payload) {
        String message = payload.getDataUtf8();
        switch (message) {
            case "bang!":
                String result = Math.random() < 0.5 ? "Haha missed!" : "Ow!";
                logger.info("{}: {}", playerName, result);
                System.out.println(MessageFormat.format("{0}: {1}", playerName, result));
                break;
            case "I give up":
                truce = true;
                System.out.println(MessageFormat.format("{0}: OK, truce", playerName));
                break;
        }
    }


}
