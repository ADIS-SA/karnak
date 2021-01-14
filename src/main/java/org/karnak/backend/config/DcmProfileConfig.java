package org.karnak.backend.config;

import javax.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "dcmprofile")
public class DcmProfileConfig {

  private static DcmProfileConfig instance;

  public static DcmProfileConfig getInstance() {
    return instance;
  }

  public static void setInstance(DcmProfileConfig instance) {
    DcmProfileConfig.instance = instance;
  }

  @PostConstruct
  public void postConstruct() {
    instance = this;
  }
}
