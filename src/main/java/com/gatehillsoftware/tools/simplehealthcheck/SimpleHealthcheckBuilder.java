package com.gatehillsoftware.tools.simplehealthcheck;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gatehillsoftware.tools.simplehealthcheck.model.CheckResult;
import com.gatehillsoftware.tools.simplehealthcheck.model.input.Endpoint;
import com.gatehillsoftware.tools.simplehealthcheck.model.input.HealthcheckConfig;
import com.gatehillsoftware.tools.simplehealthcheck.service.HealthcheckService;
import com.gatehillsoftware.tools.simplehealthcheck.service.ServiceFactory;
import com.google.common.collect.Lists;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Simple healthcheck {@link Builder}.
 * <p/>
 * <p/>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link SimpleHealthcheckBuilder} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #configFile})
 * to remember the configuration.
 * <p/>
 * <p/>
 * When a build is performed, the {@link #perform} method will be invoked.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class SimpleHealthcheckBuilder extends Builder implements SimpleBuildStep {

    private final String configFile;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public SimpleHealthcheckBuilder(String configFile) {
        this.configFile = configFile;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getConfigFile() {
        return configFile;
    }

    @Override
    public void perform(Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener) throws AbortException {
        // This is where you 'build' the project.

        listener.getLogger().println(String.format(
                "Reading healthcheck configuration from: %s", configFile));

        try {
            final FilePath healthcheckConfigFile = new FilePath(workspace, configFile);
            if (!healthcheckConfigFile.exists()) {
                throw new FileNotFoundException(String.format(
                        "Configuration file '%s' not found in project workspace", configFile));
            }

            final HealthcheckConfig config = new ObjectMapper()
                    .readValue(healthcheckConfigFile.readToString(), HealthcheckConfig.class);

            listener.getLogger().println(String.format("Loaded %s configuration(s)", config.getTargets().size()));

            // This also shows how you can consult the global configuration of the builder
            final DescriptorImpl descriptor = getDescriptor();

            final HealthcheckService healthcheckService = ServiceFactory.getInstance(HealthcheckService.class);

            final List<CheckResult> results = Lists.newArrayList();
            for (Endpoint target : config.getTargets()) {
                final CheckResult result = healthcheckService.check(descriptor, workspace, listener, target);
                results.add(result);
            }

            healthcheckService.writeReport(descriptor, workspace, listener, results);

        } catch (Exception e) {
            listener.getLogger().println(e);
            throw new AbortException(e.getLocalizedMessage());
        }
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link SimpleHealthcheckBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     * <p/>
     * <p/>
     * See <tt>src/main/resources/hudson/plugins/hello_world/SimpleHealthcheckBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         * <p/>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         * <p/>
         * Timeout in milliseconds.
         */
        private int timeout = 10000;

        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Performs on-the-fly validation of the form field 'configFile'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p/>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        public FormValidation doCheckConfigFile(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.error("Please set a configuration file");
            } else if (!value.endsWith(".json")) {
                return FormValidation.warning("Configuration file usually has .json extension");
            }
            return FormValidation.ok();
        }

        /**
         * Performs on-the-fly validation of the form field 'timeout'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p/>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        public FormValidation doCheckTimeout(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.error("Please set a timeout value");
            } else {
                try {
                    //noinspection ResultOfMethodCallIgnored
                    Integer.parseInt(value);
                    return FormValidation.ok();

                } catch (Exception ignored) {
                    return FormValidation.error("Timeout value must be an integer");
                }
            }
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Perform healthcheck";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            timeout = formData.getInt("timeout");
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req, formData);
        }

        /**
         * This method returns the timeout value from the global configuration.
         * <p/>
         * The method name is bit awkward because global.jelly calls this method to determine
         * the initial state of the checkbox by the naming convention.
         */
        public int getTimeout() {
            return timeout;
        }
    }
}

