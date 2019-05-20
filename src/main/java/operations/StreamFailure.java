package operations;

import akka.actor.ActorRef;

public class StreamFailure extends Result {
    public StreamFailure(ActorRef replyTo){
        super(replyTo);
    }
}
