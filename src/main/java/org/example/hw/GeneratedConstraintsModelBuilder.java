package org.example.hw;

import org.drools.core.common.AgendaItem;
import org.drools.model.*;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function2;
import org.drools.model.functions.Predicate2;
import org.drools.model.functions.accumulate.AccumulateFunction;
import org.drools.model.impl.ModelImpl;
import org.drools.model.view.ViewItem;
import org.example.hw.domain.*;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.constraint.streams.common.inliner.JustificationsSupplier;
import org.optaplanner.constraint.streams.common.inliner.UndoScoreImpacter;
import org.optaplanner.constraint.streams.common.inliner.WeightedScoreImpacter;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;

import static org.drools.model.DSL.*;
import static org.drools.model.PatternDSL.*;
import static org.drools.model.PatternDSL.pattern;

public class GeneratedConstraintsModelBuilder {

    /**
     * Constraint transportTime(ConstraintFactory constraintFactory) {
     *         return constraintFactory.forEach(BusStop.class)
     *                 .filter(busStop -> busStop.getTransportTimeToHub() != null && busStop.getTransportTimeRemainder() < 0)
     *                 .penalizeLong(HardSoftLongScore.ONE_HARD,
     *                         busStop -> -busStop.getTransportTimeRemainder())
     *                 .asConstraint("transportTime");
     *     }
     */
    static Rule generatedTransportTime(ModelImpl model) {
        EntityDescriptor entityDescriptor = null; // Leaving out the details.

        /*
         * Difference explanation:
         * Instead of a single ScoreHolder, we use a separate WeightedScoreImpacter for every constraint.
         * This interface creates also this UndoScoreImpacter, which is later used to undo a move's effect on the score.
         */ 
        Global<WeightedScoreImpacter> scoreImpacterGlobal = globalOf(WeightedScoreImpacter.class,
                "org.optaplanner.examples.coachshuttlegathering.domain", "scoreImpacter1");
        model.addGlobal(scoreImpacterGlobal);

        /*
         * Difference explanation:
         * Everything is named to avoid conflicts.
         */
        Variable<BusStop> busStopVariable = declarationOf(BusStop.class, "var_1");
        return rule("org.optaplanner.examples.coachshuttlegathering.domain", "transportTime").build(
                pattern(busStopVariable)
                        /*
                         * Difference explanation:
                         * The first expression is the nullity filter that forEach adds.
                         */
                        .expr("Filter using org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor$$Lambda...",
                                entityDescriptor::hasNoNullVariables)
                        .expr("Filter using org.optaplanner.examples.coachshuttlegathering.score.CoachShuttleGatheringConstraintProvider$$Lambda$...",
                                busStop -> busStop.getTransportTimeToHub() != null && busStop.getTransportTimeRemainder() < 0),
                on(scoreImpacterGlobal, busStopVariable)
                        .execute((drools, scoreImpacter, a) -> {
                            ToLongFunction<BusStop> matchWeighter = (busStop -> -busStop.getTransportTimeRemainder());
                            /*
                             * Difference explanation:
                             * Of course, there is a lot of things going on regarding justifications, but that's not
                             * related to the Drools executable model.
                             */
                            JustificationsSupplier justificationsSupplier = null; // Leaving out the details.

                            // leaving out assertCorrectImpact()
                            long impact = matchWeighter.applyAsLong(a);
                            UndoScoreImpacter undoImpact = scoreImpacter.impactScore(impact, justificationsSupplier);
                            /*
                             * Difference explanation:
                             * To undo a move's effect on the score.
                             */
                            AgendaItem agendaItem = (AgendaItem) ((RuleContext) drools).getMatch();
                            agendaItem.setCallback(undoImpact);
                        })
        );
    }
    
