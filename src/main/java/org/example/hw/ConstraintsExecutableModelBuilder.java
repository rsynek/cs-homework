package org.example.hw;

import static org.drools.model.DSL.*;
import static org.drools.model.PatternDSL.*;

import org.drools.model.Global;
import org.drools.model.Index;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.example.hw.domain.BusStop;
import org.example.hw.domain.Coach;
import org.example.hw.domain.Shuttle;
import org.example.hw.domain.StopOrHub;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.constraint.drl.holder.HardSoftLongScoreHolderImpl;

public class ConstraintsExecutableModelBuilder {

    /**
     * rule "transportTime"
     *     when
     *         BusStop(transportTimeToHub != null, $cost : transportTimeRemainder < 0)
     *     then
     *         scoreHolder.addHardConstraintMatch(kcontext, $cost);
     * end
     */
    static Rule transportTime(Global<HardSoftLongScoreHolderImpl> scoreHolderGlobal) {
        Variable<BusStop> busStopVariable = declarationOf(BusStop.class);
        Variable<Integer> costVariable = declarationOf(Integer.class);
        Rule rule = rule("transportTime").build(
                pattern(busStopVariable)
                        .expr(busStop -> busStop.getTransportTimeToHub() != null)
                        .expr(busStop -> busStop.getTransportTimeRemainder() < 0)
                        .bind(costVariable, busStop -> busStop.getTransportTimeRemainder()),
                on(costVariable, scoreHolderGlobal)
                        .execute((kcontext, cost, scoreHolder) ->
                                scoreHolder.addHardConstraintMatch((RuleContext)kcontext, cost.longValue())));

        return rule;
    }

    /**
     * rule "shuttleDestinationIsCoachOrHub"
     *     when
     *         Shuttle(destination != null, $destination : destination)
     *         StopOrHub(this == $destination, visitedByCoach == false)
     *     then
     *         scoreHolder.addHardConstraintMatch(kcontext, - 1000000000L);
     * end
     */
    static Rule shuttleDestinationIsCoachOrHub(Global<HardSoftLongScoreHolderImpl> scoreHolderGlobal) {
        Variable<Shuttle> shuttleVariable = declarationOf(Shuttle.class);
        Variable<StopOrHub> stopOrHubVariable = declarationOf(StopOrHub.class);
        Variable<StopOrHub> destinationVariable = declarationOf(StopOrHub.class);
        Rule rule = rule("shuttleDestinationIsCoachOrHub").build(
                pattern(shuttleVariable)
                        .expr(shuttle -> shuttle.getDestination() != null)
                        .bind(destinationVariable, Shuttle::getDestination),
                pattern(stopOrHubVariable)
                        .expr("matchDestination", destinationVariable, StopOrHub::equals,
                                betaIndexedBy(StopOrHub.class, Index.ConstraintType.EQUAL, 1, stopOrHub -> stopOrHub, stopOrHub -> stopOrHub))
                        .expr(stopOrHub -> stopOrHub.isVisitedByCoach() == false),
                on(shuttleVariable, scoreHolderGlobal)
                        .execute((kcontext, shuttle, scoreHolder) -> scoreHolder.addHardConstraintMatch((RuleContext) kcontext,
                                -1_000_000_000L)));
        return rule;
    }

    /**
     * rule "coachStopLimit"
     *     when
     *         $coach : Coach($stopLimit : stopLimit)
     *         accumulate(
     *             $stop : BusStop(bus == $coach);
     *             $stopTotal : count($stop);
     *             $stopTotal > $stopLimit
     *         )
     *     then
     *         scoreHolder.addHardConstraintMatch(kcontext, ($stopLimit - $stopTotal) * 1000000L);
     * end
     */
    static Rule coachStopLimit(Global<HardSoftLongScoreHolderImpl> scoreHolderGlobal) {

        Variable<Coach> coachVariable = declarationOf(Coach.class);
        Variable<Integer> stopLimitVariable = declarationOf(Integer.class);
        Variable<BusStop> busStopVariable = declarationOf(BusStop.class);
        Variable<Long> stopTotalVariable = declarationOf(Long.class);

        Rule rule = rule("coachStopLimit").build(
                pattern(coachVariable)
                        .bind(stopLimitVariable, Coach::getStopLimit),
                accumulate(
                        pattern(busStopVariable)
                                .expr(coachVariable, (busStop, coach) -> busStop.getBus() == coach),
                        accFunction(org.drools.core.base.accumulators.CountAccumulateFunction::new, busStopVariable)
                                .as(stopTotalVariable)),
                pattern(stopTotalVariable)
                        .expr(stopLimitVariable, (stopTotal, stopLimit) -> stopTotal > stopLimit),
                on(stopLimitVariable, stopTotalVariable, scoreHolderGlobal)
                        .execute((kcontext, stopLimit, stopTotal, scoreHolder) -> {
                            scoreHolder.addHardConstraintMatch((RuleContext) kcontext, (stopLimit - stopTotal) * 1_000_000L);
                        }));
        return rule;
    }
}
