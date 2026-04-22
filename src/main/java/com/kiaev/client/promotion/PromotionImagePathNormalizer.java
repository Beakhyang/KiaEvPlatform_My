package com.kiaev.client.promotion;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
public class PromotionImagePathNormalizer {

    private static final Logger log = LoggerFactory.getLogger(PromotionImagePathNormalizer.class);
    private static final String PROMOTION_IMAGE_PREFIX = "/images/promotion/";

    private final Map<String, String> canonicalPathsByLowercaseName;

    public PromotionImagePathNormalizer() {
        this(loadCanonicalPaths());
    }

    PromotionImagePathNormalizer(Map<String, String> canonicalPathsByLowercaseName) {
        this.canonicalPathsByLowercaseName = new LinkedHashMap<>(canonicalPathsByLowercaseName);
    }

    public Promotion normalize(Promotion promotion) {
        if (promotion == null) {
            return null;
        }

        promotion.setBannerImageUrl(resolve(promotion.getBannerImageUrl()));
        return promotion;
    }

    public String resolve(String rawPath) {
        if (!hasText(rawPath)) {
            return rawPath;
        }

        String normalizedPath = rawPath.trim().replace('\\', '/');
        if (normalizedPath.startsWith("http://") || normalizedPath.startsWith("https://")) {
            return normalizedPath;
        }

        String fileName = extractFileName(normalizedPath);
        if (!hasText(fileName)) {
            return normalizedPath;
        }

        String canonicalPath = canonicalPathsByLowercaseName.get(fileName.toLowerCase(Locale.ROOT));
        return hasText(canonicalPath) ? canonicalPath : normalizedPath;
    }

    private static String extractFileName(String path) {
        int lastSlashIndex = path.lastIndexOf('/');
        if (lastSlashIndex < 0) {
            return path.trim();
        }

        return path.substring(lastSlashIndex + 1).trim();
    }

    private static Map<String, String> loadCanonicalPaths() {
        Map<String, String> canonicalPaths = new LinkedHashMap<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        for (String pattern : new String[] {
                "classpath:/static/images/promotion/*",
                "classpath*:static/images/promotion/*",
                "classpath*:BOOT-INF/classes/static/images/promotion/*" }) {
            try {
                Resource[] resources = resolver.getResources(pattern);
                for (Resource resource : resources) {
                    String fileName = resource.getFilename();
                    if (!hasText(fileName)) {
                        continue;
                    }

                    canonicalPaths.putIfAbsent(
                            fileName.toLowerCase(Locale.ROOT),
                            PROMOTION_IMAGE_PREFIX + fileName);
                }
            } catch (IOException ex) {
                log.debug("Failed to scan promotion images with pattern {}", pattern, ex);
            }
        }

        return canonicalPaths;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
