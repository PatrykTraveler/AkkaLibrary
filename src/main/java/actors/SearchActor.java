package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import operations.FindRequest;
import operations.FindResult;
import utils.Book;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

public class SearchActor extends AbstractActor {
    private String database;

    public static Props getProps(String db){
        return Props.create(SearchActor.class, db);
    }

    public SearchActor(String database){
        this.database = database;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(FindRequest.class, r -> {
                    getSender().tell(getResult(r), getSelf());
                    getContext().stop(getSelf());
                })
                .matchAny(m -> System.out.println("Unknown message " + m))
                .build();
    }

    public FindResult getResult(FindRequest findRequest) throws IOException {
        String path = "Databases\\" + database + "\\" + "titles.txt";
        Stream<String> fileStream = Files.lines(Paths.get(path));

        Optional <String> book = fileStream
                .dropWhile(s -> !s.trim().split(":")[0].equals(findRequest.title))
                .findFirst();

        if(book.isPresent()){
            System.out.println(book.get());
            String line = book.get();
            String[] params = line.trim().split(":");
            return new FindResult(findRequest.replyTo, new Book(params[0], Double.parseDouble(params[1]), database));
        }
        return new FindResult(findRequest.replyTo, null);
    }
}
