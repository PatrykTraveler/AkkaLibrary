package operations;

import akka.actor.ActorRef;

public class OrderRequest extends Request {
    public String title;

    public OrderRequest(String title){
        super(null);
        this.title = title;
    }

    public OrderRequest(ActorRef replyTo, String title){
        super(replyTo);
        this.title = title;
    }
}
