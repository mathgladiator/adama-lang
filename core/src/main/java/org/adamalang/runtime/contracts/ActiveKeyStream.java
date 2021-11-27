package org.adamalang.runtime.contracts;

/** When a process starts, Adama needs to pull from the data store all keys which may have a temporal state machine */
public interface ActiveKeyStream {

    /** the data store is informing Adama of a key to load up after some time */
    public void schedule(Key key, long time);

    /** the data store has finished feeding Adama */
    public void finish();
}
