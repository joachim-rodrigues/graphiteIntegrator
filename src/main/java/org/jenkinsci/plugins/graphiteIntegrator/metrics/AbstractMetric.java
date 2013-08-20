package org.jenkinsci.plugins.graphiteIntegrator.metrics;

import hudson.model.AbstractBuild;
import java.io.IOException;
import java.io.PrintStream;
import java.net.UnknownHostException;
import org.jenkinsci.plugins.graphiteIntegrator.loggers.GraphiteLogger;
import org.jenkinsci.plugins.graphiteIntegrator.Metric;
import org.jenkinsci.plugins.graphiteIntegrator.Server;
import utils.GraphiteValidator;

/**
 * 
 * @author joachimrodrigues
 */
public abstract class AbstractMetric {

	protected GraphiteValidator validator = new GraphiteValidator();
	protected final AbstractBuild<?, ?> build;
	protected final PrintStream logger;
	protected final GraphiteLogger graphiteLogger;

	/**
	 * 
	 * @param build
	 * @param logger
	 * @param graphiteLogger
	 */
	public AbstractMetric(AbstractBuild<?, ?> build, PrintStream logger, GraphiteLogger graphiteLogger) {
		this.build = build;
		this.logger = logger;
		this.graphiteLogger = graphiteLogger;
	}

	/**
	 * @param server
	 * @param metric
	 * @param metricToSend
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	protected void sendMetric(Server server, Metric metric, String value) throws UnknownHostException,
			IOException {
		logger.println("Trying sending to  server : " + server.getIp() + ":" + server.getPort() + " On queue : "
				+ metric.getQueueName() + " With value : " + value);

		if (validator.isListening(server.getIp(), Integer.parseInt(server.getPort()))) {
			graphiteLogger.logToGraphite(server.getIp(), server.getPort(), metric.getQueueName(), value.trim());

			logger.println("Metric " + value + " correctly sended to " + server.getIp() + ":" + server.getIp()
					+ " on " + metric.getQueueName());

		} else {
			logger.println("Metric " + value + " failed when sended to " + server.getIp() + ":" + server.getIp()
					+ " on " + metric.getQueueName());

		}
	}

	/**
	 * 
	 * @param server
	 * @param metric
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public abstract void sendMetric(Server server, Metric... metric) throws UnknownHostException, IOException;
}
