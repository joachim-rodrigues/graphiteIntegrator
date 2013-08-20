/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.jenkinsci.plugins.graphiteIntegrator.metrics;

import hudson.model.AbstractBuild;
import java.io.IOException;
import java.io.PrintStream;
import java.net.UnknownHostException;
import org.jenkinsci.plugins.graphiteIntegrator.loggers.GraphiteLogger;
import org.jenkinsci.plugins.graphiteIntegrator.Metric;
import org.jenkinsci.plugins.graphiteIntegrator.Server;

/**
 * 
 * @author joachimrodrigues
 */
public class FailTestsMetric extends AbstractMetric {

	/**
	 * 
	 * @param build
	 * @param logger
	 * @param graphiteLogger
	 */
	public FailTestsMetric(AbstractBuild<?, ?> build, PrintStream logger, GraphiteLogger graphiteLogger) {
		super(build, logger, graphiteLogger);
	}

	/**
	 * 
	 * @param server
	 * @param metric
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	@Override
	public void sendMetric(Server server, Metric... metric) throws UnknownHostException, IOException {

		String metricToSend = Integer.toString(build.getTestResultAction().getFailCount());

		sendMetric(server, metric[0], metricToSend);
	}
}
