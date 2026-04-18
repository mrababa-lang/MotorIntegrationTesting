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
import lombok.Builder;
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
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RunInfo {
        private String environment;
        private String buildId;
        private String startedAt;
        private String baseUrl;

        public String getEnvironment() {
            return environment;
        }

        public String getBuildId() {
            return buildId;
        }
    }

    /**
     * Test scenario result model.
     */
    @Data
    @Builder
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
    }

    /**
     * Skip rule detail for report rendering.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkipRule {
        private String code;
        private String message;
    }

    /**
     * Configuration value detail for report rendering.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfigValue {
        private String key;
        private String value;
    }

    /**
     * Configuration card model for report rendering.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfigurationResult {
        private String category;
        private String key;
        private String label;
        private Boolean enabled;
        private String value;
        private String lastUpdated;
    }
}
