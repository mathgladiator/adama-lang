/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.validators;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.common.ErrorCodeException;

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
