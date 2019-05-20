import actors.ClientActor;
import actors.RemoteActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.util.Scanner;
import java.util.stream.Stream;

public class RemoteApp {
    public final static String configPath = "remote.conf";
    public static void main(String[] args) {
        File configFile = new File(configPath);
        Config config = ConfigFactory.parseFile(configFile);

        final ActorSystem system = ActorSystem.create("remote_system", config);
        final ActorRef remoteActor = system.actorOf(RemoteActor.getProps(), "remoteActor");

        Scanner console  = new Scanner(System.in);
        Stream.generate(console::nextLine)
                .takeWhile(s -> !s.equals("quit"))
                .forEach(System.out::println);

    }
}
