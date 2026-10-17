package org.glotaran.timpcontroller.runtime;

import org.openide.util.NbPreferences;

/** Which R Glotaran uses, stored in the preference {@value #KEY}. */
public enum RMode {

    /** R installed and started by Glotaran ({@link RRuntime}, {@link RserveProcess}). */
    MANAGED,
    /** An Rserve the user starts on localhost:6311, as in Glotaran 1.6. */
    OWN_R;

    static final String KEY = "rruntime.mode";

    /** @return the stored mode, or {@code null} if the user has not chosen yet */
    public static RMode get() {
        String value = NbPreferences.forModule(RRuntime.class).get(KEY, null);
        try {
            return value == null ? null : valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void set(RMode mode) {
        NbPreferences.forModule(RRuntime.class).put(KEY, mode.name());
    }
}
