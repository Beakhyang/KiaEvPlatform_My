package com.kiaev.common.aws;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class AwsSecretsBootstrapTest {

    @AfterEach
    void tearDown() {
        System.clearProperty("GEMINI_API_KEY");
        System.clearProperty("KAKAO_API_KEY");
        System.clearProperty("CHARGING_API_KEY");
        System.clearProperty("api.key.kakao-map");
        AwsSecretsBootstrap.resetForTests();
    }

    @Test
    void parseSecretValuesExtractsJsonKeyValuePairs() {
        Map<String, String> values = AwsSecretsBootstrap.parseSecretValues("""
                {
                  "GEMINI_API_KEY": "gemini-secret",
                  "KAKAO_API_KEY": "kakao-secret",
                  "CHARGING_API_KEY": "charging-secret"
                }
                """);

        assertThat(values)
                .containsEntry("GEMINI_API_KEY", "gemini-secret")
                .containsEntry("KAKAO_API_KEY", "kakao-secret")
                .containsEntry("CHARGING_API_KEY", "charging-secret");
    }

    @Test
    void parseSecretValuesNormalizesKeysWithSpaces() {
        Map<String, String> values = AwsSecretsBootstrap.parseSecretValues("""
                {
                  "Gemini API Key": "gemini-secret",
                  "KAKAO_API_KEY": "kakao-secret"
                }
                """);

        assertThat(values)
                .containsEntry("Gemini API Key", "gemini-secret")
                .containsEntry("GEMINI_API_KEY", "gemini-secret")
                .containsEntry("KAKAO_API_KEY", "kakao-secret");
    }

    @Test
    void applyToSystemPropertiesSkipsAlreadyDefinedValues() {
        System.setProperty("GEMINI_API_KEY", "already-defined");

        int appliedCount = AwsSecretsBootstrap.applyToSystemProperties(Map.of(
                "GEMINI_API_KEY", "new-value",
                "KAKAO_API_KEY", "kakao-secret"));

        assertThat(appliedCount).isEqualTo(1);
        assertThat(System.getProperty("GEMINI_API_KEY")).isEqualTo("already-defined");
        assertThat(System.getProperty("KAKAO_API_KEY")).isEqualTo("kakao-secret");
    }

    @Test
    void extractQuotedStringPairsRecoversPrettyPrintedSecretText() {
        Map<String, String> values = AwsSecretsBootstrap.extractQuotedStringPairs("""
                {"CHARGING_API_KEY": "charging-secret",
                "Gemini API Key": "gemini-secret",
                "KAKAO_API_KEY": "kakao-secret"}
                """);

        assertThat(values)
                .containsEntry("CHARGING_API_KEY", "charging-secret")
                .containsEntry("GEMINI_API_KEY", "gemini-secret")
                .containsEntry("KAKAO_API_KEY", "kakao-secret");
    }

    @Test
    void buildDefaultPropertiesAddsAppSpecificAliases() {
        Map<String, Object> properties = AwsSecretsBootstrap.buildDefaultProperties(Map.of(
                "GEMINI_API_KEY", "gemini-secret",
                "KAKAO_API_KEY", "kakao-secret",
                "CHARGING_API_KEY", "charging-secret"));

        assertThat(properties)
                .containsEntry("api.key.chatbot", "gemini-secret")
                .containsEntry("api.key.kakao-map", "kakao-secret")
                .containsEntry("api.key.ev-charger", "charging-secret");
    }

    @Test
    void resolveValuePrefersExistingSystemProperties() {
        System.setProperty("api.key.kakao-map", "runtime-kakao-secret");

        assertThat(AwsSecretsBootstrap.resolveValue("api.key.kakao-map", "KAKAO_API_KEY"))
                .isEqualTo("runtime-kakao-secret");
    }
}
