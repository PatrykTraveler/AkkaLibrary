package actors;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import operations.FindRequest;
import operations.FindResult;
import scala.concurrent.duration.Duration;
import utils.State;

import java.io.IOException;


public class FindActor extends AbstractActor {
    public ActorRef dbActor1, dbActor2;

    public static Props getProps(){
        return Props.create(FindActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiver(getSender(), State.EMPTY);
    }

    public Receive receiver(ActorRef sender, State state) {
        return receiveBuilder()
                .match(FindRequest.class, r -> {
                    dbActor1.tell(r, getSelf());
                    dbActor2.tell(r, getSelf());

                    getContext().become(receiver(getSender(), State.EMPTY));
                })
                .match(FindResult.class, r -> {
                    if(r.book == null && state == State.EMPTY){
                        getContext().become(receiver(sender, State.ONE_RESPONSE));
                    }
                    else if(r.book == null && state == State.ONE_RESPONSE){
                        sender.tell(r, getSelf());
                        getContext().stop(getSelf());
                    }
                    else if(r.book != null && (state == State.EMPTY || state == State.ONE_RESPONSE)){
                        sender.tell(r, getSelf());
                        getContext().become(receiver(sender, State.ALREADY_SEND));
                    }
                    else if(r.book != null && state == State.ALREADY_SEND){
                        getContext().stop(getSelf());
                    }
                })
                .matchAny(m -> System.out.println("Unknown message " + m))
                .build();

    }

    @Override
    public void preStart() throws Exception {
        dbActor1 = getContext().actorOf(SearchActor.getProps("db1"), "dbActor1");
        dbActor2 = getContext().actorOf(SearchActor.getProps("db2"), "dbActor2");
    }

    private static SupervisorStrategy strategy =
            new OneForOneStrategy(
                    10,
                    Duration.create("1 minute"),
                    DeciderBuilder
                            .match(IOException.class, e -> SupervisorStrategy.stop())
                            .matchAny(e -> SupervisorStrategy.escalate())
                            .build()
            );

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }
}
