package com.modeln.build.ant.test;

import com.modeln.build.ant.test.ReportTest;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * This is a test suite which performs unit tests to verify that the 
 * ant tasks are functioning correctly.
 *
 * @author Shawn Stafford (sstafford@modeln.com)
 */
public class AntTestSuite {


    /**
     * Construct and return the suite of tests to be run.
     * 
     * @return Test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("Test");
        suite.addTest(new TestSuite(ReportTest.class));
        
        return suite;
    }

    
}

