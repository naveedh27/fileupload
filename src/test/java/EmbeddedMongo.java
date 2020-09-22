import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

import java.io.IOException;

public class EmbeddedMongo {

    public static MongodExecutable startEmbeddedMongo(int port) throws IOException {
        IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                .net(new Net("127.0.0.1", port, Network.localhostIsIPv6()))
                .build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        MongodExecutable mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();
        return mongodExecutable;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        startEmbeddedMongo(27017);
        while (true)
            Thread.sleep(1000);
    }


}
