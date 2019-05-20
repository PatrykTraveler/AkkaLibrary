package actors;


import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.util.ByteString;
import operations.*;
import utils.Book;

public class ClientActor extends AbstractActor{
    public final String remoteActorPath = "akka.tcp://remote_system@127.0.0.1:3552/user/remoteActor";
    public ActorSelection actorSelection;

    public static Props getProps(){
        return Props.create(ClientActor.class);
    }

    public ClientActor(){
        actorSelection = getContext().actorSelection(remoteActorPath);
    }

    @Override
    public AbstractActor.Receive createReceive() {
        System.out.println(getSelf());
        return receiveBuilder()
                .match(FindRequest.class, r -> actorSelection.tell(new FindRequest(getSelf(), r.title), getSelf()))
                .match(OrderRequest.class, r -> actorSelection.tell(new OrderRequest(getSelf(), r.title), getSelf()))
                .match(StreamRequest.class, r -> actorSelection.tell(new StreamRequest(getSelf(), r.title), getSelf()))
                .match(FindResult.class, this::handleFindResult)
                .match(OrderResult.class, this::handleOrderResult)
                .match(StreamResult.class, this::handleStreamResult)
                .match(StreamFailure.class, r -> System.out.println("Failed to stream book"))
                .matchAny(m -> System.out.println("Unknown messages " + m))
                .build();
    }

    public void handleFindResult(FindResult findResult){
        Book book = findResult.book;
        if(book != null){ System.out.println(book.title + " costs " + book.price); }
        else{ System.out.println("Book doesn't exist"); }
    }

    private void handleOrderResult(OrderResult orderResult){
        if(orderResult.success){
            System.out.println("Book has been ordered successfully.");
        }
        else{
            System.out.println("Failed to order the book.");
        }
    }

    private void handleStreamResult(StreamResult streamResult){
        if(streamResult.line != null){
            System.out.println(streamResult.line);
        }
        else{
            System.out.println("An unexpected error has occured while book streaming.");
        }
    }
}
