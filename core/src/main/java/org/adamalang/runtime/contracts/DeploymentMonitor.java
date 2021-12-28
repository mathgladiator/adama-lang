package org.adamalang.runtime.contracts;

import org.adamalang.common.ErrorCodeException;

/** Monitor the progress of a deployment */
public interface DeploymentMonitor {
    /** a document was touch and then either changed to a different version or not */
    public void bumpDocument(boolean changed);

    /** while deploying, an exception happened; oh no! */
    public void witnessException(ErrorCodeException ex);
}
