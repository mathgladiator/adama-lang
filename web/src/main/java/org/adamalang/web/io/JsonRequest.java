package org.adamalang.web.io;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.web.service.ErrorCodes;

public class JsonRequest {
    private final ObjectNode node;

    public JsonRequest(ObjectNode node) {
        this.node = node;
    }

    public int id() throws ErrorCodeException {
        return (int) ((long) lng(node, "id", true, ErrorCodes.USERLAND_REQUEST_NO_ID_PROPERTY));
    }

    public String method() throws ErrorCodeException {
        return str(node, "method", true, ErrorCodes.USERLAND_REQUEST_NO_METHOD_PROPERTY);
    }


    /*
    public int stream() throws ErrorCodeException {
        return (int) lng(node, "stream", ErrorCodes.USERLAND_REQUEST_NO_STREAM_PROPERTY);
    }


    public String space() throws ErrorCodeException {
        return str(node, "space", true, ErrorCodes.USERLAND_REQUEST_NO_GAMESPACE_PROPERTY);
    }

    public String marker() throws ErrorCodeException {
        return str(node, "marker", true, ErrorCodes.USERLAND_REQUEST_NO_MARKER_PROPERTY);
    }

    public String channel() throws ErrorCodeException {
        return str(node, "channel", true, ErrorCodes.USERLAND_REQUEST_NO_CHANNEL_PROPERTY);
    }

    public String entropy() throws ErrorCodeException {
        return str(node, "entropy", false, 0);
    }

    public String key() throws ErrorCodeException {
        return str(node, "key", true, ErrorCodes.USERLAND_REQUEST_NO_GAME_PROPERTY);
    }

    public String json_message() throws ErrorCodeException {
        return node(node, "message", ErrorCodes.USERLAND_REQUEST_NO_MESSAGE_PROPERTY).toString();
    }

    public String json_arg() throws ErrorCodeException {
        return node(node, "arg", ErrorCodes.USERLAND_REQUEST_NO_CONSTRUCTOR_ARG).toString();
    }
    */

    public String getString(String field, boolean mustExist, int errorIfDoesnt) throws ErrorCodeException {
        return str(node, field, mustExist, errorIfDoesnt);
    }

    public Integer getInteger(String field, boolean mustExist, int errorIfDoesnt) throws ErrorCodeException {
        return (int) (long) lng(node, field, mustExist, errorIfDoesnt);
    }

    public Long getLong(String field, boolean mustExist, int errorIfDoesnt) throws ErrorCodeException {
        return lng(node, field, mustExist, errorIfDoesnt);
    }


    public JsonNode getObject(String field, boolean mustExist, int errorIfDoesnt) throws ErrorCodeException {
        return node(node, field, mustExist, errorIfDoesnt);
    }

    private String str(final ObjectNode request, final String field, final boolean mustExist, final int errorIfDoesnt) throws ErrorCodeException {
        final var fieldNode = request.get(field);
        if (fieldNode == null || fieldNode.isNull() || !(fieldNode.isTextual() || fieldNode.isNumber())) {
            if (mustExist) { throw new ErrorCodeException(errorIfDoesnt); }
            return null;
        }
        if (fieldNode.isNumber()) {
            return fieldNode.numberValue().toString();
        }
        return fieldNode.textValue();
    }

    private static Long lng(final ObjectNode request, final String field, boolean mustExist, final int errorIfDoesnt) throws ErrorCodeException {
        final var fieldNode = request.get(field);
        if (fieldNode == null || fieldNode.isNull() || !(fieldNode.isNumber() && fieldNode.isIntegralNumber() || fieldNode.isTextual())) {
            if (mustExist) {
                throw new ErrorCodeException(errorIfDoesnt);
            } else {
                return null;
            }
        }
        if (fieldNode.isTextual()) {
            try {
                return Long.parseLong(fieldNode.textValue());
            } catch (NumberFormatException nfe) {
                throw new ErrorCodeException(errorIfDoesnt);
            }
        }
        return fieldNode.longValue();
    }

    private static ObjectNode node(final ObjectNode request, final String field, boolean mustExist, final int errorIfDoesnt) throws ErrorCodeException {
        final var fieldNode = request.get(field);
        if (fieldNode == null || fieldNode.isNull() || !fieldNode.isObject()) {
            if (mustExist) {
                throw new ErrorCodeException(errorIfDoesnt);
            } else {
                return null;
            }
        }
        return (ObjectNode) fieldNode;
    }


}
