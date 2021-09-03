package tutorial.rsocket;

import io.rsocket.AbstractRSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import tutorial.rsocket.game.GameController;
import tutorial.rsocket.publisher.DataPublisher;

public class Server {

    private Disposable server;

    private final DataPublisher dataPublisher;
    private final GameController gameController;

    public Server() {
        this.dataPublisher = new DataPublisher();
        this.gameController = new GameController("Server Player");

        this.server = RSocketFactory.receive()
                .acceptor((setupPayload, reactiveSocket) -> Mono.just(new RSocketImpl(dataPublisher, gameController)))
                .transport(TcpServerTransport.create("localhost", 7777))
                .start()
                .doOnNext(x -> System.out.println("Server started"))
                .subscribe();

    }

    public void dispose() {
        this.server.dispose();
    }

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server();

        while (true) {
//            Thread.sleep(1000);
        }
    }
}
