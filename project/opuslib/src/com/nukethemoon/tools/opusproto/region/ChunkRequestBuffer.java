package com.nukethemoon.tools.opusproto.region;

import java.util.ArrayList;
import java.util.List;

public class ChunkRequestBuffer {

	private List<DataContainer> samplerDataBuffer = new ArrayList<DataContainer>();

	public void clear() {
		samplerDataBuffer.clear();
	}

	public void addSamplerData(String samplerId, double seed, float scale, float resolution, float[][] data) {
		DataContainer dc = getContainer(samplerId, seed, scale, resolution);
		if (dc == null) {
			samplerDataBuffer.add(new DataContainer(samplerId, seed, scale, resolution, data));
		}
		if (dc != null) {
			dc.data = data;
		}
	}

	public float[][] getSamplerData(String samplerId, double seed, float scale, float resolution) {
		DataContainer container = getContainer(samplerId, seed, scale, resolution);
		if (container != null) {
			return container.data;
		}
		return null;
	}

	private DataContainer getContainer(String samplerId, double seed, float scale, float resolution) {
		for (DataContainer container : samplerDataBuffer) {
			if (container.samplerId.equals(samplerId)
					&& container.seed == seed && container.scale == scale && container.resolution == resolution) {
				return container;
			}
		}
		return null;
	}

	public boolean containsSamplerData(String samplerId, double seed, float scale, float resolution) {
		return getContainer(samplerId, seed, scale, resolution) != null;
	}


	public static class DataContainer {
		public String samplerId;
		public double seed;
		public float scale;
		public float resolution;
		public float[][] data;

		public DataContainer(String samplerId, double seed, float scale, float resolution, float[][] data) {
			this.resolution = resolution;
			this.data = data;
			this.samplerId = samplerId;
			this.seed = seed;
			this.scale = scale;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj instanceof DataContainer) {
				DataContainer di = (DataContainer) obj;
				return di.samplerId.equals(samplerId) && di.seed == seed && di.scale == scale && resolution == di.resolution;
			}
			return false;
		}
	}
}
