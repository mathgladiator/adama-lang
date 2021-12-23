package org.adamalang.grpc.common;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class KeyTool {

    private static void pipe(String cmd) throws Exception {
        Process process = Runtime.getRuntime().exec(cmd);
        System.out.println(new String(process.getInputStream().readAllBytes()));
        System.err.println(new String(process.getErrorStream().readAllBytes()));
    }

    public static void main(String[] args) throws Exception {
        String caPath = null;
        String ip = null;
        String output = null;
        for (int k = 0; k + 1 < args.length; k++) {
            if ("--ca".equals(args[k])) {
                caPath = args[k+1];
            }
            if ("--ip".equals(args[k])) {
                ip = args[k+1];
            }
            if ("--out".equals(args[k])) {
                output = args[k+1];
            }
        }
        boolean error = false;
        if (caPath == null) {
            System.err.println("require --ca $path");
            error = true;
        } else if (!new File(caPath).exists() || !new File(caPath).isDirectory()) {
            System.err.println("the path within the '--ca $path' option doesn't exist and must be a directory");
            error = true;
        } else {

        }
        if (ip == null) {
            System.err.println("require --ip $ip");
            error = true;
        }
        if (output == null) {
            System.err.println("require --out $file");
            error = true;
        }
        if (error) {
            return;
        }

        File caCert = new File(new File(caPath), "ca-cert.pem");
        File caKey = new File(new File(caPath), "ca-key.pem");

        Files.copy(caCert.toPath(), new File("/tmp/ca-cert.pem").toPath());
        Files.copy(caKey.toPath(), new File("/tmp/ca-key.pem").toPath());

        // TODO: sort out a more generic subject
        pipe("/usr/bin/openssl req -newkey rsa:4096 -nodes -keyout /tmp/machine-key.pem -out /tmp/machine-req.pem -subj /C=US/ST=Kansas/L=KansasCity/O=Adama/OU=Adama/CN=adama.com/emailAddress=admin@adama.com");
        Files.writeString(new File("/tmp/machine.cnf").toPath(), "subjectAltName=IP:" + ip);
        pipe("/usr/bin/openssl x509 -req -in /tmp/machine-req.pem -days 365 -CA /tmp/ca-cert.pem -CAkey /tmp/ca-key.pem -CAcreateserial -out /tmp/machine-cert.pem -extfile /tmp/machine.cnf");
        String json = MachineIdentity.convertToJson(new File("/tmp/ca-cert.pem"), new File("/tmp/machine-cert.pem"), new File("/tmp/machine-key.pem"));

        new File("/tmp/ca-cert.pem").delete();
        new File("/tmp/ca-key.pem").delete();
        new File("/tmp/machine-req.pem").delete();
        new File("/tmp/machine-cert.pem").delete();
        new File("/tmp/machine-key.pem").delete();
        new File("/tmp/machine.cnf").delete();

        Files.writeString(new File(output).toPath(), json);
    }
}
