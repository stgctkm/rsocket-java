package tutorial.rsocket.client;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;

public class RequestResponseClient {

    private final RSocket socket;

    public RequestResponseClient() {
        this.socket = RSocketFactory.connect()
                .transport(TcpClientTransport.create("localhost", 7777))
                .start()
                .block();
    }

    public String callBlocking(String value) {
        return socket
                .requestResponse(DefaultPayload.create(value))
                .map(Payload::getDataUtf8)
                .block();
    }

    public void dispose() {
        this.socket.dispose();
    }
}
