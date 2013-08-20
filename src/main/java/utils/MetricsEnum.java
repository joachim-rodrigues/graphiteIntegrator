package utils;

/**
 *
 * @author joachimrodrigues
 */
public enum MetricsEnum {

    BUILD_DURATION("Build duration"),
    TOTAL_TESTS("Total tests"),
    FAIL_TESTS("Fail tests"),
    SKIPED_TESTS("Skiped tests"),
    COBERTURA_TOTAL_LINE_COVERAGE("Cobertura total line coverage"),
    COBERTURA_TOTAL_BRANCH_COVERAGE("Cobertura total branch coverage");
    // for next release
//    COBERTURA_PACKAGE_LINE_COVERAGE("Cobertura package line coverage"),
//    COBERTURA_PACKAGE_BRANCH_COVERAGE("Cobertura package branch coverage");
    
    private String name = "";

    //Constructeur
    MetricsEnum(String name) {
        this.name = name;
    }

    @Override
	public String toString() {
        return name;
    }
}
