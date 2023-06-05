package org.adamalang.cli.implementations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.runtime.Output;
import org.adamalang.cli.router.SpaceHandler;
import org.adamalang.common.Hashing;
import org.adamalang.common.Json;
import org.adamalang.common.Validators;
import org.adamalang.common.keys.PublicPrivateKeyPartnership;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.assets.ContentType;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.BlockingDeque;

public class SpaceHandlerImpl implements SpaceHandler {


    @Override
    public void create(Arguments.SpaceCreateArgs args, Output.YesOrError output) throws Exception {

    }

    @Override
    public void delete(Arguments.SpaceDeleteArgs args, Output.YesOrError output) throws Exception {

    }

    @Override
    public void deploy(Arguments.SpaceDeployArgs args, Output.YesOrError output) throws Exception {

    }

    @Override
    public void setRxhtml(Arguments.SpaceSetRxhtmlArgs args, Output.YesOrError output) throws Exception {

    }

    @Override
    public void getRxhtml(Arguments.SpaceGetRxhtmlArgs args, Output.YesOrError output) throws Exception {

    }

    @Override
    public void upload(Arguments.SpaceUploadArgs args, Output.YesOrError output) throws Exception {

    }

    @Override
    public void download(Arguments.SpaceDownloadArgs args, Output.YesOrError output) throws Exception {

    }

    @Override
    public void list(Arguments.SpaceListArgs args, Output.YesOrError output) throws Exception {

    }

    @Override
    public void usage(Arguments.SpaceUsageArgs args, Output.YesOrError output) throws Exception {

    }

    @Override
    public void reflect(Arguments.SpaceReflectArgs args, Output.YesOrError output) throws Exception {

    }

    @Override
    public void setRole(Arguments.SpaceSetRoleArgs args, Output.YesOrError output) throws Exception {

    }

    @Override
    public void generateKey(Arguments.SpaceGenerateKeyArgs args, Output.YesOrError output) throws Exception {

    }

    @Override
    public void encryptSecret(Arguments.SpaceEncryptSecretArgs args, Output.YesOrError output) throws Exception {

    }
}
