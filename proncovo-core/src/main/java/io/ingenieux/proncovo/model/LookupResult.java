package io.ingenieux.proncovo.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class LookupResult {
    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    Double[] latLng;

    public Double[] getLatLng() {
        return latLng;
    }

    public void setLatLng(Double[] latLng) {
        this.latLng = latLng;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("status", status)
                .append("lat", latLng[0])
                .append("lng", latLng[1])
                .toString();
    }
}
