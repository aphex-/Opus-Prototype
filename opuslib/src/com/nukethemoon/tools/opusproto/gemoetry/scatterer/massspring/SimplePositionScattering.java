package com.nukethemoon.tools.opusproto.gemoetry.scatterer.massspring;

import com.nukethemoon.tools.opusproto.MathE;
import com.nukethemoon.tools.opusproto.sampler.Samplers;
import com.nukethemoon.tools.opusproto.exceptions.SamplerInvalidConfigException;
import com.nukethemoon.tools.opusproto.gemoetry.AbstractGeometryData;
import com.nukethemoon.tools.opusproto.gemoetry.PointList;
import com.nukethemoon.tools.opusproto.gemoetry.scatterer.AbstractGeometrySampler;
import com.nukethemoon.tools.opusproto.noise.Algorithms;
import com.nukethemoon.tools.opusproto.noise.algorithms.SimplexNoise;
import com.nukethemoon.tools.opusproto.region.ChunkRequestBuffer;
import com.nukethemoon.tools.opusproto.sampler.AbstractSamplerConfiguration;
import com.nukethemoon.tools.opusproto.log.Log;

import java.util.ArrayList;
import java.util.List;

public class SimplePositionScattering extends AbstractGeometrySampler {

	private SimplexNoise simplexNoise;
	private SimplePositionConfig c;



	public SimplePositionScattering(AbstractSamplerConfiguration config,
									double worldSeed, Algorithms noisePool,
									Samplers samplers) {
		super(config, worldSeed, noisePool, samplers);
		c = ((SimplePositionConfig) config);

		if (c.maximumDistance >= c.gridSize / 2f) {
			Log.e(SimplePositionScattering.class, "The maximum distance '" + c.maximumDistance + "'"
					+ " must be lower than the grid size '" + c.gridSize +" / 2' for scattering '" + c.id + "'."
			);
		}

		simplexNoise = (SimplexNoise) noisePool.getAlgorithm(Algorithms.NAME_SIMPLEX);
	}

	@Override
	protected float[][] bufferedCreateValues(float x, float y, int size, float scaleFactor, double seedModifier, ChunkRequestBuffer buffer) {
		return new float[0][];
	}

	@Override
	public AbstractGeometryData createGeometries(float recX01, float recY01, float recX02, float recY02, double seed) {
		// adding neighbour grid cells (-1 / + 1 at the end)
		float gridRecLeft = ceilGrid(((recX01 < recX02) ? recX01 : recX02)) - 1;
		float gridRecRight = floorGrid(((recX01 > recX02) ? recX01 : recX02)) + 1;
		float gridRecBottom = ceilGrid((recY01 < recY02) ? recY01 : recY02) - 1;
		float gridRecTop = floorGrid((recY01 > recY02) ? recY01 : recY02) + 1;

		int gridPointCountX = (int) (Math.abs(gridRecRight - gridRecLeft)) + 1;
		int gridPointCountY = (int) (Math.abs(gridRecTop - gridRecBottom)) + 1;

		List<Float> pointsList = new ArrayList<Float>();

		for (int gridOffsetX = 0; gridOffsetX < gridPointCountX; gridOffsetX++) {
			for (int gridOffsetY = 0; gridOffsetY < gridPointCountY; gridOffsetY++) {

				float gridX = gridRecLeft + gridOffsetX;
				float gridY = gridRecBottom + gridOffsetY;

				float x = randomizePosition(gridX, gridY, seed) + (c.gridSize * gridX);
				float y = randomizePosition(-gridX, -gridY, seed) + (c.gridSize * gridY);

				if (MathE.isPointInRect(x, y, recX01, recY01, recX02, recY02)) {
					pointsList.add(x);
					pointsList.add(y);
				}
			}
		}

		float[] array = new float[pointsList.size()];
		for(int i = 0; i < pointsList.size(); i++) array[i] = pointsList.get(i);
		return new PointList(config.id, seed, array);
	}


	private float randomizePosition(float gridX, float gridY, double seed) {
		return ((float) (this.simplexNoise.noise(gridX, gridY, seed) - 0.5f) * 2f) * c.maximumDistance;

	}

	private float floorGrid(float coordinate) {
		return(float) Math.floor(coordinate / c.gridSize);
	}

	private float ceilGrid(float coordinate) {
		return(float) Math.ceil(coordinate / c.gridSize);
	}

	@Override
	protected void loadConfig() throws SamplerInvalidConfigException {

	}

	@Override
	protected void compute(float[][] data) {

	}


	@Override
	public float getMaxSample() {
		return 1;
	}

	@Override
	public float getMinSample() {
		return 0;
	}
}
