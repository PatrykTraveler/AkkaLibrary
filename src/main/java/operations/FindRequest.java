package operations;

import akka.actor.ActorRef;

public class FindRequest extends Request{
    public String title;

    public FindRequest(String title){
        super(null);
        this.title = title;
    }

    public FindRequest(ActorRef replyTo, String title){
        super(replyTo);
        this.title = title;
    }
}
