package com.lit.soullark.framework.utils;

import com.lit.soullark.framework.exceptions.EnvironmentLoadException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * @Author myou
 * @Date 2020/7/1  4:34 下午
 */
public class FrameworkEnvironmentPostProcessor implements EnvironmentPostProcessor {
    private Logger logger = LoggerFactory.getLogger(FrameworkEnvironmentPostProcessor.class);
    private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            String fileNameYml = "application.yml";
            String fileNameProperties = "application.properties";
            Resource basePathYml = new ClassPathResource(fileNameYml);
            Resource basePathProperties = new ClassPathResource(fileNameProperties);
            PropertySource<?> basePropertySource = null;
            if (basePathYml.exists()) {
                basePropertySource = loadYaml(basePathYml);
            }
            if (basePathProperties.exists()) {
                basePropertySource = loadYaml(basePathProperties);
            }
            assert basePropertySource != null : "application config load failed";
            Object profile = basePropertySource.getProperty("spring.profiles.active");
            if (null != profile) {
                String currentProfile = String.format("application-%s.yml", profile.toString());
                Resource resourceCur = new ClassPathResource(currentProfile);
                if (resourceCur.exists()) {
                    PropertySource<?> propertySourceCur = loadYaml(resourceCur);
                    environment.getPropertySources().addLast(propertySourceCur);
                }
                String currentProfileProperties = String.format("application-%s.properties", profile.toString());
                Resource resource = new ClassPathResource(currentProfileProperties);
                if (resource.exists()) {
                    PropertySource<?> propertySource = loadYaml(resource);
                    environment.getPropertySources().addLast(propertySource);
                }
            }
            environment.getPropertySources().addLast(basePropertySource);
        } catch (NullPointerException ex) {
            logger.info(String.format("application resource not found:%s", ex.getMessage()));
        }
    }

    private PropertySource<?> loadYaml(Resource path) {
        if (!path.exists()) {
            throw new EnvironmentLoadException("Resource " + path + " does not exist");
        }
        try {
            return this.loader.load("custom-resource", path).get(0);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load yaml configuration from " + path, ex);
        }
    }

}
