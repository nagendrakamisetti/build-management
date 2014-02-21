package com.modeln.build.enums.build;

/**
 * Activity enum.
 * 
 * This enum represents build activity steps.
 * Activities 1 to 7 usually correspond to activities related to step that are done in serial.
 * Activities above 7 are usually activities that correspond to step happening on parallel environment.
 * 
 * TODO
 * - Get rid of this enum and create a configuration instead that would let user create they own activities.
 * 
 * @author gzussa
 *
 */
public enum Activity {
	
	/** Metric type undefined */
    UNDEFINED("undefined",0),

    /** Metric type representing compilation and packaging */
    BUILD("build", 1),

    /** Metric type representing javadoc generation */
    JAVADOC("javadoc", 2),

    /** Metric type representing content population */
    POPULATE("populate", 3),

    /** Metric type representing dynamic content population using unit test suites */
    POPULATESUITE("populatesuite", 4),

    /** Metric type representing unit test execution */
    UNITTEST("unittest", 5),

    /** Metric type representing database migration */
    MIGRATE("migrate", 6),

    /** Metric type representing application deployment */
    DEPLOY("deploy", 7),
    
    DOWNLOAD("download", 8),
    
    PROPCONFIG("propconfig", 9),
    
    ORACLE_IMPORT("oracle.import", 10),
    
    APP_DBPOSTIMPORT("app.dbpostimport", 11),
    
    APP_REMOVE("app.remove", 12),
    
    APP_DEPLOY("app.deploy", 13),
    
    COGNOS_STOP("cognos.stop", 14),
    
    COGNOS_START("cognos.stop", 15),
    
    WEB_REMOVE("cognos.stop", 16),
    
    WEB_DEPLOY("cognos.stop", 17),
    
    COGNOS_DEPLOY("cognos.stop", 18);

    private String name;
	
	private int value;

	private Activity(String name, int value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
