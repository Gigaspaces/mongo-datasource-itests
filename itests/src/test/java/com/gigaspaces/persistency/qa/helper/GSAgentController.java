package com.gigaspaces.persistency.qa.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gsa.GridServiceAgent;

import com.gigaspaces.persistency.qa.utils.CommandLineProcess;

public class GSAgentController {

	private static final String GS_HOME = "GS_HOME";
	private static final String QA_GROUP = "qa_group";
	private static final String XAP_LOOKUP_GROUPS = "XAP_LOOKUP_GROUPS";

	private final Admin admin = new AdminFactory().addGroup(QA_GROUP)
			.createAdmin();

	private CommandLineProcess GS_AGENT_PROCESS;

	private Thread thread;
	private String GS_AGENT = (isWin()) ? "gs-agent.bat" : "gs-agent.sh";

	public void start() {

		List<String> args = new ArrayList<String>();

		String wd= FilenameUtils.normalize(System.getenv(GS_HOME)+"/bin");
		args.add(wd + File.separator + GS_AGENT);
		GS_AGENT_PROCESS = new CommandLineProcess(args,wd);

		GS_AGENT_PROCESS.addEnvironmentVariable(XAP_LOOKUP_GROUPS, QA_GROUP);
		GS_AGENT_PROCESS.addEnvironmentVariable("java.rmi.server.hostname", "127.0.0.1");

		thread = new Thread(GS_AGENT_PROCESS);

		thread.start();

		admin.getGridServiceManagers().waitForAtLeastOne();

	}

	private boolean isWin() {
		return (File.separatorChar == '\\');

	}

	public void stop() {
        if (admin != null){
            for (GridServiceAgent gsa : admin.getGridServiceAgents()) {
                gsa.shutdown();
            }
        }
        if (GS_AGENT_PROCESS != null)
		    GS_AGENT_PROCESS.stop();
	}
}
