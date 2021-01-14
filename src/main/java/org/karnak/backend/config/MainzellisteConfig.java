package org.karnak.backend.config;

import javax.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "mainzelliste")
public class MainzellisteConfig {

  private static MainzellisteConfig instance;
  private String apikey;
  private String serverurl;
  private String idtypes;

  public static MainzellisteConfig getInstance() {
    return instance;
  }

  @PostConstruct
  public void postConstruct() {
    instance = this;
  }

  public String getApikey() {
    return apikey;
  }

  public void setApikey(String apikey) {
    this.apikey = apikey;
  }

  public String getServerurl() {
    return serverurl;
  }

  public void setServerurl(String serverurl) {
    this.serverurl = serverurl;
  }

  public String getIdtypes() {
    return idtypes;
  }

  public void setIdtypes(String idtypes) {
    this.idtypes = idtypes;
  }
}
