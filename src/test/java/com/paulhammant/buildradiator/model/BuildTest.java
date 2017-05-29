package com.paulhammant.buildradiator.model;

import org.junit.Test;

import static com.paulhammant.buildradiator.model.TestRadBuilder.build;
import static com.paulhammant.buildradiator.model.TestRadBuilder.step;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class BuildTest {

    @Test
    public void cancellationShouldNotAffectStepThatHasPassed() {
        Build b = build("1", "running", step("A", 100, "passed"), step("B", 0, "running"));
        b.cancel();
        assertThat(b.status, equalTo("cancelled"));
        assertThat(b.steps.get(0).status, equalTo("passed"));
        assertThat(b.steps.get(1).status, equalTo("cancelled"));
    }

    @Test(expected = WrongBuildState.class)
    public void cancellationCannotHappenTwice() {
        Build b = build("1", "running", step("A", 100, "passed"), step("B", 0, "running"));
        b.cancel();
        b.cancel();
    }

    @Test(expected = WrongBuildState.class)
    public void cancellationCannotHappenOnPassedBuild() {
        Build b = build("1", "passed", step("A", 100, "passed"), step("B", 200, "passed"));
        b.cancel();
    }

    @Test(expected = WrongBuildState.class)
    public void cancellationCannotHappenOnFailedBuild() {
        Build b = build("1", "failed", step("A", 100, "passed"), step("B", 200, "failed"));
        b.cancel();
    }

    @Test()
    public void startShouldNotHappenToRunningFailedOrPassedStep() {
        Build b = build("1", "running", step("A", 100, "passed"), step("B", 0, "running"));
        startThrowsWrongBuildState(b, "A");
        startThrowsWrongBuildState(b, "B");
        b = build("1", "failed", step("A", 100, "failed"), step("B", 0, ""));
        startThrowsWrongBuildState(b, "A");
        b = build("1", "passed", step("A", 100, "passed"), step("B", 0, "passed"));
        startThrowsWrongBuildState(b, "A");
    }

    @Test()
    public void passShouldNotHappenToFailedOrPassedStep() {
        Build b = build("1", "running", step("A", 100, "passed"), step("B", 0, "running"));
        passThrowsWrongBuildState(b, "A");
        b = build("1", "failed", step("A", 100, "failed"), step("B", 0, ""));
        passThrowsWrongBuildState(b, "A");
        passThrowsWrongBuildState(b, "B");
    }

    @Test()
    public void failShouldNotHappenToFailedOrPassedStep() {
        Build b = build("1", "running", step("A", 100, "passed"));
        failThrowsWrongBuildState(b, "A");
        b = build("1", "failed", step("A", 100, "failed"));
        failThrowsWrongBuildState(b, "A");
        failThrowsWrongBuildState(b, "B");
    }

    private void startThrowsWrongBuildState(Build b, String step) {
        try {
            b.start(step);
            fail();
        } catch (WrongBuildState e) {
        }
    }

    private void passThrowsWrongBuildState(Build b, String step) {
        try {
            b.pass(step);
            fail();
        } catch (WrongBuildState e) {
        }
    }

    private void failThrowsWrongBuildState(Build b, String step) {
        try {
            b.pass(step);
            fail();
        } catch (WrongBuildState e) {
        }
    }

}
