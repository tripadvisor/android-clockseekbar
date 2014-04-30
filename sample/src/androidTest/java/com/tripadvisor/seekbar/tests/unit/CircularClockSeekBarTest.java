package com.tripadvisor.seekbar.tests.unit;

import android.test.InstrumentationTestCase;

import static com.tripadvisor.seekbar.CircularClockSeekBar.getDelta;
import static com.tripadvisor.seekbar.CircularClockSeekBar.getDistanceTo;
import static com.tripadvisor.seekbar.CircularClockSeekBar.shouldMoveClockwise;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by ksarmalkar on 4/30/14.
 */
public class CircularClockSeekBarTest extends InstrumentationTestCase {

    public void testThatIsClockwiseReturnsCorrectValues() throws Exception {
        assertThat(shouldMoveClockwise(80, 170)).isTrue();
        assertThat(shouldMoveClockwise(170, 80)).isFalse();
        assertThat(shouldMoveClockwise(358, 3)).isTrue();
        assertThat(shouldMoveClockwise(3, 358)).isFalse();
        assertThat(shouldMoveClockwise(160, 190)).isTrue();
        assertThat(shouldMoveClockwise(190, 160)).isFalse();
        assertThat(shouldMoveClockwise(315, 45)).isTrue();
        assertThat(shouldMoveClockwise(45, 315)).isFalse();
        assertThat(shouldMoveClockwise(89, 268)).isTrue();
        assertThat(shouldMoveClockwise(268, 89)).isFalse();
        assertThat(shouldMoveClockwise(92, 271)).isTrue();
        assertThat(shouldMoveClockwise(271, 92)).isFalse();
        assertThat(shouldMoveClockwise(3, 181)).isTrue();
        assertThat(shouldMoveClockwise(181, 3)).isFalse();
    }

    public void testThatCircularDistanceReturnTheCorrectValues() throws Exception {
        assertThat(getDistanceTo(80, 170)).isEqualTo(90);
        assertThat(getDistanceTo(170, 80)).isEqualTo(-90);
        assertThat(getDistanceTo(358, 3)).isEqualTo(5);
        assertThat(getDistanceTo(3, 358)).isEqualTo(-5);
        assertThat(getDistanceTo(160, 190)).isEqualTo(30);
        assertThat(getDistanceTo(190, 160)).isEqualTo(-30);
        assertThat(getDistanceTo(315, 45)).isEqualTo(90);
        assertThat(getDistanceTo(45, 315)).isEqualTo(-90);
        assertThat(getDistanceTo(89, 268)).isEqualTo(179);
        assertThat(getDistanceTo(268, 89)).isEqualTo(-179);
        assertThat(getDistanceTo(92, 271)).isEqualTo(179);
        assertThat(getDistanceTo(271, 92)).isEqualTo(-179);
        assertThat(getDistanceTo(3, 181)).isEqualTo(178);
        assertThat(getDistanceTo(181, 3)).isEqualTo(-178);
    }

    public void testThatCalculateNewDeltaReturnCorrectValues() throws Exception {
        assertThat(getDelta(0, 49)).isEqualTo(0);
        assertThat(getDelta(40, 0)).isEqualTo(0);
        assertThat(getDelta(0, 1)).isEqualTo(0);
        assertThat(getDelta(1, 0)).isEqualTo(0);
        assertThat(getDelta(0, 99)).isEqualTo(0);
        assertThat(getDelta(80, 170)).isEqualTo(90);
        assertThat(getDelta(170, 80)).isEqualTo(-90);
        assertThat(getDelta(358, 3)).isEqualTo(5);
        assertThat(getDelta(3, 358)).isEqualTo(-5);
        assertThat(getDelta(160, 190)).isEqualTo(30);
        assertThat(getDelta(190, 160)).isEqualTo(-30);
        assertThat(getDelta(315, 45)).isEqualTo(90);
        assertThat(getDelta(45, 315)).isEqualTo(-90);
        assertThat(getDelta(89, 268)).isEqualTo(179);
        assertThat(getDelta(268, 89)).isEqualTo(-179);
        assertThat(getDelta(92, 271)).isEqualTo(179);
        assertThat(getDelta(271, 92)).isEqualTo(-179);
        assertThat(getDelta(3, 181)).isEqualTo(178);
        assertThat(getDelta(181, 3)).isEqualTo(-178);
    }
}
