package com.nukethemoon.tools.opusproto.region;

import java.util.ArrayList;
import java.util.List;

public class ChunkRequestBuffer {

	private List<DataContainer> samplerDataBuffer = new ArrayList<DataContainer>();

	public void clear() {
		samplerDataBuffer.clear();
	}

	public void addSamplerData(String samplerId, double seed, float scale, float[][] data) {
		DataContainer dc = getContainer(samplerId, seed, scale);
		if (dc == null) {
			samplerDataBuffer.add(new DataContainer(samplerId, seed, scale, data));
		}
		if (dc != null) {
			dc.data = data;
		}
	}

	public float[][] getSamplerData(String samplerId, double seed, float scale) {
		DataContainer container = getContainer(samplerId, seed, scale);
		if (container != null) {
			return container.data;
		}
		return null;
	}

	private DataContainer getContainer(String samplerId, double seed, float scale) {
		for (DataContainer container : samplerDataBuffer) {
			if (container.samplerId.equals(samplerId)
					&& container.seed == seed && container.scale == scale) {
				return container;
			}
		}
		return null;
	}

	public boolean containsSamplerData(String samplerId, double seed, float scale) {
		return getContainer(samplerId, seed, scale) != null;
	}


	public static class DataContainer {
		public String samplerId;
		public double seed;
		public float scale;

		public float[][] data;

		public DataContainer(String samplerId, double seed, float scale, float[][] data) {
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
				return di.samplerId.equals(samplerId) && di.seed == seed && di.scale == scale;
			}
			return false;
		}
	}
}
