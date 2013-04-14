/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glotaran.timpcontroller;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.openide.modules.ModuleInstall;
import org.rosuda.REngine.Rserve.RConnection;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {

    public static final long serialVersionUID = 1;
    private Future<Integer> exitCode;
    //private String[] theCommandLineArguments = new String[]{"/usr/lib64/R/bin/Rserve", "--no-save"};
    private String[] commands;

    @Override
    public void restored() {
//        if(!isRserveRunning()) {
//            //TODO: check for properties file in user directory for local or remote Rserve settings, if none exists, then attempt to launch a local Rserve installation.
//            commands = RserveProcessHelper.checkLocalRserve();
//        }
//        // Create a Callable returning the actual Process.
//        if (commands!=null) {
//        Callable<Process> processCallable = new ProcessLaunch(commands);
//        // Create a descriptor for the UI representation of the process.
//        ExecutionDescriptor descriptor = new ExecutionDescriptor().controllable(true).frontWindow(true);
//        descriptor.showProgress(true);
//        descriptor.inputVisible(true);
//        // Create an instance of ExecutionService and run the Callable<Process>
//        ExecutionService exeService = ExecutionService.newService(processCallable, descriptor, "Rserve");
//        // Run the process and return the exitcode.
//        exitCode = exeService.run();
//        }
    }

    @Override
    public void close() {
        if (exitCode != null) {
            exitCode.cancel(true);
        }
        super.close();
    }

    private class ProcessLaunch implements Callable<Process> {

        private final String[] commandLine;

        public ProcessLaunch(String... commandLine) {
            this.commandLine = commandLine;
        }

        public Process call() throws Exception {
            ProcessBuilder pb = new ProcessBuilder(commandLine);
            pb.directory(new File(System.getProperty("user.home"))); //NOI18N
            pb.redirectErrorStream(true);
            return pb.start();
        }
    }

    /** check whether Rserve is currently running (on local machine and default port).
    @return <code>true</code> if local Rserve instance is running, <code>false</code> otherwise
     */
    public static boolean isRserveRunning() {
        try {
            RConnection c = new RConnection();
            c.close();
            return true;
        } catch (Exception e) {
            System.out.println("First connect try failed with: " + e.getMessage());
        }
        return false;
    }
}
