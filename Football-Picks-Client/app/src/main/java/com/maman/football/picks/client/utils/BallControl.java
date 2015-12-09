package com.maman.football.picks.client.utils;

import android.util.Pair;
import android.util.SparseArray;

import com.maman.football.picks.client.FootballRenderer;

import java.util.ArrayList;
import java.util.List;

public class BallControl {

    public static List<Pair<Float,Float>> getPositionList(int numGames) {
        return ballPositionMap.get(numGames-1);
    }
    public static float getZposition(int numGames) {
        if (numGames == 1) {
            return -4f;
        }
        else if (numGames < 5) {
            return -5;
        }
        return -8f;
    }
    public static int getSpinRate(int numGames, int multiplier) {
        if (numGames < 7) {
            return (multiplier * 5);
        }
        else if (numGames < 10) {
            return 5 + (multiplier * 5);
        }
        else {
            return 10 + (multiplier * 5);
        }
    }

    private static final float x1of2 = -0.75f;
    private static final float x2of2 =  0.75f;
    private static final float y1of2 =  1f;
    private static final float y2of2 = -1f;

    private static final float x1of3 = -1.5f;
    private static final float x2of3 =  0f;
    private static final float x3of3 =  1.5f;
    private static final float y1of3 =  2f;
    private static final float y2of3 =  0f;
    private static final float y3of3 = -2f;

    private static final float x1of4 = -1.5f;
    private static final float x2of4 = -0.5f;
    private static final float x3of4 =  0.5f;
    private static final float x4of4 =  1.5f;
    private static final float y1of4 =  2.25f;
    private static final float y2of4 =  0.75f;
    private static final float y3of4 = -0.75f;
    private static final float y4of4 = -2.25f;


    public static final SparseArray<List<Pair<Float,Float>>> ballPositionMap
            = new SparseArray<>(FootballRenderer.MAX_NUM_GAMES);

