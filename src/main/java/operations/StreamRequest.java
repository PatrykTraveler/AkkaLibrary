package operations;

import akka.actor.ActorRef;

public class StreamRequest extends Request {
    public String title;

    public StreamRequest(String title){
        super(null);
        this.title = title;
    }

    public StreamRequest(ActorRef replyTo, String title){
        super(replyTo);
        this.title = title;
    }
}
