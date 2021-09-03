package tutorial.rsocket.client;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;

public class RequestStreamClient {

    private final RSocket socket;

    String DATA_STREAM_NAME = "data";

    public RequestStreamClient() {
        this.socket = RSocketFactory.connect()
                .transport(TcpClientTransport.create("localhost", 7777))
                .start().block();
    }

    public Flux<Float> getDataStream() {
        return socket
                .requestStream(DefaultPayload.create(DATA_STREAM_NAME))
                .map(Payload::getData)
                .map(ByteBuffer::getFloat)
                .doOnNext(x -> System.out.println(x))
                .onErrorReturn(null);
    }

    public void dispose() {
        this.socket.dispose();
    }
}
