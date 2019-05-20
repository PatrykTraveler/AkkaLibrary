package operations;

import akka.actor.ActorRef;
import utils.Book;

import java.util.Optional;

public class FindResult extends Result {
    public Book book;
    public FindResult(ActorRef replyTo, Book book){
        super(replyTo);
        this.book = book;
    }
}
