package org.adamalang.saas;

import org.adamalang.extern.Email;
import org.adamalang.extern.ExternNexus;
import org.adamalang.grpc.common.MachineIdentity;
import org.adamalang.grpc.server.Server;
import org.adamalang.mysql.Base;
import org.adamalang.mysql.BaseConfig;
import org.adamalang.mysql.backend.BlockingDataService;
import org.adamalang.mysql.backend.DataServiceInstaller;
import org.adamalang.mysql.frontend.ManagementInstaller;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.threads.ThreadedDataService;
import org.adamalang.web.io.Json;

import java.io.File;
import java.nio.file.Files;

public class SaaS {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("requires args");
            System.exit(1);
        }

        String config = "{}";
        MachineIdentity identity = null;
        for (int k = 1; k + 1 < args.length; k++) {
            if ("--config".equals(args[k])) {
                config = Files.readString(new File(args[k+1]).toPath());
                Json.parseJsonObject(config);
            }
            if ("--identity".equals(args[k])) {
                identity = MachineIdentity.fromFile(args[k+1]);
            }
        }

        if ("frontend".equals(args[0])) {
            Base base = new Base(new BaseConfig(config));
            new ManagementInstaller(base).install();
            ExternNexus nexus = new ExternNexus(new Email() {
                @Override
                public void sendCode(String email, String code) {
                    System.err.println("send code:" + code + " to " + email);
                }
            }, base);
            Frontend.execute(nexus, config);
        }

        if ("backend".equals(args[0])) {
            Base base = new Base(new BaseConfig(config));
            new DataServiceInstaller(base).install();
            ThreadedDataService ds = new ThreadedDataService(32, () -> new BlockingDataService(base));
            DeploymentFactoryBase deploymentFactoryBase = new DeploymentFactoryBase();
            CoreService service = new CoreService(deploymentFactoryBase, ds, TimeSource.REAL_TIME, 4);
            Server server = new Server(identity, service, 20000);
            server.start();
        }
    }
}
