package com.gatehillsoftware.tools.simplehealthcheck.service;

import com.gatehillsoftware.tools.simplehealthcheck.SimpleHealthcheckBuilder;
import com.gatehillsoftware.tools.simplehealthcheck.model.CheckResult;
import com.gatehillsoftware.tools.simplehealthcheck.model.input.Endpoint;
import hudson.FilePath;
import hudson.model.TaskListener;

import java.util.List;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public interface HealthcheckService {
    CheckResult check(SimpleHealthcheckBuilder.DescriptorImpl descriptor,
                      FilePath workspace, TaskListener listener, Endpoint target);

    void writeReport(SimpleHealthcheckBuilder.DescriptorImpl descriptor,
                     FilePath workspace, TaskListener listener, List<CheckResult> results);
}
