package org.example.hw.domain;

import java.util.List;

public class BusStop extends AbstractPersistable implements BusOrStop, StopOrHub {

    protected String name;
    protected RoadLocation location;
    protected int passengerQuantity;
    protected int transportTimeLimit;

    // Planning variables: changes during planning, between score calculations.
    protected BusOrStop previousBusOrStop;

    // Shadow variables
    protected BusStop nextStop;
    protected Bus bus;
    protected List<Shuttle> transferShuttleList;
    protected Integer transportTimeToHub;

    public BusStop() {

    }

    public BusStop(long id, BusOrStop previousBusOrStop, Bus bus) {
        this(id, previousBusOrStop, bus, 0);
    }

    public BusStop(long id, BusOrStop previousBusOrStop, Bus bus, int passengerQuantity) {
        super(id);
        this.previousBusOrStop = previousBusOrStop;
        this.bus = bus;
        this.passengerQuantity = passengerQuantity;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public RoadLocation getLocation() {
        return location;
    }

    public void setLocation(RoadLocation location) {
        this.location = location;
    }

    public int getPassengerQuantity() {
        return passengerQuantity;
    }

    public void setPassengerQuantity(int passengerQuantity) {
        this.passengerQuantity = passengerQuantity;
    }

    public int getTransportTimeLimit() {
        return transportTimeLimit;
    }

    public void setTransportTimeLimit(int transportTimeLimit) {
        this.transportTimeLimit = transportTimeLimit;
    }

    public BusOrStop getPreviousBusOrStop() {
        return previousBusOrStop;
    }

    public void setPreviousBusOrStop(BusOrStop previousBusOrStop) {
        this.previousBusOrStop = previousBusOrStop;
    }

    @Override
    public BusStop getNextStop() {
        return nextStop;
    }

    @Override
    public void setNextStop(BusStop nextStop) {
        this.nextStop = nextStop;
    }

    @Override
    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    @Override
    public List<Shuttle> getTransferShuttleList() {
        return transferShuttleList;
    }

    @Override
    public void setTransferShuttleList(List<Shuttle> transferShuttleList) {
        this.transferShuttleList = transferShuttleList;
    }

    @Override
    public Integer getTransportTimeToHub() {
        return transportTimeToHub;
    }

    public void setTransportTimeToHub(Integer transportTimeToHub) {
        this.transportTimeToHub = transportTimeToHub;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public int getDistanceFromPreviousCost() {
        if (previousBusOrStop == null) {
            return 0;
        }
        return getDistanceFrom(previousBusOrStop) * bus.getMileageCost();
    }

    public int getDistanceFrom(BusOrStop busOrStop) {
        return bus.getDistanceFromTo(busOrStop.getLocation(), location);
    }

    public int getDistanceToDestinationCost(StopOrHub destination) {
        return bus.getDistanceFromTo(location, destination.getLocation()) * bus.getMileageCost();
    }

    @Override
    public boolean isVisitedByCoach() {
        return bus != null && bus instanceof Coach;
    }

    public Integer getTransportTimeRemainder() {
        if (transportTimeToHub == null) {
            return null;
        }
        if (passengerQuantity <= 0) {
            return 0;
        }
        return transportTimeLimit - transportTimeToHub;
    }

    public String getTransportLabel() {
        if (passengerQuantity <= 0) {
            return null;
        }
        return (transportTimeToHub == null ? "inf" : transportTimeToHub.toString()) + "/" + transportTimeLimit;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + name;
    }

}
