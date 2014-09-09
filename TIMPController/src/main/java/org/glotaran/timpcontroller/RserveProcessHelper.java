/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.timpcontroller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author jsg210
 */
public class RserveProcessHelper {

    /** shortcut to <code>launchRserve(cmd, "--no-save --slave", "--no-save --slave", false)</code>
     * @param cmd
     * @return
     */
    public static String[] generateRserveCommand(String cmd) {
        //return generateRserveCommand(cmd, "--no-save --slave", "--no-save --slave", false);
        return generateRserveCommand(cmd, "--no-save", "--no-save", false);
    }

    /** attempt to start Rserve. Note: parameters are <b>not</b> quoted, so avoid using any quotes in arguments
    @param cmd command necessary to start R
    @param rargs arguments are are to be passed to R
    @param rsrvargs arguments to be passed to Rserve
     * @param debug
     * @return <code>true</code> if Rserve is running or was successfully started, <code>false</code> otherwise.
     */
    public static String[] generateRserveCommand(String cmd, String rargs, String rsrvargs, boolean debug) {
        String[] command;
        boolean isWindows = false;
        String osname = System.getProperty("os.name");
        if (osname != null && osname.length() >= 7 && osname.substring(0, 7).equals("Windows")) {
            isWindows = true; /* Windows startup */
            command = new String[]{"\"" + cmd + "\" -e \"library(Rserve);Rserve(" + (debug ? "TRUE" : "FALSE") + ",args='" + rsrvargs + "')\" " + rargs};
        } else {/* unix startup */
            command = new String[]{"/bin/sh", "-c", "echo 'library(Rserve);Rserve(" + (debug ? "TRUE" : "FALSE") + ",args=\"" + rsrvargs + "\")'|" + cmd + " " + rargs};
        }
        return command;
    }

    /** checks whether Rserve is running and if that's not the case it attempts to start it using the defaults for the platform where it is run on. This method is meant to be set-and-forget and cover most default setups. For special setups you may get more control over R with <<code>launchRserve</code> instead.
     * @return
     */
    public static String[] checkLocalRserve() {
        String osname = System.getProperty("os.name");
        if (osname != null && osname.length() >= 7 && osname.substring(0, 7).equals("Windows")) {
            System.out.println("Windows: query registry to find where R is installed ...");
            String installPath = null;
            try {
                Process rp = Runtime.getRuntime().exec("reg query HKLM\\Software\\R-core\\R");
                StreamHog regHog = new StreamHog(rp.getInputStream(), true);
                rp.waitFor();
                regHog.join();
                installPath = regHog.getInstallPath();
            } catch (Exception rge) {
                System.out.println("ERROR: unable to run REG to find the location of R: " + rge);
                return null;
            }
            if (installPath == null) {
                System.out.println("ERROR: canot find path to R. Make sure reg is available and R was installed with registry settings.");
                return null;
            }
            return generateRserveCommand(installPath + "\\bin\\R.exe");
        }
        //(generateRserveCommand("R") || /* try some common unix locations of R */
        if (new File("/Library/Frameworks/R.framework/Resources/bin/R").exists()) {
            return generateRserveCommand("/Library/Frameworks/R.framework/Resources/bin/R");
        } else if (new File("/usr/local/lib/R/bin/R").exists()) {
            return generateRserveCommand("/usr/local/lib/R/bin/R");
        } else if (new File("/usr/lib/R/bin/R").exists()) {
            return generateRserveCommand("/usr/lib/R/bin/R");
        } else if (new File("/usr/local/bin/R").exists()) {
            return generateRserveCommand("/usr/local/bin/R");
        } else if (new File("/sw/bin/R").exists()) {
            return generateRserveCommand("/sw/bin/R");
        } else if (new File("/usr/common/bin/R").exists()) {
            return generateRserveCommand("/usr/common/bin/R");
        } else if (new File("/usr/local/lib64/R/bin/R").exists()) {
            return generateRserveCommand("/usr/local/lib/R/bin/R");
        } else if (new File("/usr/lib64/R/bin/R").exists()) {
            return generateRserveCommand("/usr/lib64/R/bin/R");
        } else {
            return null;
        }
    }
}

/** helper class that consumes output of a process. In addition, it filter output of the REG command on Windows to look for InstallPath registry entry which specifies the location of R. */
class StreamHog extends Thread {

    InputStream is;
    boolean capture;
    String installPath;

    StreamHog(InputStream is, boolean capture) {
        this.is = is;
        this.capture = capture;
        start();
    }

    public String getInstallPath() {
        return installPath;
    }

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (capture) { // we are supposed to capture the output from REG command
                    int i = line.indexOf("InstallPath");
                    if (i >= 0) {
                        String s = line.substring(i + 11).trim();
                        int j = s.indexOf("REG_SZ");
                        if (j >= 0) {
                            s = s.substring(j + 6).trim();
                        }
                        installPath = s;
                        System.out.println("R InstallPath = " + s);
                    }
                } else {
                    System.out.println("Rserve>" + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
