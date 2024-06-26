package com.github.test.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.TestConfiguration;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
@TestConfiguration
public class DatasourcePropertiesCustomizer implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSourceProperties dataSourceProperties) {
            if (StringUtils.isBlank(dataSourceProperties.getDriverClassName())
                    && StringUtils.isNotBlank(dataSourceProperties.getUrl())) {
                String driverClassName = determineDriverClassName(dataSourceProperties.getUrl());
                if (StringUtils.isNotBlank(driverClassName)) {
                    log.info("success determine driver class name :{}", dataSourceProperties);
                    dataSourceProperties.setDriverClassName(driverClassName);
                }
            }
        }
        return bean;
    }

    /**
     * jdbc提供的推断driver class的方法
     * @param url
     * @return String
     **/
    private String determineDriverClassName(String url) {
        Driver driver = null;
        try {
            driver = DriverManager.getDriver(url);
        } catch (SQLException e) {
            log.warn("determine driver class name fail", e);
        }
        if (driver != null) {
            return driver.getClass().getName();
        }
        return null;
    }
}
