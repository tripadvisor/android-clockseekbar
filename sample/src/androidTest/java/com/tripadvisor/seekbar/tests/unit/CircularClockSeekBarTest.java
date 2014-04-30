package com.tripadvisor.seekbar.tests.unit;

import android.test.InstrumentationTestCase;

import static com.tripadvisor.seekbar.CircularClockSeekBar.calculateNewDelta;
import static com.tripadvisor.seekbar.CircularClockSeekBar.getCircularDistance;
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
        assertThat(getCircularDistance(80, 170)).isEqualTo(90);
        assertThat(getCircularDistance(170, 80)).isEqualTo(-90);
        assertThat(getCircularDistance(358, 3)).isEqualTo(5);
        assertThat(getCircularDistance(3, 358)).isEqualTo(-5);
        assertThat(getCircularDistance(160, 190)).isEqualTo(30);
        assertThat(getCircularDistance(190, 160)).isEqualTo(-30);
        assertThat(getCircularDistance(315, 45)).isEqualTo(90);
        assertThat(getCircularDistance(45, 315)).isEqualTo(-90);
        assertThat(getCircularDistance(89, 268)).isEqualTo(179);
        assertThat(getCircularDistance(268, 89)).isEqualTo(-179);
        assertThat(getCircularDistance(92, 271)).isEqualTo(179);
        assertThat(getCircularDistance(271, 92)).isEqualTo(-179);
        assertThat(getCircularDistance(3, 181)).isEqualTo(178);
        assertThat(getCircularDistance(181, 3)).isEqualTo(-178);
    }

    public void testThatCalculateNewDeltaReturnCorrectValues() throws Exception {
        assertThat(calculateNewDelta(0, 49)).isEqualTo(0);
        assertThat(calculateNewDelta(40, 0)).isEqualTo(0);
        assertThat(calculateNewDelta(0, 1)).isEqualTo(0);
        assertThat(calculateNewDelta(1, 0)).isEqualTo(0);
        assertThat(calculateNewDelta(0, 99)).isEqualTo(0);
        assertThat(calculateNewDelta(80, 170)).isEqualTo(90);
        assertThat(calculateNewDelta(170, 80)).isEqualTo(-90);
        assertThat(calculateNewDelta(358, 3)).isEqualTo(5);
        assertThat(calculateNewDelta(3, 358)).isEqualTo(-5);
        assertThat(calculateNewDelta(160, 190)).isEqualTo(30);
        assertThat(calculateNewDelta(190, 160)).isEqualTo(-30);
        assertThat(calculateNewDelta(315, 45)).isEqualTo(90);
        assertThat(calculateNewDelta(45, 315)).isEqualTo(-90);
        assertThat(calculateNewDelta(89, 268)).isEqualTo(179);
        assertThat(calculateNewDelta(268, 89)).isEqualTo(-179);
        assertThat(calculateNewDelta(92, 271)).isEqualTo(179);
        assertThat(calculateNewDelta(271, 92)).isEqualTo(-179);
        assertThat(calculateNewDelta(3, 181)).isEqualTo(178);
        assertThat(calculateNewDelta(181, 3)).isEqualTo(-178);
    }
}
