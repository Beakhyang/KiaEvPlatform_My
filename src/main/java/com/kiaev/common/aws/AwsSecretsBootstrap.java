package com.kiaev.common.aws;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClientBuilder;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

public final class AwsSecretsBootstrap {

    private static final Logger log = Logger.getLogger(AwsSecretsBootstrap.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Pattern quotedStringPairPattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"");
    private static volatile Map<String, String> cachedResolvedValues = Map.of();
    private static volatile boolean loadAttempted;

    private AwsSecretsBootstrap() {
    }

    public static Map<String, Object> loadDefaultProperties() {
        String secretName = firstNonBlank(
                System.getProperty("AWS_SECRET_NAME"),
                System.getProperty("aws.secretName"),
                System.getenv("AWS_SECRET_NAME"));

        if (!hasText(secretName)) {
            loadAttempted = true;
            cachedResolvedValues = Map.of();
            return Map.of();
        }

        String regionName = firstNonBlank(
                System.getProperty("AWS_REGION"),
                System.getProperty("aws.region"),
                System.getenv("AWS_REGION"),
                System.getenv("AWS_DEFAULT_REGION"));

        try (SecretsManagerClient client = buildClient(regionName)) {
            String secretString = client.getSecretValue(GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build())
                    .secretString();

            Map<String, String> secretValues = parseSecretValues(secretString);
            Map<String, Object> defaultProperties = buildDefaultProperties(secretValues);
            cacheResolvedValues(defaultProperties);
            applyToSystemProperties(secretValues);
            applyObjectPropertiesToSystemProperties(defaultProperties);

            log.info(() -> "Loaded " + secretValues.size() + " secret value(s) from AWS Secrets Manager: " + secretName);
            return defaultProperties;
        } catch (Exception ex) {
            loadAttempted = true;
            cachedResolvedValues = Map.of();
            log.log(Level.WARNING,
                    "Failed to load AWS Secrets Manager secret '" + secretName
                            + "'. The application will continue with existing environment values.",
                    ex);
            return Map.of();
        }
    }

    public static String resolveValue(String... candidateKeys) {
        for (String candidateKey : candidateKeys) {
            String systemValue = readSystemValue(candidateKey);
            if (hasText(systemValue)) {
                return systemValue.trim();
            }
        }

        ensureValuesLoaded();

        Map<String, String> snapshot = cachedResolvedValues;
        for (String candidateKey : candidateKeys) {
            for (String lookupKey : candidateLookupKeys(candidateKey)) {
                String value = snapshot.get(lookupKey);
                if (hasText(value)) {
                    return value.trim();
                }
            }
        }

        return "";
    }

    static SecretsManagerClient buildClient(String regionName) {
        SecretsManagerClientBuilder builder = SecretsManagerClient.builder();
        if (hasText(regionName)) {
            builder.region(Region.of(regionName.trim()));
        }
        return builder.build();
    }

    static Map<String, String> parseSecretValues(String secretString) {
        if (!hasText(secretString)) {
            return Map.of();
        }

        try {
            Map<String, Object> rawValues = objectMapper.readValue(
                    secretString,
                    new TypeReference<Map<String, Object>>() {
                    });

            Map<String, String> parsedValues = new LinkedHashMap<>();
            rawValues.forEach((key, value) -> addCandidateKeys(parsedValues, key, value));
            return parsedValues;
        } catch (Exception ex) {
            Map<String, String> fallbackValues = extractQuotedStringPairs(secretString);
            if (!fallbackValues.isEmpty()) {
                log.info("Recovered AWS secret values with the fallback text parser.");
                return fallbackValues;
            }

            log.log(Level.WARNING,
                    "AWS Secrets Manager secret value is not a JSON object. Existing environment values will be used.",
                    ex);
            return Map.of();
        }
    }

