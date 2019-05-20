package actors;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.util.ByteString;
import operations.*;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RemoteActor extends AbstractActor {
    public static Props getProps(){
        return Props.create(RemoteActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiver();
    }

    public Receive receiver(){
        return receiveBuilder()
                .match(FindRequest.class, this::handleFind)
                .match(OrderRequest.class, this::handleOrder)
                .match(StreamRequest.class, this::handleStream)
                .match(Result.class, r -> r.replyTo.tell(r, getSelf()))
                .matchAny(m -> System.out.println("Unknown message " + m))
                .build();
    }

    public void handleFind(FindRequest findRequest){
        ActorRef find = getContext().actorOf(FindActor.getProps());
        find.tell(findRequest, getSelf());
    }

    public void handleOrder(OrderRequest orderRequest){
        ActorRef order = getContext().actorOf(OrderActor.getProps());
        order.tell(orderRequest, getSelf());
    }

    public void handleStream(StreamRequest streamRequest){
        ActorRef stream = getContext().actorOf(StreamActor.getProps());
        stream.tell(streamRequest, getSelf());
    }

    private static SupervisorStrategy strategy =
            new OneForOneStrategy(
                    10,
                    Duration.create("1 minute"),
                    DeciderBuilder
                            .match(IOException.class, e -> {
                                System.out.println("error");
                                return SupervisorStrategy.resume();
                            })
                            .matchAny(e -> SupervisorStrategy.escalate())
                            .build()
            );

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }
}
