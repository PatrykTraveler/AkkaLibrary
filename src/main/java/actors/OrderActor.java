package actors;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import operations.FindRequest;
import operations.FindResult;
import operations.OrderRequest;
import operations.OrderResult;
import scala.concurrent.duration.Duration;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OrderActor extends AbstractActor {
    public static Props getProps(){
        return Props.create(OrderActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiver(getSender());
    }

    public Receive receiver(ActorRef sender){
        return receiveBuilder()
                .match(OrderRequest.class, r ->{
                    handleFind(new FindRequest(r.replyTo, r.title));
                    getContext().become(receiver(getSender()));
                })
                .match(FindResult.class, r -> {
                    if(r.book == null){
                        sender.tell(new OrderResult(r.replyTo,false), getSelf());
                    }
                    else{
                        writeOrder(r.book.title);
                        sender.tell(new OrderResult(r.replyTo,true), getSelf());
                    }
                })
                .matchAny(m -> System.out.println("Unknown message " + m))
                .build();
    }

    public void writeOrder(String title) throws IOException {
        String path = "Databases\\orders.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
        writer.write(title + "\n");
        writer.close();
    }

    public void handleFind(FindRequest findRequest){
        ActorRef find = getContext().actorOf(FindActor.getProps());
        find.tell(findRequest, getSelf());
    }
}
