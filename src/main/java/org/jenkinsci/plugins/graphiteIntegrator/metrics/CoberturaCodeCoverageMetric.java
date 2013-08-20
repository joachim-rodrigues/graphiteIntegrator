/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.jenkinsci.plugins.graphiteIntegrator.metrics;

import hudson.model.AbstractBuild;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.sourceforge.cobertura.check.PackageCoverage;
import net.sourceforge.cobertura.coveragedata.ClassData;
import net.sourceforge.cobertura.coveragedata.CoverageDataFileHandler;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import org.jenkinsci.plugins.graphiteIntegrator.loggers.GraphiteLogger;
import org.jenkinsci.plugins.graphiteIntegrator.Metric;
import org.jenkinsci.plugins.graphiteIntegrator.Server;
import utils.MetricsEnum;

/**
 * 
 * @author joachimrodrigues
 */
public class CoberturaCodeCoverageMetric extends AbstractMetric {

	/**
	 * 
	 * @param build
	 * @param logger
	 * @param graphiteLogger
	 */
	public CoberturaCodeCoverageMetric(AbstractBuild<?, ?> build, PrintStream logger, GraphiteLogger graphiteLogger) {
		super(build, logger, graphiteLogger);
	}

	@Override
	public void sendMetric(Server server, Metric... metrics) throws UnknownHostException, IOException {

		File dataFile = new File(build.getWorkspace() + "/target/cobertura/cobertura.ser");

		ProjectData projectData = CoverageDataFileHandler.loadCoverageData(dataFile);

		if (projectData == null) {
			logger.print("Error: Unable to read from data file " + dataFile.getAbsolutePath());
		}

		double totalLines = 0;
		double totalLinesCovered = 0;
		double totalBranches = 0;
		double totalBranchesCovered = 0;

		Iterator<?> iter = projectData.getClasses().iterator();
		while (iter.hasNext()) {
			ClassData classData = (ClassData) iter.next();

			totalBranches += classData.getNumberOfValidBranches();
			totalBranchesCovered += classData.getNumberOfCoveredBranches();

			totalLines += classData.getNumberOfValidLines();
			totalLinesCovered += classData.getNumberOfCoveredLines();
			
			// for next release : 
			// PackageCoverage packageCoverage = getPackageCoverage(classData.getPackageName());
			// packageCoverage.addBranchCount(classData.getNumberOfValidBranches());
			// packageCoverage.addBranchCoverage(classData.getNumberOfCoveredBranches());
			//
			// packageCoverage.addLineCount(classData.getNumberOfValidLines());
			// packageCoverage.addLineCoverage(classData.getNumberOfCoveredLines());
			//
			// + percentage(classData.getLineCoverageRate()) + "%, branch coverage rate: "
			// + percentage(classData.getBranchCoverageRate()) + "%");

		}

		for (int i = 0; i < metrics.length; i++) {
			if (metrics[i].getName().equals(MetricsEnum.COBERTURA_TOTAL_LINE_COVERAGE.name())) {
				sendMetric(server, metrics[i], percentage(totalLinesCovered / totalLines));
			}
			if (metrics[i].getName().equals(MetricsEnum.COBERTURA_TOTAL_BRANCH_COVERAGE.name())) {
				sendMetric(server, metrics[i], percentage(totalBranchesCovered / totalBranches));
			}
		}

	}

	Map<String, PackageCoverage> packageCoverageMap = new HashMap();

	/**
	 * @param packageName
	 * @return
	 */
	private PackageCoverage getPackageCoverage(String packageName) {
		PackageCoverage packageCoverage = packageCoverageMap.get(packageName);
		if (packageCoverage == null) {
			packageCoverage = new PackageCoverage();
			packageCoverageMap.put(packageName, packageCoverage);
		}
		return packageCoverage;
	}

	/**
	 * @param coverateRate
	 * @return
	 */
	private String percentage(double coverateRate) {
		BigDecimal decimal = new BigDecimal(coverateRate * 100);
		return decimal.setScale(1, BigDecimal.ROUND_DOWN).toString();
	}

}
