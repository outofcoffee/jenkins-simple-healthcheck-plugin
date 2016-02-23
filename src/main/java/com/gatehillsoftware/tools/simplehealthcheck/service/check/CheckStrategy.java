package com.gatehillsoftware.tools.simplehealthcheck.service.check;

import com.gatehillsoftware.tools.simplehealthcheck.SimpleHealthcheckBuilder;
import com.gatehillsoftware.tools.simplehealthcheck.model.CheckResult;
import com.gatehillsoftware.tools.simplehealthcheck.model.input.Endpoint;
import hudson.FilePath;
import hudson.model.TaskListener;

/**
 * @author pete
 */
public interface CheckStrategy {
    CheckResult check(SimpleHealthcheckBuilder.DescriptorImpl descriptor,
                      FilePath workspace, TaskListener listener, Endpoint target);

}
