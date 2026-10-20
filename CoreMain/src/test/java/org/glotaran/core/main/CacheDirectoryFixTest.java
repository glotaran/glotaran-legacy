package org.glotaran.core.main;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class CacheDirectoryFixTest {

    private static final List<String> CONF_160 = List.of(
            "# ${HOME} will be replaced by user home directory according to platform",
            "default_userdir=\"${HOME}/.${APPNAME}/1.6\"",
            "default_mac_userdir=\"${HOME}/Library/Application Support/${APPNAME}/1.6\"",
            "#jdkhome=\"/path/to/jdk\"");

    private static final String LINES = CacheDirectoryFix.COMMENT + "\ncachedir=\"${DEFAULT_CACHEDIR_ROOT}/1.6\"\n";

    @Test
    public void setsCacheDirectoryForInstallationWithoutDefault() {
        assertEquals(LINES, CacheDirectoryFix.linesToAppend(CONF_160, List.of(), "1.6"));
    }

    @Test
    public void appendsAfterExistingUserSettings() {
        assertEquals("\n" + LINES, CacheDirectoryFix.linesToAppend(CONF_160, List.of("jdkhome=\"/opt/jdk\""), "1.6"));
    }

    @Test
    public void leavesInstallationWithDefaultCacheDirectoryAlone() {
        List<String> conf161 = List.of("default_userdir=\"${HOME}/.${APPNAME}/1.6\"",
                "default_cachedir=\"${DEFAULT_CACHEDIR_ROOT}/1.6\"");
        assertNull(CacheDirectoryFix.linesToAppend(conf161, List.of(), "1.6"));
    }

    @Test
    public void keepsCacheDirectorySetInUserConfiguration() {
        assertNull(CacheDirectoryFix.linesToAppend(CONF_160, List.of("cachedir=\"/data/cache\""), "1.6"));
        assertNull("written once", CacheDirectoryFix.linesToAppend(CONF_160, List.of(LINES.split("\n")), "1.6"));
    }

    @Test
    public void ignoresCommentedOutSettings() {
        List<String> commented = List.of("#default_cachedir=\"/x\"");
        assertEquals("\n" + LINES, CacheDirectoryFix.linesToAppend(commented, List.of("# cachedir=\"/y\""), "1.6"));
    }
}
