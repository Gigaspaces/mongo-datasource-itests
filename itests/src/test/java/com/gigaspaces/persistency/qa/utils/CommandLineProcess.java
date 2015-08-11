package com.gigaspaces.persistency.qa.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.springframework.util.StringUtils;

import com.j_spaces.jms.utils.StringsUtils;

public class CommandLineProcess implements Runnable {

	private List<String> command;
	private int exitValue;
	Process process;
	private Map<String, String> env = new HashMap<String, String>();
	private String workingDir;
    public String waitForString = null;
    private Semaphore lock;

    public CommandLineProcess(List<String> cmd, String workingDir,String waitingForString, Semaphore sem) {

        if (cmd == null || cmd.isEmpty())
            throw new IllegalArgumentException("cmd");

        this.command = cmd;
        this.workingDir = workingDir;
        this.waitForString = waitingForString;
        this.lock = sem;
    }

	public CommandLineProcess(List<String> cmd, String workingDir) {

		if (cmd == null || cmd.isEmpty())
			throw new IllegalArgumentException("cmd");

		this.command = cmd;
		this.workingDir = workingDir;
	}

	public void addEnvironmentVariable(String key, String value) {
		env.put(key, value);
	}

	public void run() {
        if (waitForString == null)
		    execute(command);
        else {
            execute(command,waitForString);
        }
		this.exitValue = process.exitValue();
	}

    private void execute(List<String> command2, String endWaitingString) {
        try {
            ProcessBuilder builder = new ProcessBuilder(command2);

            if (env.size() > 0)
                builder.environment().putAll(env);

            if (StringUtils.hasLength(workingDir))
                builder.directory(new File(workingDir));

            builder.redirectErrorStream(true);
            process = builder.start();

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));

            String line;

            while ((line = stdInput.readLine()) != null) {
                if (endWaitingString != null && line.contains(endWaitingString)){
                    lock.release();
                }
                System.out.println(line);
            }

            process.waitFor();

        } catch (IOException e) {
            throw new AssertionError(e);
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }
    }

	private void execute(List<String> command2) {
		execute(command2,null);
	}

	public void stop() {
		if (process != null)
			process.destroy();
	}

	public int getExitValue() {
		return exitValue;
	}
}
