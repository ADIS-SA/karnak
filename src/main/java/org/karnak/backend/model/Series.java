package org.karnak.backend.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

public class Series {

  private final String seriesInstanceUID;
  private final Map<String, SopInstance> sopInstanceMap;

  private String seriesDescription;
  private LocalDateTime seriesDate;

  public Series(String seriesInstanceUID) {
    this.seriesInstanceUID = Objects.requireNonNull(seriesInstanceUID, "seriesInstanceUID is null");
    this.sopInstanceMap = new HashMap<>();
    this.seriesDescription = "";
  }

  public String getSeriesInstanceUID() {
    return seriesInstanceUID;
  }

  public String getSeriesDescription() {
    return seriesDescription;
  }

  public void setSeriesDescription(String s) {
    seriesDescription = s;
  }

  public LocalDateTime getSeriesDate() {
    return seriesDate;
  }

  public void setSeriesDate(LocalDateTime seriesDate) {
    this.seriesDate = seriesDate;
  }

  public void addSopInstance(SopInstance s) {
    SopInstance.addSopInstance(sopInstanceMap, s);
  }

  public SopInstance removeSopInstance(String sopUID) {
    return SopInstance.removeSopInstance(sopInstanceMap, sopUID);
  }

  public SopInstance getSopInstance(String sopUID) {
    return SopInstance.getSopInstance(sopInstanceMap, sopUID);
  }

  public Set<Entry<String, SopInstance>> getEntrySet() {
    return sopInstanceMap.entrySet();
  }

  public Collection<SopInstance> getSopInstances() {
    return sopInstanceMap.values();
  }

  public boolean isEmpty() {
    return sopInstanceMap.isEmpty();
  }

  @Override
  public int hashCode() {
    return 31 + seriesInstanceUID.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Series other = (Series) obj;
    return seriesInstanceUID.equals(other.seriesInstanceUID);
  }
}
