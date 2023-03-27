package org.example.hw.domain;

public interface BusOrStop {

    Long getId();

    /**
     * @return never null
     */
    RoadLocation getLocation();

    /**
     * @return sometimes null
     */
    Bus getBus();

    /**
     * @return sometimes null
     */
    BusStop getNextStop();

    void setNextStop(BusStop nextStop);

}
