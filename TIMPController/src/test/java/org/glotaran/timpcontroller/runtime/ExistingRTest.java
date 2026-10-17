package org.glotaran.timpcontroller.runtime;

import org.junit.Test;
import static org.junit.Assert.*;

public class ExistingRTest {

    @Test
    public void readsInstallPathFromRegQuery() {
        String output = "\r\nHKEY_LOCAL_MACHINE\\Software\\R-core\\R\r\n    InstallPath    REG_SZ    C:\\Program Files\\R\\R-4.6.1\r\n\r\n";
        assertEquals("C:\\Program Files\\R\\R-4.6.1", ExistingR.parseInstallPath(output));
    }

    @Test
    public void noInstallPathWithoutValue() {
        assertNull(ExistingR.parseInstallPath("ERROR: The system was unable to find the specified registry key or value.\r\n"));
        assertNull(ExistingR.parseInstallPath("\r\nHKEY_LOCAL_MACHINE\\Software\\R-core\\R\r\n    Current Version    REG_SZ    4.6.1\r\n"));
    }
}
