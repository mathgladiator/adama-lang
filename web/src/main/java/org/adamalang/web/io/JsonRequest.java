package org.adamalang.web.io;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.ErrorCodes;

/** a thin wrapper for easy access to a JSON request */
public class JsonRequest {
    private final ObjectNode node;

    public JsonRequest(ObjectNode node) {
        this.node = node;
    }

    public int id() throws ErrorCodeException {
        return getInteger("id", true, ErrorCodes.USERLAND_REQUEST_NO_ID_PROPERTY);
    }

    public String method() throws ErrorCodeException {
        return getString("method", true, ErrorCodes.USERLAND_REQUEST_NO_METHOD_PROPERTY);
    }

    public String getString(String field, boolean mustExist, int errorIfDoesnt) throws ErrorCodeException {
        final var fieldNode = node.get(field);
        if (fieldNode == null || fieldNode.isNull() || !(fieldNode.isTextual() || fieldNode.isNumber())) {
            if (mustExist) { throw new ErrorCodeException(errorIfDoesnt); }
            return null;
        }
        if (fieldNode.isNumber()) {
            return fieldNode.numberValue().toString();
        }
        return fieldNode.textValue();
    }

    public Integer getInteger(String field, boolean mustExist, int errorIfDoesnt) throws ErrorCodeException {
        final var fieldNode = node.get(field);
        if (fieldNode == null || fieldNode.isNull() || !(fieldNode.isNumber() && fieldNode.isIntegralNumber() || fieldNode.isTextual())) {
            if (mustExist) {
                throw new ErrorCodeException(errorIfDoesnt);
            } else {
                return null;
            }
        }
        if (fieldNode.isTextual()) {
            try {
                return Integer.parseInt(fieldNode.textValue());
            } catch (NumberFormatException nfe) {
                throw new ErrorCodeException(errorIfDoesnt);
            }
        }
        return fieldNode.intValue();
    }

    public Long getLong(String field, boolean mustExist, int errorIfDoesnt) throws ErrorCodeException {
        final var fieldNode = node.get(field);
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

    public ObjectNode getObject(String field, boolean mustExist, int errorIfDoesnt) throws ErrorCodeException {
        final var fieldNode = node.get(field);
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
