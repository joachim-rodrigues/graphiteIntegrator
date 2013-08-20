package org.jenkinsci.plugins.graphiteIntegrator;

import org.jenkinsci.plugins.graphiteIntegrator.loggers.GraphiteLogger;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jenkinsci.plugins.graphiteIntegrator.metrics.AbstractMetric;
import org.jenkinsci.plugins.graphiteIntegrator.metrics.BuildDurationMetric;
import org.jenkinsci.plugins.graphiteIntegrator.metrics.CoberturaCodeCoverageMetric;
import org.jenkinsci.plugins.graphiteIntegrator.metrics.FailTestsMetric;
import org.jenkinsci.plugins.graphiteIntegrator.metrics.SkipTestsMetric;
import org.jenkinsci.plugins.graphiteIntegrator.metrics.TotalTestsMetric;
import utils.MetricsEnum;

/**
 * @author joachimrodrigues
 * 
 */
public class GraphitePublisher extends Notifier {

	/**
     *
     */
	@Extension
	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

	/**
     *
     */
	private String selectedIp;

	/**
     *
     */
	private String selectedMetric;

	/**
     *
     */
	private List<Metric> metrics = new ArrayList<>();

	/**
     *
     */
	public GraphitePublisher() {
	}

	/**
     *
     */
	public GraphitePublisher(String ip, String metric) {
		this.selectedIp = ip;
		this.selectedMetric = metric;

	}

	/**
     *
     */
	public List<Metric> getMetrics() {
		return metrics;
	}

	/**
     *
     */
	public String getSelectedIp() {
		String ipTemp = selectedIp;
		if (ipTemp == null) {
			Server[] servers = DESCRIPTOR.getServers();
			if (servers.length > 0) {
				ipTemp = servers[0].getIp();
			}
		}
		return ipTemp;
	}

	/**
     *
     */
	public String getSelectedMetric() {
		String metricTemp = selectedMetric;
		if (metricTemp == null) {
			Metric[] metrics = DESCRIPTOR.getMetrics();
			if (metrics.length > 0) {
				metricTemp = metrics[0].getName();
			}
		}
		return metricTemp;
	}

	/**
     *
     */
	public void setSelectedIp(String ip) {
		this.selectedIp = ip;
	}

	/**
     *
     */
	public void setSelectedMetric(String metric) {
		this.selectedMetric = metric;
	}

	/**
     *
     */
	public Server getServer() {
		Server[] servers = DESCRIPTOR.getServers();
		if (selectedIp == null && servers.length > 0) {
			return servers[0];
		}
		for (Server server : servers) {
			if (server.getIp().equals(selectedIp)) {
				return server;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hudson.tasks.BuildStepCompatibilityLayer#prebuild(hudson.model.AbstractBuild, hudson.model.BuildListener)
	 */
	@Override
	public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hudson.tasks.Publisher#needsToRunAfterFinalized()
	 */
	@Override
	public boolean needsToRunAfterFinalized() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hudson.tasks.BuildStep#getRequiredMonitorService()
	 */
	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hudson.tasks.Notifier#getDescriptor()
	 */
	@Override
	public BuildStepDescriptor<Publisher> getDescriptor() {
		return DESCRIPTOR;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hudson.tasks.BuildStepCompatibilityLayer#perform(hudson.model.AbstractBuild, hudson.Launcher,
	 * hudson.model.BuildListener)
	 */
	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
			throws InterruptedException, IOException {

		if (build.getResult() == Result.FAILURE || build.getResult() == Result.ABORTED) {
			return true;
		}
		if (getServer() == null) {
			return false;
		}

		listener.getLogger().println("Connecting to " + getServer().getDescription());

		GraphiteLogger graphiteLogger = new GraphiteLogger(listener.getLogger());

		AbstractMetric metricSender = null;

		List<Metric> coberturaMetrics = null;

		for (Metric metric : metrics) {
			if (metric.name.equals(MetricsEnum.BUILD_DURATION.name())) {
				metricSender = new BuildDurationMetric(build, listener.getLogger(), graphiteLogger);
				metricSender.sendMetric(getServer(), metric);
			}
			if (isCoberturaMetric(metric)) {
				if (!isCoberturaListInitialized(coberturaMetrics)) {
					coberturaMetrics = new ArrayList<>();
				}
				coberturaMetrics.add(metric);
			}
			if (metric.name.equals(MetricsEnum.FAIL_TESTS.name())) {
				metricSender = new FailTestsMetric(build, listener.getLogger(), graphiteLogger);
				metricSender.sendMetric(getServer(), metric);
			}
			if (metric.name.equals(MetricsEnum.SKIPED_TESTS.name())) {
				metricSender = new SkipTestsMetric(build, listener.getLogger(), graphiteLogger);
				metricSender.sendMetric(getServer(), metric);
			}
			if (metric.name.equals(MetricsEnum.TOTAL_TESTS.name())) {
				metricSender = new TotalTestsMetric(build, listener.getLogger(), graphiteLogger);
				metricSender.sendMetric(getServer(), metric);
			}
		}
		if (isCoberturaListInitialized(coberturaMetrics)) {
			metricSender = new CoberturaCodeCoverageMetric(build, listener.getLogger(), graphiteLogger);
			metricSender.sendMetric(getServer(), coberturaMetrics.toArray(new Metric[coberturaMetrics.size()]));
		}

		return true;
	}

	/**
	 * @param coberturaMetrics
	 * @return
	 */
	private boolean isCoberturaListInitialized(List<Metric> coberturaMetrics) {
		return coberturaMetrics != null;
	}

	/**
	 * @param metric
	 * @return
	 */
	private boolean isCoberturaMetric(Metric metric) {
		return (// metric.name.equals(MetricsEnum.COBERTURA_PACKAGE_BRANCH_COVERAGE.name())
				// || metric.name.equals(MetricsEnum.COBERTURA_PACKAGE_LINE_COVERAGE.name())
				// ||
		metric.name.equals(MetricsEnum.COBERTURA_TOTAL_BRANCH_COVERAGE.name()) || metric.name
				.equals(MetricsEnum.COBERTURA_TOTAL_LINE_COVERAGE.name())

		);
	}
}
