package org.example.hw.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

public class CoachShuttleGatheringSolution extends AbstractPersistable {

    protected String name;
    protected List<RoadLocation> locationList;
    protected List<Coach> coachList;
    protected List<Shuttle> shuttleList;
    protected List<BusStop> stopList;
    protected BusHub hub;

    protected HardSoftLongScore score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RoadLocation> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<RoadLocation> locationList) {
        this.locationList = locationList;
    }

    public List<Coach> getCoachList() {
        return coachList;
    }

    public void setCoachList(List<Coach> coachList) {
        this.coachList = coachList;
    }

    public List<Shuttle> getShuttleList() {
        return shuttleList;
    }

    public void setShuttleList(List<Shuttle> shuttleList) {
        this.shuttleList = shuttleList;
    }

    public List<BusStop> getStopList() {
        return stopList;
    }

    public void setStopList(List<BusStop> stopList) {
        this.stopList = stopList;
    }

    public BusHub getHub() {
        return hub;
    }

    public void setHub(BusHub hub) {
        this.hub = hub;
    }

    public HardSoftLongScore getScore() {
        return score;
    }

    public void setScore(HardSoftLongScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public List<BusHub> getHubRange() {
        return Collections.singletonList(hub);
    }

    public List<Bus> getBusList() {
        List<Bus> busList = new ArrayList<>(coachList.size() + shuttleList.size());
        busList.addAll(coachList);
        busList.addAll(shuttleList);
        return busList;
    }

}
