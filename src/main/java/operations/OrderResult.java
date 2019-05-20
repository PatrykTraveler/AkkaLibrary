package operations;

import akka.actor.ActorRef;

public class OrderResult extends Result{
    public boolean success;
    public OrderResult(ActorRef replyTo, boolean success){
        super(replyTo);
        this.success = success;
    }
}
