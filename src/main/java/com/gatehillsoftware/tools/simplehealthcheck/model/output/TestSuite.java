package com.gatehillsoftware.tools.simplehealthcheck.model.output;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "testsuite")
public class TestSuite {
    @XmlAttribute(name = "tests")
    private int tests;

    @XmlElement(name = "testcase")
    private List<TestCase> testCases;

    public int getTests() {
        return tests;
    }

    public void setTests(int tests) {
        this.tests = tests;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }
}
