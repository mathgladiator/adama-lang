/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.deploy;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.json.JsonStreamReader;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/** parses a deployment plan and constructs a safe plan which has yet to be compiled */
public class DeploymentPlan {
    public final HashMap<String, String> versions;
    public final ArrayList<Stage> stages;
    public final String defaultVersion;

    public static class Stage {
        public final String version;
        public final String prefix;
        public final String seed;
        public final double percent;

        public Stage(String version, String prefix, String seed, double percent) {
            this.version = version;
            this.prefix = prefix;
            this.seed = seed;
            this.percent = percent;
        }
    }

    public DeploymentPlan(String json, ExceptionLogger logger) throws ErrorCodeException {
        try {
            JsonStreamReader reader = new JsonStreamReader(json);
            versions = new HashMap<>();
            stages = new ArrayList<>();
            String _defaultVersion = null;
            if (reader.startObject()) {
                while (reader.notEndOfObject()) {
                    switch (reader.fieldName()) {
                        case "versions": {
                            if (reader.startObject()) {
                                while (reader.notEndOfObject()) {
                                    String version = reader.fieldName();
                                    String code = reader.readString();
                                    versions.put(version, code);
                                }
                            } else {
                                throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_PLAN_VERSIONS_MUST_BE_OBJECT);
                            }
                        }
                        break;
                        case "default": {
                            _defaultVersion = reader.readString();
                        }
                        break;
                        case "plan": {
                            if (reader.startArray()) {
                                while (reader.notEndOfArray()) {
                                    if (reader.startObject()) {
                                        String _seed = "";
                                        String _prefix = "";
                                        double _percent = 1.0;
                                        String _version = null;
                                        while (reader.notEndOfObject()) {
                                            switch (reader.fieldName()) {
                                                case "version":
                                                    _version = reader.readString();
                                                    if (!versions.containsKey(_version)) {
                                                        throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_PLAN_VERSION_MUST_EXIST);
                                                    }
                                                    break;
                                                case "prefix":
                                                    _prefix = reader.readString();
                                                    break;
                                                case "seed":
                                                    _seed = reader.readString();
                                                    break;
                                                case "percent":
                                                    try {
                                                        _percent = reader.readDouble();
                                                    } catch (NumberFormatException nfe) {
                                                        throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_PLAN_PERCENT_MUST_BE_DOUBLE);
                                                    }
                                                    break;
                                                default:
                                                    throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_UNKNOWN_FIELD_STAGE);
                                            }
                                        }
                                        if (_version == null) {
                                            throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_PLAN_PLAN_NO_VERSION);
                                        }
                                        stages.add(new Stage(_version, _prefix, _seed, _percent));
                                    } else {
                                        throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_PLAN_PLAN_ARRAY_ELEMENT_MUST_OBJECT);
                                    }
                                }
                            } else {
                                throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_PLAN_PLAN_MUST_BE_ARRAY);
                            }
                        }
                        break;
                        default:
                            throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_UNKNOWN_FIELD_ROOT);
                    }
                }
                if (versions.size() == 0) {
                    throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_PLAN_NO_VERSIONS);
                }
                if (_defaultVersion == null) {
                    throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_PLAN_NO_DEFAULT);
                }
                if (!versions.containsKey(_defaultVersion)) {
                    throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_PLAN_MUST_HAVE_DEFAULT);
                }
                this.defaultVersion = _defaultVersion;
            } else {
                throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_PLAN_MUST_BE_ROOT_OBJECT);
            }

        } catch (Exception ex) {
            throw ErrorCodeException.detectOrWrap(ErrorCodes.DEPLOYMENT_UNKNOWN_EXCEPTION, ex, logger);
        }
    }

    public static double hash(String seed, String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(seed.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(key.getBytes(StandardCharsets.UTF_8));
            return (Math.abs(Arrays.hashCode(bytes)) % 1000000) / 10000.0;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String pickVersion(String key) {
        for (Stage stage : stages) {
            if (key.startsWith(stage.prefix)) {
                double check = hash(stage.seed, key);
                if (check <= stage.percent) {
                    return stage.version;
                }
            }
        }
        return defaultVersion;
    }
}
