package com.gatehillsoftware.tools.simplehealthcheck.service;

import com.gatehillsoftware.tools.simplehealthcheck.SimpleHealthcheckBuilder;
import com.gatehillsoftware.tools.simplehealthcheck.model.CheckResult;
import com.gatehillsoftware.tools.simplehealthcheck.model.input.Endpoint;
import com.gatehillsoftware.tools.simplehealthcheck.model.output.TestCase;
import com.gatehillsoftware.tools.simplehealthcheck.model.output.TestCaseFailure;
import com.gatehillsoftware.tools.simplehealthcheck.model.output.TestSuite;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import hudson.FilePath;
import hudson.model.TaskListener;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class HealthcheckServiceImpl implements HealthcheckService {
    /**
     * Perform the actual healthcheck on the {@code target}.
     *
     * @param descriptor
     * @param workspace
     * @param listener
     * @param target
     * @return
     */
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

    /**
     * Write a test report for the given {@code results}.
     *
     * @param descriptor
     * @param workspace
     * @param listener
     * @param results
     */
    @Override
    public void writeReport(SimpleHealthcheckBuilder.DescriptorImpl descriptor, FilePath workspace,
                            TaskListener listener, List<CheckResult> results) {

        try {
            final TestSuite report = generateXUnitReport(results);

            final FilePath outputDir = new FilePath(workspace, "output");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            final FilePath outputFile = new FilePath(outputDir, "results.xml");

            try (OutputStream os = outputFile.write()) {
                final JAXBContext jaxbContext = JAXBContext.newInstance(TestSuite.class);
                final Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(report, os);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert the results into an xUnit report.
     *
     * @param results
     * @return
     * @throws MalformedURLException
     */
    private TestSuite generateXUnitReport(List<CheckResult> results) throws MalformedURLException {
        final TestSuite testSuite = new TestSuite();
        testSuite.setTests(results.size());

        final List<TestCase> testCases = Lists.newArrayListWithCapacity(results.size());
        for (CheckResult checkResult : results) {
            final TestCase testCase = new TestCase();
            testCase.setName(checkResult.getEndpoint().getName());
            testCase.setClassName(getClassName(checkResult.getEndpoint().getUrl()));

            if (checkResult.isSuccess()) {
                testCase.setSystemOut(checkResult.getMessage());
            } else {
                final TestCaseFailure failure = new TestCaseFailure();
                failure.setMessage(checkResult.getMessage());
                testCase.setFailure(failure);
            }

            testCases.add(testCase);
        }
        testSuite.setTestCases(testCases);

        return testSuite;
    }

    /**
     * Convert the hostname of the endpoint to reverse-syntax domain format,
     * e.g. {@literal http://example.com/foo} becomes {@literal com.example}
     *
     * @param url
     * @return
     * @throws MalformedURLException
     */
    private String getClassName(String url) throws MalformedURLException {
        try {
            final String host = new URL(url).getHost();
            final String[] splitHosts = host.split("\\.");

            return Joiner.on(".").join(
                    Lists.reverse(Lists.newArrayList(splitHosts)).toArray(new String[splitHosts.length]));

        } catch (MalformedURLException e) {
            return "";
        }
    }
}
