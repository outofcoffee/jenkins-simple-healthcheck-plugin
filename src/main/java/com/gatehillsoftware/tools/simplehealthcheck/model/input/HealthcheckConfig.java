package com.gatehillsoftware.tools.simplehealthcheck.model.input;

import java.util.List;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class HealthcheckConfig {
    private List<Endpoint> targets;

    public List<Endpoint> getTargets() {
        return targets;
    }

    public void setTargets(List<Endpoint> targets) {
        this.targets = targets;
    }
}
