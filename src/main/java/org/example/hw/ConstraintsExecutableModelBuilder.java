package org.example.hw;

import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.globalOf;
import static org.drools.model.DSL.on;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.rule;

import org.drools.model.Global;
import org.drools.model.Model;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.impl.ModelImpl;
import org.example.hw.domain.BusStop;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.constraint.drl.holder.HardSoftLongScoreHolderImpl;

public class ConstraintsExecutableModelBuilder {

    public static Model buildModel() {
        Global<HardSoftLongScoreHolderImpl> scoreHolder = globalOf(HardSoftLongScoreHolderImpl.class, "", "scoreHolder");

        Model model = new ModelImpl()
                .addGlobal(scoreHolder)
                .addRule(transportTime(scoreHolder));

        return model;
    }

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
    static Rule shuttleDestinationIsCoachOrHub() {
        throw new UnsupportedOperationException();
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
    static Rule coachStopLimit() {
        throw new UnsupportedOperationException();
    }
}
