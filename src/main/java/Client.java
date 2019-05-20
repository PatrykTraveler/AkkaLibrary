import actors.ClientActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import operations.FindRequest;
import operations.OrderRequest;
import operations.StreamRequest;

import java.io.File;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Client {
    public final static String configPath = "client.conf";

    public static void main(String[] args){
        File configFile = new File(args[0]);
        Config config = ConfigFactory.parseFile(configFile);

        final ActorSystem system = ActorSystem.create("local_system", config);
        final ActorRef clientActor = system.actorOf(ClientActor.getProps(), "clientActor");

        Consumer<? super String> commandHandler = cmd -> {
            String[] commands = cmd.split("\\s+");
            String command = commands[0];
            String title = commands[1];

            System.out.println(cmd);

            switch(command){
                case "find":
                    clientActor.tell(new FindRequest(title), null);
                    break;
                case "order":
                    clientActor.tell(new OrderRequest(title), null);
                    break;
                case "stream":
                    clientActor.tell(new StreamRequest(title), null);
                    break;
            }

        };

        Scanner console  = new Scanner(System.in);
        Stream.generate(console::nextLine)
                .takeWhile(s -> !s.equals("quit"))
                .filter(s -> s.split("\\s+").length > 1)
                .forEach(commandHandler);
    }
}
