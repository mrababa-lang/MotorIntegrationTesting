package com.insurance.automation.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates an HTML report by replacing placeholders in a template.
 */
public class InsuranceQuoteReportGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(InsuranceQuoteReportGenerator.class);
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final RunInfo runInfo;
    private final String templatePath;
    private final String outputDir;
    private final List<TestResult> testResults = new ArrayList<>();
    private final List<ConfigurationResult> configurationResults = new ArrayList<>();
    private final ObjectMapper objectMapper;

    /**
     * Constructs report generator.
     *
     * @param runInfo metadata for current run.
     * @param templatePath template location.
     * @param outputDir output directory path.
     */
    public InsuranceQuoteReportGenerator(final RunInfo runInfo, final String templatePath, final String outputDir) {
        this.runInfo = runInfo;
        this.templatePath = templatePath;
        this.outputDir = outputDir;
        this.objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Adds one scenario result to the report model.
     *
     * @param result test result.
     */
    public synchronized void addResult(final TestResult result) {
        testResults.add(result);
    }

    /**
     * Adds one configuration card item to the report model.
     *
     * @param result configuration result.
     */
    public synchronized void addConfiguration(final ConfigurationResult result) {
        configurationResults.add(result);
    }

    /**
     * Generates the report file and returns its full output path.
     *
     * @return generated report path, or null when template is missing.
     */
    public synchronized String generate() {
        try {
            final Path template = Paths.get(templatePath);
            if (!Files.exists(template)) {
                LOG.error("Report template not found at {}", template.toAbsolutePath());
                return null;
            }

            final String timestamp = LocalDateTime.now().format(TS_FORMAT);
            final Path outputDirectoryPath = Paths.get(outputDir);
            Files.createDirectories(outputDirectoryPath);
            final String filename = String.format("report_%s_%s_%s.html", runInfo.getEnvironment(), runInfo.getBuildId(), timestamp);
            final Path outputPath = outputDirectoryPath.resolve(filename);

            final String templateHtml = Files.readString(template, StandardCharsets.UTF_8);
            final String content = templateHtml
                .replace("{{RUN_INFO_JSON}}", toJson(runInfo))
                .replace("{{RESULTS_JSON}}", toJson(testResults))
                .replace("{{CONFIGS_JSON}}", toJson(configurationResults));

            Files.writeString(outputPath, content, StandardCharsets.UTF_8);
            LOG.info("Insurance report generated at {}", outputPath.toAbsolutePath());
            return outputPath.toString();
        } catch (IOException exception) {
            LOG.error("Failed to generate insurance quote report", exception);
            return null;
        }
    }

    private String toJson(final Object object) throws IOException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    /**
     * Run metadata model.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RunInfo {
        private String environment;
        private String buildId;
        private String startedAt;
        private String baseUrl;

        public RunInfo(final String environment, final String buildId, final String startedAt, final String baseUrl) {
            this.environment = environment;
            this.buildId = buildId;
            this.startedAt = startedAt;
            this.baseUrl = baseUrl;
        }

        public static RunInfoBuilder builder() {
            return new RunInfoBuilder();
        }

        public String getEnvironment() {
            return environment;
        }

        public String getBuildId() {
            return buildId;
        }

        public static class RunInfoBuilder {
            private String environment;
            private String buildId;
            private String startedAt;
            private String baseUrl;

            public RunInfoBuilder environment(final String environment) {
                this.environment = environment;
                return this;
            }

            public RunInfoBuilder buildId(final String buildId) {
                this.buildId = buildId;
                return this;
            }

            public RunInfoBuilder startedAt(final String startedAt) {
                this.startedAt = startedAt;
                return this;
            }

            public RunInfoBuilder baseUrl(final String baseUrl) {
                this.baseUrl = baseUrl;
                return this;
            }

            public RunInfo build() {
                return new RunInfo(environment, buildId, startedAt, baseUrl);
            }
        }
    }

    /**
     * Test scenario result model.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestResult {
        private String id;
        private String name;
        private String description;
        private String category;
        private String status;
        private Long duration;
        private String input;
        private String expected;
        private String actual;
        private Boolean match;
        private List<SkipRule> skipRules;
        private List<ConfigValue> configValues;
        private String error;

        public TestResult(final String id, final String name, final String description, final String category,
                final String status, final Long duration, final String input, final String expected,
                final String actual, final Boolean match, final List<SkipRule> skipRules,
                final List<ConfigValue> configValues, final String error) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.category = category;
            this.status = status;
            this.duration = duration;
            this.input = input;
            this.expected = expected;
            this.actual = actual;
            this.match = match;
            this.skipRules = skipRules;
            this.configValues = configValues;
            this.error = error;
        }

        public static TestResultBuilder builder() {
            return new TestResultBuilder();
        }

        public static class TestResultBuilder {
            private String id;
            private String name;
            private String description;
            private String category;
            private String status;
            private Long duration;
            private String input;
            private String expected;
            private String actual;
            private Boolean match;
            private List<SkipRule> skipRules;
            private List<ConfigValue> configValues;
            private String error;

            public TestResultBuilder id(final String id) { this.id = id; return this; }
            public TestResultBuilder name(final String name) { this.name = name; return this; }
            public TestResultBuilder description(final String description) { this.description = description; return this; }
            public TestResultBuilder category(final String category) { this.category = category; return this; }
            public TestResultBuilder status(final String status) { this.status = status; return this; }
            public TestResultBuilder duration(final Long duration) { this.duration = duration; return this; }
            public TestResultBuilder input(final String input) { this.input = input; return this; }
            public TestResultBuilder expected(final String expected) { this.expected = expected; return this; }
            public TestResultBuilder actual(final String actual) { this.actual = actual; return this; }
            public TestResultBuilder match(final Boolean match) { this.match = match; return this; }
            public TestResultBuilder skipRules(final List<SkipRule> skipRules) { this.skipRules = skipRules; return this; }
            public TestResultBuilder configValues(final List<ConfigValue> configValues) { this.configValues = configValues; return this; }
            public TestResultBuilder error(final String error) { this.error = error; return this; }

            public TestResult build() {
                return new TestResult(id, name, description, category, status, duration, input, expected, actual, match, skipRules, configValues, error);
            }
        }
    }

    /**
     * Skip rule detail for report rendering.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkipRule {
        private String code;
        private String message;

        public SkipRule(final String code, final String message) {
            this.code = code;
            this.message = message;
        }

        public static SkipRuleBuilder builder() {
            return new SkipRuleBuilder();
        }

        public static class SkipRuleBuilder {
            private String code;
            private String message;

            public SkipRuleBuilder code(final String code) {
                this.code = code;
                return this;
            }

            public SkipRuleBuilder message(final String message) {
                this.message = message;
                return this;
            }

            public SkipRule build() {
                return new SkipRule(code, message);
            }
        }
    }

    /**
     * Configuration value detail for report rendering.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfigValue {
        private String key;
        private String value;

        public ConfigValue(final String key, final String value) {
            this.key = key;
            this.value = value;
        }

        public static ConfigValueBuilder builder() {
            return new ConfigValueBuilder();
        }

        public static class ConfigValueBuilder {
            private String key;
            private String value;

            public ConfigValueBuilder key(final String key) {
                this.key = key;
                return this;
            }

            public ConfigValueBuilder value(final String value) {
                this.value = value;
                return this;
            }

            public ConfigValue build() {
                return new ConfigValue(key, value);
            }
        }
    }

    /**
     * Configuration card model for report rendering.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfigurationResult {
        private String category;
        private String key;
        private String label;
        private Boolean enabled;
        private String value;
        private String lastUpdated;

        public ConfigurationResult(final String category, final String key, final String label,
                final Boolean enabled, final String value, final String lastUpdated) {
            this.category = category;
            this.key = key;
            this.label = label;
            this.enabled = enabled;
            this.value = value;
            this.lastUpdated = lastUpdated;
        }

        public static ConfigurationResultBuilder builder() {
            return new ConfigurationResultBuilder();
        }

        public static class ConfigurationResultBuilder {
            private String category;
            private String key;
            private String label;
            private Boolean enabled;
            private String value;
            private String lastUpdated;

            public ConfigurationResultBuilder category(final String category) { this.category = category; return this; }
            public ConfigurationResultBuilder key(final String key) { this.key = key; return this; }
            public ConfigurationResultBuilder label(final String label) { this.label = label; return this; }
            public ConfigurationResultBuilder enabled(final Boolean enabled) { this.enabled = enabled; return this; }
            public ConfigurationResultBuilder value(final String value) { this.value = value; return this; }
            public ConfigurationResultBuilder lastUpdated(final String lastUpdated) { this.lastUpdated = lastUpdated; return this; }

            public ConfigurationResult build() {
                return new ConfigurationResult(category, key, label, enabled, value, lastUpdated);
            }
        }
    }
}
