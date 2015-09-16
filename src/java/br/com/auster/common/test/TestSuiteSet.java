package br.com.auster.common.test;

import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * Set of TestSuites
 * 
 * @author TTI Technologies
 */
public class TestSuiteSet {

    public static Test suite() {
        TestSuite ts = new TestSuite();
        ts.addTestSuite(TestCryptor.class);
        return ts;
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
        //junit.swingui.TestRunner.run(suite());
    }
}

 

