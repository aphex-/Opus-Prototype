package com.nukethemoon.tools.opusproto.example;

import com.nukethemoon.tools.opusproto.generator.ChunkListener;
import com.nukethemoon.tools.opusproto.generator.Opus;
import com.nukethemoon.tools.opusproto.interpreter.AbstractInterpreter;
import com.nukethemoon.tools.opusproto.loader.json.OpusLoaderJson;
import com.nukethemoon.tools.opusproto.region.Chunk;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;


/**
 * This example loads a world defined in "data/world.json".
 * It creates some chunks of this world and saves them to
 * PNG files located in "data/"
 */
public class BasicExample implements ChunkListener {

	private Opus opus;

	public BasicExample() {
		OpusLoaderJson loader = new OpusLoaderJson();

		try {

			// load opus by a json file
			opus = loader.load("data/world.json");
			// add a callback to receive chunks
			opus.addChunkListener(BasicExample.this);
			// request chunks
			opus.requestChunks(new int[] {0, 1, 0, 1}, new int[] {0, 0, 1, 1});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets called by opus if a chunk is created.
	 * @param chunkX The x position of the chunk.
	 * @param chunkY The y position of the chunk.
	 * @param chunk The chunk.
	 */
	@Override
	public void onChunkCreated(int chunkX, int chunkY, Chunk chunk) {

		System.out.println("Received chunk x" + chunkX + " y" + chunkY );

		try {

			// prepare an image (Java stuff)
			BufferedImage image = new BufferedImage(chunk.getWidth(), chunk.getHeight(),
					BufferedImage.TYPE_INT_RGB);

			int layerCount = opus.getLayers().size();
			// loop through all layers of the chunk
			for (int layerIndex = 0; layerIndex < layerCount; layerIndex++) {
				// get the the interpreter of the layer
				AbstractInterpreter interpreter = opus.getLayers().get(layerIndex).getInterpreter();

				// loop through all tiles of the chunk
				for(int x = 0; x < chunk.getWidth(); x++){
					for(int y = 0; y < chunk.getHeight(); y++){

						// get the value at the position (between 0 and 1)
						float value = chunk.getLayerData()[layerIndex][x][y];

						// use the interpreter to get the type of the value
						// in this case it is a rgb888 color int
						int rgb888 = interpreter.getType(value);

						// assuming that a 0 value should not be drawn
						if (rgb888 > 0) {
							image.setRGB(x, y, rgb888);
						}
					}
				}
			}

			// save the image (Java.awt stuff)
			ImageIO.write(image, "PNG", new File("data/chunk_x" + chunkX + "_y" + chunkY + ".png"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new BasicExample();
	}
}
