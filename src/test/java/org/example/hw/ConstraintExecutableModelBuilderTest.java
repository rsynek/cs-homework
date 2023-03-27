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
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import org.optaplanner.constraint.drl.holder.HardSoftLongScoreHolderImpl;

class ConstraintExecutableModelBuilderTest {

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

        kieSession.setGlobal("scoreHolder", new HardSoftLongScoreHolderImpl(true));
        kieSession.insert(busHub);
        kieSession.insert(bus);
        kieSession.insert(busStop);

        kieSession.fireAllRules();

        HardSoftLongScoreHolderImpl scoreHolder = (HardSoftLongScoreHolderImpl) kieSession.getGlobal("scoreHolder");
        assertThat(scoreHolder.getHardScore()).isEqualTo(-50);
    }


    private KieSession buildKieSession(Function<Global<HardSoftLongScoreHolderImpl>, Rule> ruleFunction) {
        KieBaseConfiguration kieBaseConfiguration = KieServices.Factory.get().newKieBaseConfiguration();
        Global<HardSoftLongScoreHolderImpl> scoreHolder = globalOf(HardSoftLongScoreHolderImpl.class, "", "scoreHolder");
        Model model = new ModelImpl()
                .addGlobal(scoreHolder)
                .addRule(ruleFunction.apply(scoreHolder));
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model, kieBaseConfiguration);
        return kieBase.newKieSession();
    }
}
