package org.adamalang.api.commands.contracts;

import org.adamalang.runtime.contracts.DataService;

/** the given command supports lookup of a data service */
public interface CommandRequiresDataService {
  public void onDataServiceFound(DataService service);
}
