package org.example.hw.domain;

import java.util.List;

public interface StopOrHub {

    String getName();

    /**
     * @return never null
     */
    RoadLocation getLocation();

    boolean isVisitedByCoach();

    List<Shuttle> getTransferShuttleList();

    void setTransferShuttleList(List<Shuttle> transferShuttleList);

    Integer getTransportTimeToHub();

}
