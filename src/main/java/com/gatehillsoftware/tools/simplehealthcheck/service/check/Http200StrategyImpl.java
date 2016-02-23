package com.gatehillsoftware.tools.simplehealthcheck.service.check;

import com.gatehillsoftware.tools.simplehealthcheck.SimpleHealthcheckBuilder;
import com.gatehillsoftware.tools.simplehealthcheck.model.CheckResult;
import com.gatehillsoftware.tools.simplehealthcheck.model.input.Endpoint;
import hudson.FilePath;
import hudson.model.TaskListener;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Send an HTTP GET to the given URL, expecting an HTTP 200 response.
 *
 * @author pete
 */
public class Http200StrategyImpl implements CheckStrategy {
    @Override
    public CheckResult check(SimpleHealthcheckBuilder.DescriptorImpl descriptor, FilePath workspace,
                             TaskListener listener, Endpoint target) {

        listener.getLogger().println(String.format("Checking %s with timeout of %s ms...",
                target.getName(), descriptor.getTimeout()));

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(target.getUrl()).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(descriptor.getTimeout());
            connection.setReadTimeout(descriptor.getTimeout());
            connection.connect();

            listener.getLogger().println(String.format("HTTP %s status code returned by %s",
                    connection.getResponseCode(), target.getUrl()));

            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                return CheckResult.of(target).success();
            } else {
                throw new RuntimeException(String.format("Unexpected HTTP status code %s returned by %s",
                        connection.getResponseCode(), target.getUrl()));
            }

        } catch (Exception e) {
            listener.getLogger().println(e.getLocalizedMessage());
            return CheckResult.of(target).fail(e);

        } finally {
            if (null != connection) {
                connection.disconnect();
            }
        }
    }
}
