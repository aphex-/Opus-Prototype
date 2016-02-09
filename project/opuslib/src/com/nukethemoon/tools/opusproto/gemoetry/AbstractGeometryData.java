package com.nukethemoon.tools.opusproto.gemoetry;

public abstract class AbstractGeometryData {

	private final String creatorId;
	private final double seed;

	public AbstractGeometryData(String creatorId, double seed) {
		this.creatorId = creatorId;
		this.seed = seed;
	}


	public boolean isBasedOn(String creatorId, double seed) {
		return creatorId.equals(this.creatorId) && seed == this.seed;
	}
}
