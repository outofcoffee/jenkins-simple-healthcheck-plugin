package com.gatehillsoftware.tools.simplehealthcheck.service.check;

import com.gatehillsoftware.tools.simplehealthcheck.SimpleHealthcheckBuilder;
import com.gatehillsoftware.tools.simplehealthcheck.model.CheckResult;
import com.gatehillsoftware.tools.simplehealthcheck.model.input.Endpoint;
import hudson.FilePath;
import hudson.model.TaskListener;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

/**
 * Connect to a socket specified by the given URL, expecting a successful connection.
 *
 * @author pete
 */
public class SocketStrategyImpl implements CheckStrategy {
    @Override
    public CheckResult check(SimpleHealthcheckBuilder.DescriptorImpl descriptor, FilePath workspace,
                             TaskListener listener, Endpoint target) {

        listener.getLogger().println(String.format("Checking %s with timeout of %s ms...",
                target.getName(), descriptor.getTimeout()));

        try (Socket socket = new Socket()) {
            final URL url = new URL(target.getUrl());
            final int port = getPort(url);

            socket.setSoTimeout(descriptor.getTimeout());
            socket.connect(new InetSocketAddress(url.getHost(), port));

            listener.getLogger().println(String.format("Connected to socket %s:%s",
                    url.getHost(), port));

            return CheckResult.of(target).success();

        } catch (Exception e) {
            listener.getLogger().println(e.getLocalizedMessage());
            return CheckResult.of(target).fail(e);
        }
    }

    private int getPort(URL url) {
        return (url.getPort() == -1 ? ("https".equals(url.getProtocol()) ? 443 : 80) : url.getPort());
    }
}
