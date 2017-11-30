package com.bjghhnt.app.treatmentdevice.utils;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class provides a random combination of a variety of fixed wave forms.
 * Created by Q on 18/01/2016.
 */
public class Waves {

	private static final int LENGTH_ONE_PERIOD = 8;

	private static final int NUMBER_OF_KINDS = 8;

	private static final int[][] ALL_WAVES = {
			{0, 0, 0, 0, 1, 0, 0, 0},
			{0, 0, 0, 0, -1, 0, 0, 0},
			{0, 0, 0, 1, -1, 0, 0, 0},
			{0, 0, 0, -1, 1, 0, 0, 0},
			{0, 0, 1, 0, 0, 0, 1, 0},
			{0, 0, -1, 0, 0, 0, -1, 0},
			{0, 0, 0, 0, 0, 1, -1, 0},
			{0, -1, 1, 0, 0, 0, 0, 0}
	};

	private static final int[] sOrder = {0, 1, 2, 3, 4, 5, 6, 7};

	// shuffle the order of appearance
	private static void shuffleArray() {
		Random rnd = new Random();
		for (int i = sOrder.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			// Simple swap
			int a = sOrder[index];
			sOrder[index] = sOrder[i];
			sOrder[i] = a;
		}
	}

	public static ArrayList<Integer> createWaves() {
		int[] waves = new int[LENGTH_ONE_PERIOD * NUMBER_OF_KINDS];
		//shuffle the order of appearance
		shuffleArray();
		//concatenate
		for (int i = 0; i < sOrder.length; i++) {
			System.arraycopy(ALL_WAVES[sOrder[i]], 0, waves, i * LENGTH_ONE_PERIOD, LENGTH_ONE_PERIOD);
		}
		//return as an array list
		ArrayList<Integer> result = new ArrayList<>(LENGTH_ONE_PERIOD * NUMBER_OF_KINDS);
		for (int i : waves) {
			result.add(i);
		}
		return result;
	}

	public static void shiftToLeft(ArrayList<Integer> waves) {
		waves.add(waves.remove(0));

	}
}
