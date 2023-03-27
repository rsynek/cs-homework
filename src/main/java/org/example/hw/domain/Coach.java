package org.example.hw.domain;

public class Coach extends Bus {

    protected int stopLimit;
    protected BusHub destination;

    public int getStopLimit() {
        return stopLimit;
    }

    public void setStopLimit(int stopLimit) {
        this.stopLimit = stopLimit;
    }

    public void setDestination(BusHub destination) {
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
    public int getSetupCost() {
        return 0;
    }

    @Override
    public int getDistanceFromTo(RoadLocation sourceLocation, RoadLocation targetLocation) {
        return sourceLocation.getCoachDistanceTo(targetLocation);
    }

    @Override
    public int getDurationFromTo(RoadLocation sourceLocation, RoadLocation targetLocation) {
        return sourceLocation.getCoachDurationTo(targetLocation);
    }

    @Override
    public StopOrHub getDestination() {
        return destination;
    }

    public int getDistanceToDestinationCost() {
        return getDistanceFromTo(departureLocation, destination.getLocation()) * getMileageCost();
    }

}
