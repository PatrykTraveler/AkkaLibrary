package operations;

import akka.actor.ActorRef;

import java.io.Serializable;

public abstract class Request implements Serializable {
    public ActorRef replyTo;
    public Request(ActorRef replyTo){
        this.replyTo = replyTo;
    }
}
