package operations;

import akka.actor.ActorRef;

public class StreamResult extends Result {
    public String line;
    public StreamResult(ActorRef replyTo, String line){
        super(replyTo);
        this.line = line;
    }
}
