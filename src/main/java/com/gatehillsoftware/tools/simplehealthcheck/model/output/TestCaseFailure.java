package com.gatehillsoftware.tools.simplehealthcheck.model.output;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author pete
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class TestCaseFailure {
    @XmlAttribute(name = "message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
