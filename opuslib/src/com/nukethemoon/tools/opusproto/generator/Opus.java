package com.nukethemoon.tools.opusproto.generator;

import com.nukethemoon.tools.opusproto.layer.Layer;
import com.nukethemoon.tools.opusproto.region.Chunk;
import com.nukethemoon.tools.opusproto.region.ChunkRequestBuffer;
import com.nukethemoon.tools.simpletask.SimpleTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class Opus {

	private WorldConfiguration config;
	private List<ChunkListener> chunkListeners = new ArrayList<ChunkListener>();

	private List<Layer> layers = new ArrayList<Layer>();
	private List<Chunk> chunks = new ArrayList<Chunk>();

	public Opus(WorldConfiguration config, Layer[] layers) {
		this.config = config;
		for (int layerIndex = 0; layerIndex < config.layerIds.length; layerIndex++) {
			String layerId = config.layerIds[layerIndex];
			for (int i = 0; i < layers.length; i++) {
				if (layers[i].getConfig().id.equals(layerId)) {
					this.layers.add(layers[i]);
				}
			}
		}
	}

	private Chunk getChunk(int chunkX, int chunkY) {
		for (Chunk c : chunks) {
			if (c.getChunkX() == chunkX && c.getChunkY() == chunkY) {
				return c;
			}
		}
		return null;
	}

	public void clear() {
		chunks.clear();
	}

	public void requestChunks(int[] chunkX, int[] chunkY) throws ExecutionException, InterruptedException {

		SimpleTaskExecutor<Chunk> executor = new SimpleTaskExecutor<Chunk>();

		for (int chunkIndex = 0; chunkIndex < chunkX.length; chunkIndex++) {

			final int x = chunkX[chunkIndex];
			final int y = chunkY[chunkIndex];

			final Chunk chunk = getChunk(x, y);

			// region al already created
			if (chunk != null) {
				onChunkCreated(x, y, chunk);
			} else {
				executor.addTask(new Callable<Chunk>() {
					// other thread
					@Override
					public Chunk call() throws Exception {
						return createChunk(x, y);
					}
				}, new SimpleTaskExecutor.ResultListener<Chunk>() {
					// main thread
					@Override
					public void onResult(Chunk result) {
						chunks.add(result);
						onChunkCreated(x, y, result);
					}
				});
			}
		}
		executor.execute();
	}

	private Chunk createChunk(int chunkX, int chunkY) {
		int offsetX = chunkX * config.mapSize;
		int offsetY = chunkY * config.mapSize;

		Chunk chunk = new Chunk(
				config.mapSize, config.mapSize,
				offsetX, offsetY, layers.size());

		ChunkRequestBuffer dataBuffer = new ChunkRequestBuffer();

		for (int layerIndex = 0; layerIndex < layers.size(); layerIndex++) {


			Layer layer = layers.get(layerIndex);

			float[][] data = layer.createValues(
					offsetX, offsetY,
					config.mapSize,
					layer.getConfig().scale,
					layer.getSamplerSeed(),
					dataBuffer);

			chunk.getLayerData()[layerIndex] = data;

			/*for (int tileX = offsetX; tileX < offsetX + config.mapSize; tileX++) {
				for (int tileY = offsetY; tileY < offsetY + config.mapSize; tileY++) {
					float v = getSampleAt(tileX, tileY, layerIndex, dataBuffer);
					chunk.setAbsolute(tileX, tileY, layerIndex, v);
				}
			}*/

		}
		dataBuffer.clear();
		return chunk;
	}

	private void onChunkCreated(int chunkX, int chunkY, Chunk chunk) {
		for (ChunkListener listener : chunkListeners) {
			listener.onChunkCreated(chunkX, chunkY, chunk);
		}
	}



	public List<Layer> getLayers() {
		return layers;
	}

	public Layer getLayer(String id) {
		for (Layer l : layers) {
			if (l.getConfig().id.equals(id)) {
				return l;
			}
		}
		return null;
	}

	public WorldConfiguration getConfig() {
		return config;
	}

	public void addRegionListener(ChunkListener listener) {
		chunkListeners.add(listener);
	}

}
