package operations;

import akka.actor.ActorRef;

import java.io.Serializable;

public abstract class Result implements Serializable {
    public ActorRef replyTo;
    public Result(ActorRef replyTo){
        this.replyTo = replyTo;
    }
}
