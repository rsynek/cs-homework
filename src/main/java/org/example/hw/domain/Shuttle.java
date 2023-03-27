package org.example.hw.domain;

public class Shuttle extends Bus {

    protected int setupCost;

    // Planning variables: changes during planning, between score calculations.
    protected StopOrHub destination;

    @Override
    public int getSetupCost() {
        return setupCost;
    }

    public void setSetupCost(int setupCost) {
        this.setupCost = setupCost;
    }

    @Override
    public StopOrHub getDestination() {
        return destination;
    }

    public void setDestination(StopOrHub destination) {
        this.destination = destination;
    }

    @Override
    public Integer getPassengerQuantityTotal() {
        return super.getPassengerQuantityTotal();
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public int getDistanceFromTo(RoadLocation sourceLocation, RoadLocation targetLocation) {
        return sourceLocation.getShuttleDistanceTo(targetLocation);
    }

    @Override
    public int getDurationFromTo(RoadLocation sourceLocation, RoadLocation targetLocation) {
        return sourceLocation.getShuttleDurationTo(targetLocation);
    }

    public Bus getDestinationBus() {
        if (destination == null) {
            return null;
        }
        if (!(destination instanceof BusStop)) {
            return null;
        }
        return ((BusStop) destination).getBus();
    }

}
