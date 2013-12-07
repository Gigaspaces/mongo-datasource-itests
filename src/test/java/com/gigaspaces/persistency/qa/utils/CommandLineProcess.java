package com.gigaspaces.persistency.qa.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.j_spaces.jms.utils.StringsUtils;

public class CommandLineProcess implements Runnable {

	private List<String> command;
	private int exitValue;
	Process process;
	private Map<String, String> env = new HashMap<String, String>();
	private String workingDir;

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
		execute(command);

		this.exitValue = process.exitValue();
	}

	private void execute(List<String> command2) {
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
				System.out.println(line);
			}

			process.waitFor();

		} catch (IOException e) {
			throw new AssertionError(e);
		} catch (InterruptedException e) {
			throw new AssertionError(e);
		}
	}

	public void stop() {
		if (process != null)
			process.destroy();
	}

	public int getExitValue() {
		return exitValue;
	}
}
