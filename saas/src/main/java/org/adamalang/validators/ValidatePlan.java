package org.adamalang.validators;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.contracts.ExceptionLogger;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.exceptions.ErrorCodeException;

public class ValidatePlan {
    public static void validate(ObjectNode node) throws ErrorCodeException {
        new DeploymentPlan(node.toString(), new ExceptionLogger() {
            @Override
            public void convertedToErrorCode(Throwable t, int errorCode) {

            }
        });
        // TODO: validate everything compiles
    }
}
