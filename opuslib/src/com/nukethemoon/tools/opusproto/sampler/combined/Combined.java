package com.nukethemoon.tools.opusproto.sampler.combined;

import com.nukethemoon.tools.opusproto.sampler.Samplers;
import com.nukethemoon.tools.opusproto.exceptions.SamplerInvalidConfigException;
import com.nukethemoon.tools.opusproto.noise.Algorithms;
import com.nukethemoon.tools.opusproto.region.ChunkRequestBuffer;
import com.nukethemoon.tools.opusproto.sampler.AbstractSampler;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;
import com.nukethemoon.tools.opusproto.sampler.ChildSamplerConfig;

import java.math.BigInteger;

public class Combined extends AbstractSampler {

	private CombinedConfig combinedConfig;
	private AbstractSampler[] samplers;

	public Combined(AbstractSamplerConfiguration config, double worldSeed, Algorithms noisePool, Samplers samplers) throws SamplerInvalidConfigException {
		super(config, worldSeed, noisePool, samplers);
		combinedConfig = (CombinedConfig) config;
		init();
	}

	@Override
	public void loadConfig() {
		samplers = new AbstractSampler[combinedConfig.samplerItems.length];
		for (int i = 0; i < combinedConfig.samplerItems.length; i++) {
			ChildSamplerConfig samplerItem = combinedConfig.samplerItems[i];
			AbstractSampler sampler = samplerLoader.getSampler(samplerItem.samplerReferenceId);
			samplers[i] = sampler;
		}
	}

	@Override
	protected void compute(float[][] data) {

	}

	public static void applyOperator(float[][] values, float[][] data, ChildSamplerConfig.Operator operator) {
		for (int x = 0; x < data.length; x++) {
			for (int y = 0; y < data[x].length; y++) {
				values[x][y] = applyOperator(values[x][y], data[x][y], operator);
			}
		}
	}

	/**
	 * Applies a operation to combine to values.
	 * @param value01 The first value.
	 * @param value02 The second value.
	 * @param operator The operator to combine.
	 * @return The combined value.
	 */
	public static float applyOperator(float value01, float value02, ChildSamplerConfig.Operator operator) {
		if (operator == ChildSamplerConfig.Operator.Plus) {
			return value01 + value02;
		}
		if (operator == ChildSamplerConfig.Operator.Minus) {
			return value01 - value02;
		}
		if (operator == ChildSamplerConfig.Operator.Mix) {
			return (value01 + value02) / 2f;
		}
		if (operator == ChildSamplerConfig.Operator.And) {
			if (value01 > 0) {
				return value02;
			}
		}
		if (operator == ChildSamplerConfig.Operator.Or) {
			return or(value01, value02);
		}
		if (operator == ChildSamplerConfig.Operator.Highest) {
			return value01 < value02 ? value02 : value01;
		}
		if (operator == ChildSamplerConfig.Operator.Lowest) {
			return value01 > value02 ? value02 : value01;
		}
		return 0;
	}




	public static float or(float value01, float value02) {
		if (value01 == 0) {
			return value02;
		}
		if (value02 == 0) {
			return value01;
		}
		return (value01 + value02) / 2f;
	}

	@Override
	public float[][] bufferedCreateValues(float x, float y,
										  int size,
										  float scaleFactor, double seedModifier,
										  ChunkRequestBuffer buffer) {

		float[][][] tmpSampleData = new float[samplers.length][][];

		float modifiedScale = scaleFactor * config.scale;

		float[][] data = new float[size][size];

		for (int i = 0; i < samplers.length; i++) {
			if (i < combinedConfig.samplerItems.length) {
				if (combinedConfig.samplerItems[i].active) {

					float tmpScale = combinedConfig.samplerItems[i].scaleModifier * modifiedScale;
					double tmpSeed = getModifiedSeed(getSeedOfChild(i), seedModifier);

					tmpSampleData[i] = samplers[i].createValues(
							x, y,
							size,
							tmpScale,
							tmpSeed,
							buffer);
					if (combinedConfig.samplerItems[i].invert) {
						AbstractSampler.invert(tmpSampleData[i]);
					}
					combine(tmpSampleData[i], combinedConfig.samplerItems[i].multiply);
					Combined.applyOperator(data, tmpSampleData[i], combinedConfig.samplerItems[i].operator);
				}
			}
		}
		return data;
	}

	private void combine(float[][] data, float multiply) {
		for (int x = 0; x < data.length; x++) {
			for (int y = 0; y < data[x].length; y++) {
				data[x][y] = data[x][y] * multiply;
			}
		}
	}

	private double getSeedOfChild(int index) {
		return getModifiedSeed(getContainingSeed(), combinedConfig.samplerItems[index].seedModifier);
	}


	@Override
	public float getMaxSample() {
		return calcCombination(true);
	}

	@Override
	public float getMinSample() {
		return calcCombination(false);
	}

	/**
	 * Calculates all possible combinations of the samplers
	 * to get the maximum or minimum sample.
	 * @param forMaximum Searches the maximum value if true.
	 * @return The maximum or minimum sample.
	 */
	private float calcCombination(boolean forMaximum) {
		float[] maxSamples = generateSamples(true);
		float[] minSamples = generateSamples(false);

		int combinations = (int) Math.pow(combinedConfig.samplerItems.length, 2) + 1;

		Float returnValue = null;
		for (int combinationIndex = 0; combinationIndex < combinations; combinationIndex++) {
			float combinationValue = 0;
			for (int operationIndex = 0; operationIndex < combinedConfig.samplerItems.length; operationIndex++) {
				boolean bitSet = BigInteger.valueOf(combinationIndex).testBit(operationIndex);
				float value;
				if (bitSet) {
					value = maxSamples[operationIndex];
				} else {
					value = minSamples[operationIndex];
				}
				combinationValue = Combined.applyOperator(combinationValue, value,
						combinedConfig.samplerItems[operationIndex].operator);
			}
			if (forMaximum && (returnValue == null || returnValue < combinationValue)) {
				returnValue = combinationValue;
			}
			if (!forMaximum && (returnValue == null || returnValue > combinationValue)) {
				returnValue = combinationValue;
			}
		}
		if (returnValue == null) {
			return 0;
		}
		return returnValue;
	}

	/**
	 * Creates an array of values of every sampler. Takes the maximum or minimum calues.
	 * @param forMaximum True for maximum values.
	 * @return An array of the values of this sampler.
	 */
	private float[] generateSamples(boolean forMaximum) {
		float[] samples = new float[combinedConfig.samplerItems.length];
		for (int i = 0; i < combinedConfig.samplerItems.length; i++) {
			ChildSamplerConfig c = combinedConfig.samplerItems[i];
			AbstractSampler sampler = samplerLoader.getSampler(c.samplerReferenceId);
			if (forMaximum) {
				samples[i] = sampler.modify(sampler.getMaxSample());
			} else {
				samples[i] = sampler.modify(sampler.getMinSample());
			}
		}
		return samples;
	}

}
