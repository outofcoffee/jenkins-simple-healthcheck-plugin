package com.gatehillsoftware.tools.simplehealthcheck.model.output;

import javax.xml.bind.annotation.*;

/**
 * @author pete
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class TestCase {
    @XmlAttribute(name = "classname")
    private String className;

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "status")
    private String status;

    @XmlElement(name = "failure")
    private TestCaseFailure failure;

    @XmlElement(name = "system-out")
    private String systemOut;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TestCaseFailure getFailure() {
        return failure;
    }

    public void setFailure(TestCaseFailure failure) {
        this.failure = failure;
    }

    public String getSystemOut() {
        return systemOut;
    }

    public void setSystemOut(String systemOut) {
        this.systemOut = systemOut;
    }
}
