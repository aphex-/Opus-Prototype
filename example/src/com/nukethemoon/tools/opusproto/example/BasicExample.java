package com.nukethemoon.tools.opusproto.example;

import com.nukethemoon.tools.opusproto.generator.ChunkListener;
import com.nukethemoon.tools.opusproto.generator.Opus;
import com.nukethemoon.tools.opusproto.loader.json.OpusLoaderJson;
import com.nukethemoon.tools.opusproto.region.Chunk;

public class BasicExample implements ChunkListener {

	public BasicExample() {
		OpusLoaderJson loader = new OpusLoaderJson();

		try {

			// load opus by a json file
			Opus opus = loader.load("data/save.json");
			// add a callback te receive chunks
			opus.addChunkListener(BasicExample.this);
			// request chunks
			opus.requestChunks(new int[] {0, 1}, new int[] {0, 0});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onChunkCreated(int x, int y, Chunk chunk) {
		System.out.println("Received chunk x" + x + " y" + y );


	}

	public static void main(String[] args) {
		new BasicExample();
	}
}
