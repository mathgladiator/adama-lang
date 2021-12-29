/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.CanGetAndSet;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtComplex;

/** a reactive complex number */
public class RxComplex extends RxBase implements CanGetAndSet<NtComplex>  {
    private NtComplex backup;
    private NtComplex value;

    public RxComplex(final RxParent parent, final NtComplex value) {
        super(parent);
        backup = value;
        this.value = value;
    }

    @Override
    public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
        if (__isDirty()) {
            forwardDelta.writeObjectFieldIntro(name);
            forwardDelta.writeNtComplex(value);
            reverseDelta.writeObjectFieldIntro(name);
            reverseDelta.writeNtComplex(backup);
            backup = value;
            __lowerDirtyCommit();
        }
    }

    @Override
    public void __dump(final JsonStreamWriter writer) {
        writer.writeNtComplex(value);
    }

    @Override
    public void __insert(final JsonStreamReader reader) {
        backup = reader.readNtCompex();
        value = backup;
    }

    @Override
    public void __patch(JsonStreamReader reader) {
        set(reader.readNtCompex());
    }

    @Override
    public void __revert() {
        if (__isDirty()) {
            value = backup;
            __lowerDirtyRevert();
        }
    }

    @Override
    public NtComplex get() {
        return value;
    }

    @Override
    public void set(final NtComplex value) {
        this.value = value;
        __raiseDirty();
    }

    @Override
    public long __memory() {
        return super.__memory() + backup.memory() + value.memory() + 16;
    }
}