    static {
        for (int i = 0; i < FootballRenderer.MAX_NUM_GAMES; i++) {
            ballPositionMap.append(i, new ArrayList<Pair<Float, Float>>());
        }
        List<Pair<Float,Float>> ballPositionList;
        
        // 1 in the middle
        ballPositionList = ballPositionMap.get(0);
        ballPositionList.add(new Pair<>(0f, 0f));

        // 1 row of 2
        ballPositionList = ballPositionMap.get(1);
        ballPositionList.add(new Pair<>(x1of2, 0f));
        ballPositionList.add(new Pair<>(x2of2, 0f));

        // 1 row of 2, 1 row of 1
        ballPositionList = ballPositionMap.get(2);
        ballPositionList.add(new Pair<>(x1of2, y1of2));
        ballPositionList.add(new Pair<>(x2of2, y1of2));
        ballPositionList.add(new Pair<>(   0f, y2of2));

        // 2 rows of 2
        ballPositionList = ballPositionMap.get(3);
        ballPositionList.add(new Pair<>(x1of2, y1of2));
        ballPositionList.add(new Pair<>(x2of2, y1of2));
        ballPositionList.add(new Pair<>(x1of2, y2of2));
        ballPositionList.add(new Pair<>(x2of2, y2of2));

        // 1 row of 2, 1 row of 3
        ballPositionList = ballPositionMap.get(4);
        ballPositionList.add(new Pair<>(x1of2, y1of2));
        ballPositionList.add(new Pair<>(x2of2, y1of2));
        ballPositionList.add(new Pair<>(x1of3, y2of2));
        ballPositionList.add(new Pair<>(x2of3, y2of2));
        ballPositionList.add(new Pair<>(x3of3, y2of2));

        // 2 rows of 3
        ballPositionList = ballPositionMap.get(5);
        ballPositionList.add(new Pair<>(x1of3, y1of2));
        ballPositionList.add(new Pair<>(x2of3, y1of2));
        ballPositionList.add(new Pair<>(x3of3, y1of2));
        ballPositionList.add(new Pair<>(x1of3, y2of2));
        ballPositionList.add(new Pair<>(x2of3, y2of2));
        ballPositionList.add(new Pair<>(x3of3, y2of2));

        // 2 rows of 2, 1 row of 3
        ballPositionList = ballPositionMap.get(6);
        ballPositionList.add(new Pair<>(x1of2, y1of3));
        ballPositionList.add(new Pair<>(x2of2, y1of3));
        ballPositionList.add(new Pair<>(x1of2, y2of3));
        ballPositionList.add(new Pair<>(x2of2, y2of3));
        ballPositionList.add(new Pair<>(x1of3, y3of3));
        ballPositionList.add(new Pair<>(x2of3, y3of3));
        ballPositionList.add(new Pair<>(x3of3, y3of3));

        // 1 row of 2, 2 rows of 3
        ballPositionList = ballPositionMap.get(7);
        ballPositionList.add(new Pair<>(x1of2, y1of3));
        ballPositionList.add(new Pair<>(x2of2, y1of3));
        ballPositionList.add(new Pair<>(x1of3, y2of3));
        ballPositionList.add(new Pair<>(x2of3, y2of3));
        ballPositionList.add(new Pair<>(x3of3, y2of3));
        ballPositionList.add(new Pair<>(x1of3, y3of3));
        ballPositionList.add(new Pair<>(x2of3, y3of3));
        ballPositionList.add(new Pair<>(x3of3, y3of3));

        // 3 rows of 3
        ballPositionList = ballPositionMap.get(8);
        ballPositionList.add(new Pair<>(x1of3, y1of3));
        ballPositionList.add(new Pair<>(x2of3, y1of3));
        ballPositionList.add(new Pair<>(x3of3, y1of3));
        ballPositionList.add(new Pair<>(x1of3, y2of3));
        ballPositionList.add(new Pair<>(x2of3, y2of3));
        ballPositionList.add(new Pair<>(x3of3, y2of3));
        ballPositionList.add(new Pair<>(x1of3, y3of3));
        ballPositionList.add(new Pair<>(x2of3, y3of3));
        ballPositionList.add(new Pair<>(x3of3, y3of3));

        // 2 rows of 3, 1 row of 4
        ballPositionList = ballPositionMap.get(9);
        ballPositionList.add(new Pair<>(x1of3, y1of3));
        ballPositionList.add(new Pair<>(x2of3, y1of3));
        ballPositionList.add(new Pair<>(x3of3, y1of3));
        ballPositionList.add(new Pair<>(x1of3, y2of3));
        ballPositionList.add(new Pair<>(x2of3, y2of3));
        ballPositionList.add(new Pair<>(x3of3, y2of3));
        ballPositionList.add(new Pair<>(x1of4, y3of3));
        ballPositionList.add(new Pair<>(x2of4, y3of3));
        ballPositionList.add(new Pair<>(x3of4, y3of3));
        ballPositionList.add(new Pair<>(x4of4, y3of3));

        // 1 row of 3, 2 rows of 4
        ballPositionList = ballPositionMap.get(10);
        ballPositionList.add(new Pair<>(x1of3, y1of3));
        ballPositionList.add(new Pair<>(x2of3, y1of3));
        ballPositionList.add(new Pair<>(x3of3, y1of3));
        ballPositionList.add(new Pair<>(x1of4, y2of3));
        ballPositionList.add(new Pair<>(x2of4, y2of3));
        ballPositionList.add(new Pair<>(x3of4, y2of3));
        ballPositionList.add(new Pair<>(x4of4, y2of3));
        ballPositionList.add(new Pair<>(x1of4, y3of3));
        ballPositionList.add(new Pair<>(x2of4, y3of3));
        ballPositionList.add(new Pair<>(x3of4, y3of3));
        ballPositionList.add(new Pair<>(x4of4, y3of3));

        // 4 rows of 3
        ballPositionList = ballPositionMap.get(11);
        ballPositionList.add(new Pair<>(x1of3, y1of4));
        ballPositionList.add(new Pair<>(x2of3, y1of4));
        ballPositionList.add(new Pair<>(x3of3, y1of4));
        ballPositionList.add(new Pair<>(x1of3, y2of4));
        ballPositionList.add(new Pair<>(x2of3, y2of4));
        ballPositionList.add(new Pair<>(x3of3, y2of4));
        ballPositionList.add(new Pair<>(x1of3, y3of4));
        ballPositionList.add(new Pair<>(x2of3, y3of4));
        ballPositionList.add(new Pair<>(x3of3, y3of4));
        ballPositionList.add(new Pair<>(x1of3, y4of4));
        ballPositionList.add(new Pair<>(x2of3, y4of4));
        ballPositionList.add(new Pair<>(x3of3, y4of4));

        // 3 rows of 3, 1 row of 4
        ballPositionList = ballPositionMap.get(12);
        ballPositionList.add(new Pair<>(x1of3, y1of4));
        ballPositionList.add(new Pair<>(x2of3, y1of4));
        ballPositionList.add(new Pair<>(x3of3, y1of4));
        ballPositionList.add(new Pair<>(x1of3, y2of4));
        ballPositionList.add(new Pair<>(x2of3, y2of4));
        ballPositionList.add(new Pair<>(x3of3, y2of4));
        ballPositionList.add(new Pair<>(x1of3, y3of4));
        ballPositionList.add(new Pair<>(x2of3, y3of4));
        ballPositionList.add(new Pair<>(x3of3, y3of4));
        ballPositionList.add(new Pair<>(x1of4, y4of4));
        ballPositionList.add(new Pair<>(x2of4, y4of4));
        ballPositionList.add(new Pair<>(x3of4, y4of4));
        ballPositionList.add(new Pair<>(x4of4, y4of4));

        // 2 rows of 3, 2 rows of 4
        ballPositionList = ballPositionMap.get(13);
        ballPositionList.add(new Pair<>(x1of3, y1of4));
        ballPositionList.add(new Pair<>(x2of3, y1of4));
        ballPositionList.add(new Pair<>(x3of3, y1of4));
        ballPositionList.add(new Pair<>(x1of3, y2of4));
        ballPositionList.add(new Pair<>(x2of3, y2of4));
        ballPositionList.add(new Pair<>(x3of3, y2of4));
        ballPositionList.add(new Pair<>(x1of4, y3of4));
        ballPositionList.add(new Pair<>(x2of4, y3of4));
        ballPositionList.add(new Pair<>(x3of4, y3of4));
        ballPositionList.add(new Pair<>(x4of4, y3of4));
        ballPositionList.add(new Pair<>(x1of4, y4of4));
        ballPositionList.add(new Pair<>(x2of4, y4of4));
        ballPositionList.add(new Pair<>(x3of4, y4of4));
        ballPositionList.add(new Pair<>(x4of4, y4of4));

        // 1 row of 3, 3 rows of 4
        ballPositionList = ballPositionMap.get(14);
        ballPositionList.add(new Pair<>(x1of3, y1of4));
        ballPositionList.add(new Pair<>(x2of3, y1of4));
        ballPositionList.add(new Pair<>(x3of3, y1of4));
        ballPositionList.add(new Pair<>(x1of4, y2of4));
        ballPositionList.add(new Pair<>(x2of4, y2of4));
        ballPositionList.add(new Pair<>(x3of4, y2of4));
        ballPositionList.add(new Pair<>(x4of4, y2of4));
        ballPositionList.add(new Pair<>(x1of4, y3of4));
        ballPositionList.add(new Pair<>(x2of4, y3of4));
        ballPositionList.add(new Pair<>(x3of4, y3of4));
        ballPositionList.add(new Pair<>(x4of4, y3of4));
        ballPositionList.add(new Pair<>(x1of4, y4of4));
        ballPositionList.add(new Pair<>(x2of4, y4of4));
        ballPositionList.add(new Pair<>(x3of4, y4of4));
        ballPositionList.add(new Pair<>(x4of4, y4of4));

        // 4 rows of 4
        ballPositionList = ballPositionMap.get(15);
        ballPositionList.add(new Pair<>(x1of4, y1of4));
        ballPositionList.add(new Pair<>(x2of4, y1of4));
        ballPositionList.add(new Pair<>(x3of4, y1of4));
        ballPositionList.add(new Pair<>(x4of4, y1of4));
        ballPositionList.add(new Pair<>(x1of4, y2of4));
        ballPositionList.add(new Pair<>(x2of4, y2of4));
        ballPositionList.add(new Pair<>(x3of4, y2of4));
        ballPositionList.add(new Pair<>(x4of4, y2of4));
        ballPositionList.add(new Pair<>(x1of4, y3of4));
        ballPositionList.add(new Pair<>(x2of4, y3of4));
        ballPositionList.add(new Pair<>(x3of4, y3of4));
        ballPositionList.add(new Pair<>(x4of4, y3of4));
        ballPositionList.add(new Pair<>(x1of4, y4of4));
        ballPositionList.add(new Pair<>(x2of4, y4of4));
        ballPositionList.add(new Pair<>(x3of4, y4of4));
        ballPositionList.add(new Pair<>(x4of4, y4of4));
    }
}
