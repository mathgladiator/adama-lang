/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.cli.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.common.Json;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.*;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Fleet {
  public static void execute(Config config, String[] args) throws Exception {
    if (args.length == 0) {
      fleetsHelp(args);
      return;
    }
    String command = Util.normalize(args[0]);
    String[] next = Util.tail(args);
    switch (command) {
      case "configure":
        fleetConfigure(config, next);
        return;
      case "show":
        fleetShow(config, next);
        return;
      case "deploy":
        fleetDeploy(config, next);
        return;
      case "restart":
      case "help":
        fleetsHelp(next);
        return;
    }
  }

  private static Ec2Client getEC2(Config config) {
    String accessKeyId = config.get_string("aws_ec2_access_key_id", null);
    String secretAccessKey = config.get_string("aws_ec2_secret_access_key", null);
    String region = config.get_string("aws_ec2_region", null);
    if (accessKeyId == null || secretAccessKey == null || region == null) {
      throw new NullPointerException("not configured for fleet management");
    }

    AwsCredentialsProvider provider = new AwsCredentialsProvider() {
      @Override
      public AwsCredentials resolveCredentials() {
        return new AwsCredentials() {
          @Override
          public String accessKeyId() {
            return accessKeyId;
          }

          @Override
          public String secretAccessKey() {
            return secretAccessKey;
          }
        };
      }
    };

    return Ec2Client.builder().credentialsProvider(provider).region(Region.of(region)).build();
  }


  private static ElasticLoadBalancingV2Client getELB(Config config) {
    String accessKeyId = config.get_string("aws_ec2_access_key_id", null);
    String secretAccessKey = config.get_string("aws_ec2_secret_access_key", null);
    String region = config.get_string("aws_ec2_region", null);
    if (accessKeyId == null || secretAccessKey == null || region == null) {
      throw new NullPointerException("not configured for fleet management");
    }

    AwsCredentialsProvider provider = new AwsCredentialsProvider() {
      @Override
      public AwsCredentials resolveCredentials() {
        return new AwsCredentials() {
          @Override
          public String accessKeyId() {
            return accessKeyId;
          }

          @Override
          public String secretAccessKey() {
            return secretAccessKey;
          }
        };
      }
    };

    return ElasticLoadBalancingV2Client.builder().credentialsProvider(provider).region(Region.of(region)).build();
  }

  private static void fleetConfigure(Config config, String[] args) throws Exception {
    String accessKey = "";
    while ("".equals(accessKey)) {
      System.out.println();
      System.out.print(Util.prefix("AccessKey:", Util.ANSI.Yellow));
      accessKey = System.console().readLine().trim();
    }

    String secretKey = "";
    while ("".equals(secretKey)) {
      System.out.println();
      System.out.print(Util.prefix("SecretKey:", Util.ANSI.Yellow));
      secretKey = new String(System.console().readPassword()).trim();
    }

    String region = "";
    while ("".equals(region)) {
      System.out.println();
      System.out.print(Util.prefix("   Region:", Util.ANSI.Yellow));
      region = System.console().readLine().trim();
    }

    String _accessKey = accessKey;
    String _secretKey = secretKey;
    String _region = region;

    config.manipulate((obj) -> {
      obj.put("aws_ec2_access_key_id", _accessKey);
      obj.put("aws_ec2_secret_access_key", _secretKey);
      obj.put("aws_ec2_region", _region);
    });
  }

  private static void fleetShow(Config config, String[] args) throws Exception {
    Ec2Client ec2 = getEC2(config);
    boolean showNotOK = Util.scan("--all", args);
    DescribeInstancesResponse response = ec2.describeInstances();
    for (Reservation reservation : response.reservations()) {
      for (Instance instance : reservation.instances()) {
        String state = instance.state().nameAsString();
        boolean ok = "running".equals(state) || "pending".equals(state);
        if (ok) {
          String role = "unknown";
          for (Tag tag : instance.tags()) {
            if ("role".equals(tag.key())) {
              role = tag.value();
            }
          }
          System.out.println(instance.instanceId() + "," + state + "," + instance.publicIpAddress() + "," + instance.privateIpAddress() + "," + instance.subnetId() + "," + role);
        } else if (showNotOK) {
          System.out.println(instance.instanceId() + "," + state + "-,-");
        }
      }
    }
  }

  private static String roleOf(Instance instance) {
    String role = "unknown";
    for (Tag tag : instance.tags()) {
      if ("role".equals(tag.key())) {
        role = tag.value();
      }
    }
    return role;
  }

  private static void fleetDeployUpdateELB(Config config, HashSet<String> frontendInstances) {
    ElasticLoadBalancingV2Client elb = getELB(config);
    String loadBalancerArn = config.get_string("loadbalancer-arn", null);
    if (loadBalancerArn == null) {
      throw new NullPointerException("config has no 'loadbalancer-arn'");
    }
    ArrayList<TargetDescription> toRemove = new ArrayList<>();
    ArrayList<TargetDescription> toAdd = new ArrayList<>();
    for (TargetHealthDescription health : elb.describeTargetHealth(DescribeTargetHealthRequest.builder().targetGroupArn(loadBalancerArn).build()).targetHealthDescriptions()) {
      if (frontendInstances.contains(health.target().id())) {
        frontendInstances.remove(health.target().id());
      } else {
        toRemove.add(health.target());
      }
    }
    for (String idToAdd : frontendInstances) {
      toAdd.add(TargetDescription.builder().id(idToAdd).port(8080).build());
    }
    if (toAdd.size() > 0) {
      elb.registerTargets(RegisterTargetsRequest.builder().targetGroupArn(loadBalancerArn).targets(toAdd).build());
      System.err.println("registered " + toAdd.size() + " targets");
    }
    if (toRemove.size() > 0) {
      elb.deregisterTargets(DeregisterTargetsRequest.builder().targetGroupArn(loadBalancerArn).targets(toRemove).build());
      System.err.println("deregistered " + toRemove.size() + " targets");
    }
  }

  public Fleet() {
    super();
  }

  private static void fleetDeploy(Config config, String[] args) throws Exception {
    Ec2Client ec2 = getEC2(config);
    String scopeToJustRole = Util.extractWithDefault("--scope", "-s", "*", args);


    DescribeInstancesResponse response = ec2.describeInstances();
    String templateFile = Util.extractOrCrash("--template", "-t", args);
    String keyName = Util.extractOrCrash("--key", "-k", args);
    String template = Files.readString(new File(templateFile).toPath());
    ObjectNode configTemplate = Json.parseJsonObject(template);
    StringBuilder commands = new StringBuilder().append("#!/bin/sh\n");
    ArrayList<Instance> instances = new ArrayList<>();
    ArrayList<String> gossipHosts = new ArrayList<>();
    for (Reservation reservation : response.reservations()) {
      for (Instance instance : reservation.instances()) {
        String state = instance.state().nameAsString();
        if ("pending".equals(state)) {
          throw new Exception("An instance (" + instance.instanceId() + ") is pending; can't configure");
        }
        if ("running".equals(state)) {
          String role = roleOf(instance);
          if (role.equals("frontend")) {
            gossipHosts.add(instance.privateIpAddress() + ":8004");
          } else if (role.equals("backend")) {
            gossipHosts.add(instance.privateIpAddress() + ":8002");
          } else if (role.equals("overlord")) {
            gossipHosts.add(instance.privateIpAddress() + ":8010");
            System.out.println(Util.prefix("OVERLORD AT ", Util.ANSI.Red) + instance.publicIpAddress());
          }
          if (scopeToJustRole.equals(role) || scopeToJustRole.equals("*")) {
            instances.add(instance);
          }
        }
      }
    }

    ArrayList<String> bootstrap = new ArrayList<>();
    Random rng = new Random();
    while (bootstrap.size() < 3 && gossipHosts.size() > 0) {
      bootstrap.add(gossipHosts.remove(rng.nextInt(gossipHosts.size())));
    }
    bootstrap.sort(String::compareTo);
    ArrayNode bootstrapNodes = configTemplate.putArray("bootstrap");
    for (String endpoint : bootstrap) {
      bootstrapNodes.add(endpoint);
    }

    new File("staging").mkdir();
    HashSet<String> frontendInstances = new HashSet<>();
    for (Instance instance : instances) {
      String role = roleOf(instance);
      configTemplate.put("role", role);
      if ("frontend".equals(role)) {
        frontendInstances.add(instance.instanceId());
      }
      Files.writeString(new File("staging/" + instance.privateIpAddress() + ".json").toPath(), configTemplate.toPrettyString());
      Files.writeString(new File("staging/" + instance.privateIpAddress() + ".sh").toPath(), "#!/bin/sh\nmv adama-new.jar adama.jar\njava -jar adama.jar service auto\n");
      Security.newServer(new String[] { "--ip", instance.privateIpAddress(), "--out", "staging/" + instance.privateIpAddress() + ".identity"});
      commands.append("echo Uploading to ").append(instance.publicIpAddress()).append("\n");
      commands.append("scp -i ").append(keyName).append(" staging/" + instance.privateIpAddress() + ".identity ec2-user@" + instance.publicIpAddress() + ":/home/ec2-user/me.identity\n");
      commands.append("scp -i ").append(keyName).append(" staging/" + instance.privateIpAddress() + ".json ec2-user@" + instance.publicIpAddress() + ":/home/ec2-user/.adama\n");
      commands.append("scp -i ").append(keyName).append(" adama.jar ec2-user@" + instance.publicIpAddress() + ":/home/ec2-user/adama-new.jar\n");
      commands.append("scp -i ").append(keyName).append(" staging/" + instance.privateIpAddress() + ".sh ec2-user@" + instance.publicIpAddress() + ":/home/ec2-user/adama.sh\n");
      commands.append("ssh -i ").append(keyName).append(" ec2-user@" + instance.publicIpAddress() + " chmod 700 /home/ec2-user/adama.sh\n");
      commands.append("rm staging/").append(instance.privateIpAddress()).append(".identity\n");
      commands.append("rm staging/").append(instance.privateIpAddress()).append(".json\n");
      commands.append("rm staging/").append(instance.privateIpAddress()).append(".sh\n");
    }
    commands.append("rmdir staging\n");
    for (Instance instance : instances) {
      commands.append("echo ").append(Util.prefix("RESTART", Util.ANSI.Red)).append(" ").append(instance.publicIpAddress()).append("\n");
      commands.append("ssh -i ").append(keyName).append(" ec2-user@" + instance.publicIpAddress() + " sudo systemctl restart adama\n");
    }

    if (scopeToJustRole.equals("*")) {
      System.err.println("Updating ELB");
      fleetDeployUpdateELB(config, frontendInstances);
    }


    Files.writeString(new File("execute.sh").toPath(), commands.toString());
  }

  public static void fleetsHelp(String[] args) {
    if (args.length > 0) {
      String command = Util.normalize(args[0]);
    }
    System.out.println(Util.prefix("Interact with a fleet via EC2", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama fleet", Util.ANSI.Green) + " " + Util.prefix("[FLEETSUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println();
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("FLEETSUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("configure", Util.ANSI.Green) + "         Configure your CLI");
    System.out.println("    " + Util.prefix("deploy", Util.ANSI.Green) + "            Generate a fleet config and deploy the jar");
    System.out.println("    " + Util.prefix("show", Util.ANSI.Green) + "              Show the current capacity in the given region");
  }
}
