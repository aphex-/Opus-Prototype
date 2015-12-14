package com.nukethemoon.tools.opusproto;

import com.nukethemoon.tools.opusproto.generator.WorldConfiguration;
import com.nukethemoon.tools.opusproto.generator.WorldGenerator;
import com.nukethemoon.tools.opusproto.noise.NoiseAlgorithmPool;

public class PresetFabric {

	public static WorldGenerator createDefaultGenerator(WorldConfiguration worldConfiguration,
														NoiseAlgorithmPool noiseAlgorithmPool,
														SamplerLoader samplerLoader) {


		/*com.nukethemoon.tools.opusproto.layer.Layer.LayerConfig standardLandscapeLayer = new Layer.LayerConfig("LayerStandardLandscape01");
		standardLandscapeLayer.maskIds = new String[] {"Flat01"};
		standardLandscapeLayer.maskOperator = new Layer.LayerConfig.Operator[] {Layer.LayerConfig.Operator.Plus};
		standardLandscapeLayer.samplerIds = new String[] {"SNoise01", "SNoise02", "SNoise03"};
		standardLandscapeLayer.samplerOperator = new Layer.LayerConfig.Operator[] {
				Layer.LayerConfig.Operator.Plus, Layer.LayerConfig.Operator.Plus, Layer.LayerConfig.Operator.Plus};
		com.nukethemoon.tools.opusproto.layer.Layer layer = new com.nukethemoon.tools.opusproto.layer.Layer(standardLandscapeLayer, worldConfiguration.seed, samplerPool);
		return new WorldGenerator(worldConfiguration, new Layer[] {layer});*/
		return null;
	}

	public static SamplerLoader createDefaultSamplerPool(NoiseAlgorithmPool noiseAlgorithmPool, WorldConfiguration worldConfiguration) {
		/*SamplerPool samplerPool = new SamplerPool();

		// Flat sampler 			"Flat01"
		FlatSampler.FlatSamplerConfig flatConfig = new FlatSampler.FlatSamplerConfig("Flat01");
		flatConfig.value = 1;
		FlatSampler flatSampler = new FlatSampler(flatConfig, worldConfiguration.seed, noiseAlgorithmPool);
		samplerPool.addSampler(flatSampler);

		// Simplex Noise Sampler 	"SNoise01"
		NoiseSampler.NoiseConfig sNoise01 = new NoiseSampler.NoiseConfig("SNoise01");
		sNoise01.scale = 30;
		sNoise01.modifiers.add(new SamplerModifier(SamplerModifier.Type.Multiply, 0.2f));
		samplerPool.addSampler(AbstractSampler.create(sNoise01, worldConfiguration.seed, noiseAlgorithmPool));


		// Simplex Noise Sampler 	"SNoise02"
		NoiseSampler.NoiseConfig sNoise02 = new NoiseSampler.NoiseConfig("SNoise02");
		sNoise02.scale = 50;
		sNoise02.modifiers.add(new SamplerModifier(SamplerModifier.Type.Multiply, -0.3f));
		samplerPool.addSampler(AbstractSampler.create(sNoise02, worldConfiguration.seed, noiseAlgorithmPool));

		// Simplex Noise Sampler 	"SNoise03"
		NoiseSampler.NoiseConfig sNoise03 = new NoiseSampler.NoiseConfig("SNoise03");
		sNoise03.scale = 200;
		sNoise03.modifiers.add(new SamplerModifier(SamplerModifier.Type.Multiply, 1.75f));
		sNoise03.modifiers.add(new SamplerModifier(SamplerModifier.Type.Min, 1f));
		sNoise03.modifiers.add(new SamplerModifier(SamplerModifier.Type.Multiply, 0.5f));
		samplerPool.addSampler(AbstractSampler.create(sNoise03, worldConfiguration.seed, noiseAlgorithmPool));

		return samplerPool;*/
		return null;
	}


}