    /**
     * Constraint shuttleDestinationIsCoachOrHub(ConstraintFactory constraintFactory) {
     *         return constraintFactory.forEach(Shuttle.class)
     *                 .filter(shuttle -> shuttle.getDestination() != null)
     *                 .join(StopOrHub.class, equal(Shuttle::getDestination, Function.identity()))
     *                 .filter((shuttle, stop) -> !stop.isVisitedByCoach())
     *                 .penalizeLong(HardSoftLongScore.ONE_HARD,
     *                         (bus, stop) -> 1000000000L)
     *                 .asConstraint("shuttleDestinationIsCoachOrHub");
     *     }
     */
    static Rule generatedShuttleDestinationIsCoachOrHub(ModelImpl model) {
        EntityDescriptor entityDescriptor = null; // Leaving out the details.

        Global<WeightedScoreImpacter> scoreImpacterGlobal = globalOf(WeightedScoreImpacter.class,
                "org.optaplanner.examples.coachshuttlegathering.domain", "scoreImpacter1");
        model.addGlobal(scoreImpacterGlobal);

        Function<Shuttle, Object> leftMapping = Shuttle::getDestination;
        Function<StopOrHub, StopOrHub> rightMapping = Function.identity();
        /*
         * Difference explanation:
         * Additional indirection; is it just to comply with the EM API?
         */
        Function1<StopOrHub, Object> rightExtractor = b -> rightMapping.apply(b);
        Predicate2<StopOrHub, Shuttle> predicate = (b, a) -> Objects.equals(leftMapping.apply(a), rightExtractor.apply(b));

        /*
         * Difference explanation:
         * Interestingly, the CS-D does not have to deal with exact types; 'Object' is good enough for equality,
         * 'Comparable' is good enough for LT/GT, etc.
         */
        BetaIndex<StopOrHub, Shuttle, ?> index = betaIndexedBy(Object.class, Index.ConstraintType.EQUAL, 0, rightExtractor,
                leftMapping::apply, Object.class);

        Variable<Shuttle> shuttleVariable = declarationOf(Shuttle.class, "var_1");
        Variable<StopOrHub> stopOrHubVariable = declarationOf(StopOrHub.class, "var_2");
        return rule("org.optaplanner.examples.coachshuttlegathering.domain", "shuttleDestinationIsCoachOrHub").build(
                pattern(shuttleVariable)
                        .expr("Filter using org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor$$Lambda...",
                                entityDescriptor::hasNoNullVariables)
                        .expr("Filter using org.optaplanner.examples.coachshuttlegathering.score.CoachShuttleGatheringConstraintProvider$$Lambda$...",
                                shuttle -> shuttle.getDestination() != null),
                pattern(stopOrHubVariable)
                        /*
                         * Difference explanation:
                         * I used 'bind' to get the shuttle destination; here the predicate and the index use proper
                         * mapping functions to get to it.
                         */
                        .expr("Join using joiner #0 in ...", shuttleVariable, predicate, index)
                        .expr("Filter using ...", shuttleVariable, (stop, shuttle) -> !stop.isVisitedByCoach()),
                on(scoreImpacterGlobal, shuttleVariable, stopOrHubVariable)
                        .execute((drools, scoreImpacter, a, b) -> {
                            ToLongBiFunction<Shuttle, StopOrHub> matchWeighter = ((shuttle, stopOrHub) -> -1_000_000_000L);
                            JustificationsSupplier justificationsSupplier = null; // Leaving out the details.

                            // assertCorrectImpact()
                            long impact = matchWeighter.applyAsLong(a, b);
                            UndoScoreImpacter undoImpact = scoreImpacter.impactScore(impact, justificationsSupplier);
                            AgendaItem agendaItem = (AgendaItem) ((RuleContext) drools).getMatch();
                            agendaItem.setCallback(undoImpact);
                        }));
    }
    
