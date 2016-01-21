package com.nukethemoon.tools.opusproto.generator;

import com.nukethemoon.tools.opusproto.Config;
import com.nukethemoon.tools.opusproto.layer.Layer;
import com.nukethemoon.tools.opusproto.log.Log;
import com.nukethemoon.tools.opusproto.region.Chunk;
import com.nukethemoon.tools.opusproto.region.ChunkRequestBuffer;
import com.nukethemoon.tools.simpletask.SimpleTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class Opus {

	private OpusConfiguration config;
	private List<ChunkListener> chunkListeners = new ArrayList<ChunkListener>();

	private List<Layer> layers = new ArrayList<Layer>();
	private List<Chunk> chunks = new ArrayList<Chunk>();

	private int threadPriority = Thread.NORM_PRIORITY;

	public Opus(OpusConfiguration config, Layer[] layers) {
		this.config = config;
		for (int layerIndex = 0; layerIndex < config.layerIds.length; layerIndex++) {
			String layerId = config.layerIds[layerIndex];
			for (int i = 0; i < layers.length; i++) {
				if (layers[i].getConfig().id.equals(layerId)) {
					this.layers.add(layers[i]);
				}
			}
		}
		Log.i(Opus.class, "Using " + Runtime.getRuntime().availableProcessors() + " threads.");

	}

	public void setThreadPriority(int priority) {
		this.threadPriority = priority;
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

	/**
	 * Requests the chunks at the assigned coordinates. Uses threads
	 * to create the chunks.
	 * @param chunkX An array of x coordinates
	 * @param chunkY An array of y coordinates
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public void requestChunks(int[] chunkX, int[] chunkY) throws ExecutionException, InterruptedException {
		SimpleTaskExecutor<Chunk> executor = new SimpleTaskExecutor<Chunk>(Runtime.getRuntime().availableProcessors(), threadPriority);
		for (int chunkIndex = 0; chunkIndex < chunkX.length; chunkIndex++) {
			addChunkRequestTask(executor, chunkX[chunkIndex], chunkY[chunkIndex]);
		}
		executor.execute();
	}

	private void addChunkRequestTask(SimpleTaskExecutor<Chunk> executor, final int chunkX, final int chunkY) {

		final long startMillis = System.currentTimeMillis();

		final Chunk chunk = getChunk(chunkX, chunkY);
		// chunk already created
		if (chunk != null) {
			onChunkCreated(chunkX, chunkY, chunk);
		} else {
			executor.addTask(new Callable<Chunk>() {
				@Override
				public Chunk call() throws Exception {
					// chunk creation thread
					return createChunk(chunkX, chunkY);
				}
			}, new SimpleTaskExecutor.ResultListener<Chunk>() {
				@Override
				public void onResult(Chunk result) {
					// main thread
					chunks.add(result);
					onChunkCreated(chunkX, chunkY, result);
				}
			});
		}
	}

	/**
	 * This method must be thread safe.
	 * @param chunkX The x coordinate of a chunk
	 * @param chunkY The y coordinate of a chunk.
	 * @return The created chunk.
	 */
	private Chunk createChunk(int chunkX, int chunkY) {
		int requestOffsetX = chunkX * (config.chunkSize - config.chunkOverlap);
		int requestOffsetY = chunkY * (config.chunkSize - config.chunkOverlap);

		int chunkOffsetX = chunkX * config.chunkSize;
		int chunkOffsetY = chunkY * config.chunkSize;

		Chunk chunk = new Chunk(
				config.chunkSize, config.chunkSize,
				chunkOffsetX, chunkOffsetY, layers.size());

		ChunkRequestBuffer dataBuffer = new ChunkRequestBuffer();

		for (int layerIndex = 0; layerIndex < layers.size(); layerIndex++) {


			Layer layer = layers.get(layerIndex);

			float[][] data = layer.createValues(
					requestOffsetX, requestOffsetY,
					config.chunkSize,
					layer.getConfig().scale,
					layer.getContainingSeed(),
					dataBuffer);

			chunk.getLayerData()[layerIndex] = data;

			/*for (int tileX = offsetX; tileX < offsetX + config.chunkSize; tileX++) {
				for (int tileY = offsetY; tileY < offsetY + config.chunkSize; tileY++) {
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

	public OpusConfiguration getConfig() {
		return config;
	}

	public void addChunkListener(ChunkListener listener) {
		chunkListeners.add(listener);
	}

}