    static int applyToSystemProperties(Map<String, String> secretValues) {
        int appliedCount = 0;

        for (Map.Entry<String, String> entry : secretValues.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (!hasText(key) || !hasText(value)) {
                continue;
            }

            if (hasText(System.getProperty(key)) || hasText(System.getenv(key))) {
                continue;
            }

            System.setProperty(key, value);
            appliedCount++;
        }

        return appliedCount;
    }

    static Map<String, Object> buildDefaultProperties(Map<String, String> secretValues) {
        Map<String, Object> defaultProperties = new LinkedHashMap<>(secretValues);

        putAlias(defaultProperties, "GEMINI_API_KEY", "api.key.chatbot");
        putAlias(defaultProperties, "KAKAO_API_KEY", "api.key.kakao-map");
        putAlias(defaultProperties, "CHARGING_API_KEY", "api.key.ev-charger");

        return defaultProperties;
    }

    static int applyObjectPropertiesToSystemProperties(Map<String, Object> properties) {
        int appliedCount = 0;

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (!hasText(key) || !(value instanceof String textValue) || !hasText(textValue)) {
                continue;
            }

            if (hasText(System.getProperty(key)) || hasText(System.getenv(key))) {
                continue;
            }

            System.setProperty(key, textValue);
            appliedCount++;
        }

        return appliedCount;
    }

    static void resetForTests() {
        cachedResolvedValues = Map.of();
        loadAttempted = false;
    }

    private static String firstNonBlank(String... candidates) {
        for (String candidate : candidates) {
            if (hasText(candidate)) {
                return candidate.trim();
            }
        }
        return "";
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static void ensureValuesLoaded() {
        if (!loadAttempted) {
            loadDefaultProperties();
        }
    }

    private static void cacheResolvedValues(Map<String, Object> properties) {
        Map<String, String> cachedValues = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            if (entry.getValue() instanceof String textValue && hasText(textValue)) {
                cachedValues.put(entry.getKey(), textValue);
            }
        }

        cachedResolvedValues = Map.copyOf(cachedValues);
        loadAttempted = true;
    }

    private static String readSystemValue(String candidateKey) {
        for (String lookupKey : candidateLookupKeys(candidateKey)) {
            String systemProperty = System.getProperty(lookupKey);
            if (hasText(systemProperty)) {
                return systemProperty;
            }

            String envValue = System.getenv(lookupKey);
            if (hasText(envValue)) {
                return envValue;
            }
        }

        return "";
    }

    private static String[] candidateLookupKeys(String candidateKey) {
        if (!hasText(candidateKey)) {
            return new String[0];
        }

        String normalizedKey = normalizeKey(candidateKey);
        if (candidateKey.equals(normalizedKey)) {
            return new String[] { candidateKey };
        }

        return new String[] { candidateKey, normalizedKey };
    }

    private static void addCandidateKeys(Map<String, String> target, String key, Object value) {
        if (!hasText(key) || value == null) {
            return;
        }

        String textValue = String.valueOf(value);
        if (!hasText(textValue)) {
            return;
        }

        target.putIfAbsent(key, textValue);

        String normalizedKey = normalizeKey(key);
        if (hasText(normalizedKey)) {
            target.putIfAbsent(normalizedKey, textValue);
        }
    }

    static Map<String, String> extractQuotedStringPairs(String secretString) {
        Map<String, String> parsedValues = new LinkedHashMap<>();
        Matcher matcher = quotedStringPairPattern.matcher(secretString);

        while (matcher.find()) {
            addCandidateKeys(parsedValues, matcher.group(1), matcher.group(2));
        }

        return parsedValues;
    }

    static String normalizeKey(String key) {
        if (!hasText(key)) {
            return "";
        }

        return key.trim()
                .replaceAll("[^A-Za-z0-9]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "")
                .toUpperCase(Locale.ROOT);
    }

    private static void putAlias(Map<String, Object> target, String sourceKey, String aliasKey) {
        Object value = target.get(sourceKey);
        if (value instanceof String textValue && hasText(textValue)) {
            target.putIfAbsent(aliasKey, textValue);
        }
    }
}