    /**
     *     Constraint coachStopLimit(ConstraintFactory constraintFactory) {
     *         return constraintFactory.forEach(Coach.class)
     *                 .join(BusStop.class, equal(coach -> coach, BusStop::getBus))
     *                 .groupBy((coach, busStop) -> coach, countBi())
     *                 .filter((coach, stopCount) -> stopCount > coach.getStopLimit())
     *                 .penalizeLong(HardSoftLongScore.ONE_HARD,
     *                         (coach, stopCount) -> (stopCount - coach.getStopLimit()) * 1000000L)
     *                 .asConstraint("coachStopLimit");
     *     }
     */
    static Rule generatedCoachStopLimit(ModelImpl model) {
        EntityDescriptor entityDescriptor = null; // Leaving out the details.

        Global<WeightedScoreImpacter> scoreImpacterGlobal = globalOf(WeightedScoreImpacter.class,
                "org.optaplanner.examples.coachshuttlegathering.domain", "scoreImpacter1");
        model.addGlobal(scoreImpacterGlobal);

        Function<Coach, Object> leftMapping = coach -> coach;
        Function<BusStop, Bus> rightMapping = BusStop::getBus;
        Function1<BusStop, Object> rightExtractor = b -> rightMapping.apply(b);
        Predicate2<BusStop, Coach> predicate = (b, a) -> Objects.equals(leftMapping.apply(a), rightExtractor.apply(b));

        BetaIndex<BusStop, Coach, ?> index = betaIndexedBy(Object.class, Index.ConstraintType.EQUAL, 0, rightExtractor,
                leftMapping::apply, Object.class);

        Variable<Coach> coachVariable = declarationOf(Coach.class, "var_1");
        Variable<BusStop> busStopVariable = declarationOf(BusStop.class, "var_2");

        PatternDSL.PatternDef<Coach> coachPatternDef = pattern(coachVariable)
                .expr("Filter using org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor$$Lambda...",
                        entityDescriptor::hasNoNullVariables);
        PatternDSL.PatternDef<BusStop> busStopPatternDef = pattern(busStopVariable)
                .expr("Join using joiner #1 in ...", coachVariable, predicate, index);

        BiFunction<Coach, BusStop, Coach> keyMappingA = (coach, busStop) -> coach;

        Variable<Coach> groupKey = declarationOf(Coach.class, "groupKey_3");
        Variable<Integer> accumulateOutput = declarationOf(Integer.class, "output_4");

        Function2<Coach, BusStop, Coach> groupKeyExtractor = (a, b) -> keyMappingA.apply(a, b);

        /*
         * Difference explanation:
         * The CS-D uses groupBy instead of accumulate.
         */
        AccumulateFunction accumulateFunction = new AccumulateFunction(null, null) // () -> new BiAccumulator<>(coachVariable, busStopVariable, ConstraintCollectors.countBi())
                .with(coachVariable, busStopVariable)
                .as(accumulateOutput);
        ViewItem<?> groupByPattern = groupBy(and(coachPatternDef, busStopPatternDef), coachVariable, busStopVariable, groupKey, groupKeyExtractor, accumulateFunction);

        return rule("org.optaplanner.examples.coachshuttlegathering.domain", "shuttleDestinationIsCoachOrHub").build(
                coachPatternDef,
                busStopPatternDef,
                /*
                 * Difference explanation:
                 * The biggest difference is, that if I tried to inline the expression, as I did in the previous assignment,
                 * my head would explode.
                 */
                groupByPattern,
                /*
                 * Difference explanation:
                 * As a result of using groupBy, there has to be an additional filter, which originally was a part of accumulate.
                 */
                pattern(accumulateOutput)
                        .expr("Filter using org.optaplanner.examples.coachshuttlegathering.score.CoachShuttleGatheringConstraintProvider$$Lambda$...",
                                groupKey, (stopCount, coach) -> stopCount > coach.getStopLimit()),
                on(scoreImpacterGlobal, groupKey, accumulateOutput)
                        .execute((drools, scoreImpacter, coach, stopCount) -> {
                            ToLongBiFunction<Coach, Integer> matchWeighter = (a, b) -> (b - a.getStopLimit()) * 1_000_000L;
                            JustificationsSupplier justificationsSupplier = null; // Leaving out the details.

                            // assertCorrectImpact()
                            long impact = matchWeighter.applyAsLong(coach, stopCount);
                            UndoScoreImpacter undoImpact = scoreImpacter.impactScore(impact, justificationsSupplier);
                            AgendaItem agendaItem = (AgendaItem) ((RuleContext) drools).getMatch();
                            agendaItem.setCallback(undoImpact);
                        })
        );
    }
}
