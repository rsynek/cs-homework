package org.example.hw;

import static org.drools.model.DSL.globalOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Function;

import org.drools.model.Global;
import org.drools.model.Model;
import org.drools.model.Rule;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.KieBaseBuilder;
import org.example.hw.domain.BusHub;
import org.example.hw.domain.BusStop;
import org.example.hw.domain.Coach;
import org.example.hw.domain.Shuttle;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import org.optaplanner.constraint.drl.holder.HardSoftLongScoreHolderImpl;

class ConstraintExecutableModelBuilderTest {

    private static final String SCORE_HOLDER_GLOBAL = "scoreHolder";

    @Test
    void transportTime() {
        KieSession kieSession = buildKieSession(ConstraintsExecutableModelBuilder::transportTime);

        BusHub busHub = new BusHub();
        Coach bus = new Coach();
        bus.setDestination(busHub);
        BusStop busStop = new BusStop(1L, null, bus, 10);
        busStop.setTransportTimeToHub(100);
        busStop.setTransportTimeLimit(50);
        busStop.setBus(bus);

        kieSession.insert(busHub);
        kieSession.insert(bus);
        kieSession.insert(busStop);

        kieSession.fireAllRules();

        HardSoftLongScoreHolderImpl scoreHolder = (HardSoftLongScoreHolderImpl) kieSession.getGlobal(SCORE_HOLDER_GLOBAL);
        assertThat(scoreHolder.getHardScore()).isEqualTo(-50);
    }

    @Test
    void shuttleDestinationIsCoachOrHub() {
        KieSession kieSession = buildKieSession(ConstraintsExecutableModelBuilder::shuttleDestinationIsCoachOrHub);

        Shuttle shuttle = new Shuttle();

        BusStop busStop = new BusStop(1L, null, shuttle, 10);
        busStop.setTransportTimeToHub(100);
        busStop.setTransportTimeLimit(50);

        shuttle.setDestination(busStop);

        kieSession.insert(shuttle);
        kieSession.insert(busStop);
        kieSession.fireAllRules();

        HardSoftLongScoreHolderImpl scoreHolder = (HardSoftLongScoreHolderImpl) kieSession.getGlobal(SCORE_HOLDER_GLOBAL);
        assertThat(scoreHolder.getHardScore()).isEqualTo(-1_000_000_000L);
    }

    @Test
    void coachStopLimit() {
        KieSession kieSession = buildKieSession(ConstraintsExecutableModelBuilder::coachStopLimit);

        BusHub busHub = new BusHub();
        Coach coach = new Coach();
        coach.setDestination(busHub);
        coach.setStopLimit(5);

        for (int i = 0; i < 10; i++) {
            BusStop busStop = new BusStop(i, null, coach, 10);
            kieSession.insert(busStop);
        }

        kieSession.insert(busHub);
        kieSession.insert(coach);
        kieSession.fireAllRules();

        HardSoftLongScoreHolderImpl scoreHolder = (HardSoftLongScoreHolderImpl) kieSession.getGlobal(SCORE_HOLDER_GLOBAL);
        assertThat(scoreHolder.getHardScore()).isEqualTo(-5_000_000L);
    }

    private KieSession buildKieSession(Function<Global<HardSoftLongScoreHolderImpl>, Rule> ruleFunction) {
        KieBaseConfiguration kieBaseConfiguration = KieServices.Factory.get().newKieBaseConfiguration();
        Global<HardSoftLongScoreHolderImpl> scoreHolder = globalOf(HardSoftLongScoreHolderImpl.class, "", SCORE_HOLDER_GLOBAL);
        Model model = new ModelImpl()
                .addGlobal(scoreHolder)
                .addRule(ruleFunction.apply(scoreHolder));
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model, kieBaseConfiguration);
        KieSession kieSession = kieBase.newKieSession();
        kieSession.setGlobal(SCORE_HOLDER_GLOBAL, new HardSoftLongScoreHolderImpl(true));
        return kieSession;
    }
}
