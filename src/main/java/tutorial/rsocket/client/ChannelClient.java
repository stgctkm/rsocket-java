package tutorial.rsocket.client;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import reactor.core.publisher.Flux;
import tutorial.rsocket.game.GameController;

public class ChannelClient {

    RSocket socket;
    GameController gameController = new GameController("Client Player");

    public ChannelClient() {
        this.socket = RSocketFactory.connect()
                .transport(TcpClientTransport.create("localhost", 7777))
                .start()
                .block();
    }

    public void playGame() {
        socket.requestChannel(Flux.from(gameController))
                .doOnNext(gameController::processPayload)
                .blockLast();
    }

    public void dispose() {
        socket.dispose();
    }
}
