package com.kiaev.client.car;

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
public class CarImagePathNormalizer {

    private static final Logger log = LoggerFactory.getLogger(CarImagePathNormalizer.class);
    private static final String CAR_IMAGE_PREFIX = "/images/cars/";

    private final Map<String, String> canonicalPathsByLowercaseName;

    public CarImagePathNormalizer() {
        this(loadCanonicalPaths());
    }

    CarImagePathNormalizer(Map<String, String> canonicalPathsByLowercaseName) {
        this.canonicalPathsByLowercaseName = new LinkedHashMap<>(canonicalPathsByLowercaseName);
    }

    public Car normalize(Car car) {
        if (car == null) {
            return null;
        }

        car.setImagePath(resolve(car.getImagePath()));
        car.setImageDetail1(resolve(car.getImageDetail1()));
        car.setImageDetail2(resolve(car.getImageDetail2()));
        return car;
    }

    String resolve(String rawPath) {
        if (!hasText(rawPath) || !rawPath.startsWith(CAR_IMAGE_PREFIX)) {
            return rawPath;
        }

        String fileName = rawPath.substring(CAR_IMAGE_PREFIX.length()).trim();
        if (!hasText(fileName)) {
            return rawPath;
        }

        String canonicalPath = canonicalPathsByLowercaseName.get(fileName.toLowerCase(Locale.ROOT));
        return hasText(canonicalPath) ? canonicalPath : rawPath;
    }

    private static Map<String, String> loadCanonicalPaths() {
        Map<String, String> canonicalPaths = new LinkedHashMap<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        for (String pattern : new String[] {
                "classpath:/static/images/cars/*",
                "classpath*:static/images/cars/*",
                "classpath*:BOOT-INF/classes/static/images/cars/*" }) {
            try {
                Resource[] resources = resolver.getResources(pattern);
                for (Resource resource : resources) {
                    String fileName = resource.getFilename();
                    if (!hasText(fileName)) {
                        continue;
                    }

                    canonicalPaths.putIfAbsent(
                            fileName.toLowerCase(Locale.ROOT),
                            CAR_IMAGE_PREFIX + fileName);
                }
            } catch (IOException ex) {
                log.debug("Failed to scan car images with pattern {}", pattern, ex);
            }
        }

        return canonicalPaths;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
