package com.gatehillsoftware.tools.simplehealthcheck.model;

import com.gatehillsoftware.tools.simplehealthcheck.model.input.Endpoint;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class CheckResult {
    private boolean success;
    private String message;
    private Endpoint endpoint;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CheckResult success() {
        this.success = true;
        return this;
    }

    public CheckResult fail(String message) {
        this.success = false;
        this.message = message;
        return this;
    }

    public CheckResult fail(Throwable cause) {
        return fail(String.format("Exception: %s", cause.getLocalizedMessage()));
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public static CheckResult of(Endpoint target) {
        final CheckResult result = new CheckResult();
        result.endpoint = target;
        return result;
    }
}
