package com.nukethemoon.tools.opusproto.sampler;

import com.nukethemoon.tools.opusproto.Samplers;
import com.nukethemoon.tools.opusproto.exceptions.SamplerInvalidConfigException;
import com.nukethemoon.tools.opusproto.noise.Algorithms;
import com.nukethemoon.tools.opusproto.region.ChunkRequestBuffer;

public abstract class AbstractSampler implements ISampleable {

	protected AbstractSamplerConfiguration config;

	protected double samplerSeed;
	protected double worldSeed;
	protected Algorithms noisePool;
	protected Samplers samplerLoader;

	public boolean active = true;

	public AbstractSampler(AbstractSamplerConfiguration config, double worldSeed, Algorithms noisePool, Samplers samplers) {
		this.samplerLoader = samplers;
		this.noisePool = noisePool;
		this.config = config;
		this.worldSeed = worldSeed;
	}

	/*@Override
	public float getValueAt(float x, float y, double seed, SamplerDataBuffer buffer) {
		float sample = sample((x / config.scale), (y / config.scale), seed, buffer);
		float computed = compute(sample);
		return modify(computed);
	}*/

	public void init() throws SamplerInvalidConfigException {
		updateSeed(config.worldSeedModifier);
		loadConfig();
	}

	protected abstract float[][] bufferedCreateValues(float x, float y,
													  int size,
													  float scaleFactor, double seedModifier,
													  ChunkRequestBuffer buffer);

	public float[][] createValues(float x, float y,
								  int size,
								  float scaleFactor, double seedModifier,
								  ChunkRequestBuffer buffer) {

		if (buffer != null) {
			float[][] bufferedData = buffer.getSamplerData(getConfig().id, seedModifier, scaleFactor);
			if (bufferedData != null) {
				return bufferedData;
			}
		}

		float[][] data = bufferedCreateValues(x, y, size, scaleFactor, seedModifier, buffer);
		compute(data);
		modify(data);
		if (buffer != null) {
			buffer.addSamplerData(getConfig().id, seedModifier, scaleFactor, data);
		}
		return data;
	}


	protected abstract void loadConfig() throws SamplerInvalidConfigException;

	protected abstract void compute(float[][] data);

	public void modify(float[][] data) {
		if (config.modifiers != null) {
			for (int x = 0; x < data.length; x++) {
				for (int y = 0; y < data[x].length; y++) {
					data[x][y] = modify(data[x][y]);
				}
			}
		}
	}

	public float modify(float value) {
		if (config.modifiers != null) {
			for (SamplerModifier modifier : config.modifiers) {
				value = calculate(value, modifier.type, modifier.value);
			}
		}
		return value;
	}

	public void multiply(float[][] data, float value) {
		for (int x = 0; x < data.length; x++) {
			for (int y = 0; y < data[x].length; y++) {
				data[x][y] = data[x][y] * value;
			}
		}
	}

	public void multiply(float[][] data, float[][] factors) {
		for (int x = 0; x < data.length; x++) {
			for (int y = 0; y < data[x].length; y++) {
				data[x][y] = data[x][y] * factors[x][y];
			}
		}
	}

	public abstract float getMaxSample();

	public abstract float getMinSample();

	public void updateSeed(double seedMod) {
		samplerSeed = getModifiedSeed(worldSeed, seedMod);
	}

	private float calculate(float value, SamplerModifier.Type modifierType, float modification) {
		if (modifierType.equals(SamplerModifier.Type.Max)) {
			return Math.max(value, modification);
		}
		if (modifierType.equals(SamplerModifier.Type.Min)) {
			return Math.min(value, modification);
		}
		if (modifierType.equals(SamplerModifier.Type.Multiply)) {
			return value * modification;
		}
		if (modifierType.equals(SamplerModifier.Type.Add)) {
			return value + modification;
		}
		if (modifierType.equals(SamplerModifier.Type.Step)) {
			return step(value, modification);
		}
		if (modifierType.equals(SamplerModifier.Type.Limit)) {
			return limit(value);
		}
		if (modifierType.equals(SamplerModifier.Type.Sharpen)) {
			return sharpen(value, modification);
		}
		if (modifierType.equals(SamplerModifier.Type.Pow)) {
			return pow(value, modification);
		}
		if (modifierType.equals(SamplerModifier.Type.Sin)) {
			return sin(value, modification);
		}
		if (modifierType.equals(SamplerModifier.Type.HigherThan)) {
			if (value > modification) {
				return value;
			} else {
				return 0;
			}
		}
		if (modifierType.equals(SamplerModifier.Type.LowerThan)) {
			if (value < modification) {
				return value;
			} else {
				return 0;
			}
		}
		if (modifierType.equals(SamplerModifier.Type.Invert)) {
			return invert(value);
		}

		return value;
	}

	public static void invert(float[][] data) {
		for (int x = 0; x < data.length; x++) {
			for (int y = 0; y < data[x].length; y++) {
				data[x][y] = invert(data[x][y]);
			}
		}
	}

	public static float invert(float value) {
		return 1f - value;
	}


	public static float sharpen(float value, float modifier) {
		float distance = value - 0.5f;
		value = (float) Math.tanh(distance * modifier);
		return  (value + 1) / 2;
	}


	public static float sin(float value, float modifier) {
		return (float) (Math.sin(value * Math.PI * modifier) + 1f) / 2f;
	}

	public static float pow(float value, float modifier) {
		return (float) Math.pow(value, modifier);
	}

	/**
	 * Limits the assigned value to the interval 0, 1
	 * @param pValue The value to limit.
	 * @return The limited value.
	 */
	public static float limit(float pValue) {
		float cutTop = Math.min(pValue, 1f);
		return Math.max(cutTop, 0f);
	}

	/**
	 * Rounds the assigned value to a value between 0, 1
	 * with the assigned steps.
	 * @param value The value to step.
	 * @param steps The steps to round.
	 * @return The rounded value.
	 */
	public static float step(float value, float steps) {
		float singleStep = 1f / steps;
		float i = 0;
		while (i * singleStep < value) {
			i = i + 1;
		}
		return i * singleStep;
	}

	public static AbstractSampler[] create(AbstractSamplerConfiguration[] configs, double seed, Algorithms pool, Samplers samplerLoader) {
		AbstractSampler[] samplers = new AbstractSampler[configs.length];
		for (int i = 0; i < configs.length; i++) {
			samplers[i] = Samplers.create(configs[i], seed, pool, samplerLoader);
		}
		return samplers;
	}


	public AbstractSamplerConfiguration getConfig() {
		return config;
	}

	public double getModifiedSeed(double seed, double modifier) {
		if (modifier == 0) {
			return seed;
		}
		return ((seed + modifier) % Double.MAX_VALUE);
	}

	public double getSamplerSeed() {
		return samplerSeed;
	}


	@Override
	public String toString() {
		//return this.config.id + " [" + this.getClass().getSimpleName() + " ]";
		return this.config.id;
	}
}
