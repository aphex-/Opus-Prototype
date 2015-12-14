package com.nukethemoon.tools.opusproto.generator;

import com.nukethemoon.tools.opusproto.region.Chunk;

public interface ChunkListener {

	void onChunkCreated(int regionX, int regionY, Chunk chunk);
}
