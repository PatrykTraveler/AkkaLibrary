package actors;

import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.pattern.Patterns;
import akka.remote.WireFormats;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import akka.util.Timeout;
import operations.*;
import akka.remote.WireFormats.*;
import scala.concurrent.duration.FiniteDuration;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;
import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamActor extends AbstractActor {
    public static Props getProps(){
        return Props.create(StreamActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiver(getSender());
    }

    public Receive receiver(ActorRef sender){
        return receiveBuilder()
                .match(StreamRequest.class, r -> {
                    handleFind(new FindRequest(r.replyTo, r.title));
                    getContext().become(receiver(getSender()));
                })
                .match(FindResult.class, r -> {
                    if(r.book != null) {
                        final Materializer mat = ActorMaterializer.create(getContext());

                        String path = "Databases\\" + r.book.db + "\\" + r.book.title + ".txt";
                        Stream<String> fileStream = Files.lines(Paths.get(path));

                        Source<StreamResult, NotUsed> lines = Source.from(fileStream
                                .map(e -> new StreamResult(r.replyTo, e))
                                .collect(Collectors.toList()));

                        lines.throttle(1, Duration.ofSeconds(1))
                                .watchTermination((ignore, termination) -> {
                                    termination.whenComplete((done, throwable) -> {
                                        getContext().stop(getSelf());
                                        System.out.println("Stream completed");
                                    });
                                    return NotUsed.getInstance();
                                })
                                .runWith(Sink.actorRef(sender, new StreamEnd()), mat);
                    }
                    else{
                        sender.tell(new StreamFailure(r.replyTo), getSelf());
                        getContext().stop(getSelf());
                    }
                })
                .build();
    }

    public void handleFind(FindRequest findRequest){
        ActorRef find = getContext().actorOf(FindActor.getProps());
        find.tell(findRequest, getSelf());
    }
}
