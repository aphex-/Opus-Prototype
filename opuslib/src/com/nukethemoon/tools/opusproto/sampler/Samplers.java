package com.nukethemoon.tools.opusproto.sampler;

import com.nukethemoon.tools.opusproto.Config;
import com.nukethemoon.tools.opusproto.exceptions.SamplerRecursionException;
import com.nukethemoon.tools.opusproto.exceptions.SamplerUnresolvedDependencyException;
import com.nukethemoon.tools.opusproto.interpreter.AbstractInterpreter;
import com.nukethemoon.tools.opusproto.layer.Layer;
import com.nukethemoon.tools.opusproto.layer.LayerConfig;
import com.nukethemoon.tools.opusproto.noise.Algorithms;
import com.nukethemoon.tools.opusproto.sampler.acontinent.AContinent;
import com.nukethemoon.tools.opusproto.sampler.acontinent.AContinentConfig;
import com.nukethemoon.tools.opusproto.sampler.combined.Combined;
import com.nukethemoon.tools.opusproto.sampler.combined.CombinedConfig;
import com.nukethemoon.tools.opusproto.sampler.flat.FlatSampler;
import com.nukethemoon.tools.opusproto.sampler.flat.FlatSamplerConfig;
import com.nukethemoon.tools.opusproto.sampler.masked.MaskedSampler;
import com.nukethemoon.tools.opusproto.sampler.masked.MaskedSamplerConfig;
import com.nukethemoon.tools.opusproto.sampler.noise.NoiseConfig;
import com.nukethemoon.tools.opusproto.sampler.noise.NoiseSampler;
import com.nukethemoon.tools.opusproto.log.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Samplers {

	private List<AbstractSampler> loadedSamplers = new ArrayList<AbstractSampler>();
	private List<AbstractInterpreter> loadedInterpreter = new ArrayList<AbstractInterpreter>();

	public static Map<Class<? extends AbstractSampler>, Class<? extends AbstractSamplerConfiguration>>
			SAMPLER_TO_CONFIG = new HashMap<Class<? extends AbstractSampler>, Class<? extends AbstractSamplerConfiguration>>();

	static {
		SAMPLER_TO_CONFIG.put(Combined.class, 		CombinedConfig.class);
		SAMPLER_TO_CONFIG.put(NoiseSampler.class,	NoiseConfig.class);
		SAMPLER_TO_CONFIG.put(FlatSampler.class,	FlatSamplerConfig.class);
		SAMPLER_TO_CONFIG.put(MaskedSampler.class, 	MaskedSamplerConfig.class);
		SAMPLER_TO_CONFIG.put(AContinent.class,		AContinentConfig.class);
	}

	/**
	 * Loads the samplers for the assigned configurations. Handles sampler dependencies.
	 * @param configs The configs to load the sampler from.
	 * @param seed The world seed.
	 * @param pool The noise pool.
	 * @throws SamplerRecursionException
	 * @throws SamplerUnresolvedDependencyException
	 */
	public void loadSamplers(AbstractSamplerConfiguration[] configs, double seed, Algorithms pool)
			throws SamplerRecursionException, SamplerUnresolvedDependencyException {

		// first run: load samplers without child dependencies
		for (AbstractSamplerConfiguration c : configs) {
			if (!(c instanceof AbstractSamplerContainerConfig)) {
				addSampler(create(c, seed, pool, this));
			}
		}

		List<AbstractSamplerContainerConfig> remainingSamplerConfigs = new ArrayList<AbstractSamplerContainerConfig>();

		// create a list of remaining sampler
		for (AbstractSamplerConfiguration c : configs) {
			if ((c instanceof AbstractSamplerContainerConfig)) {
				remainingSamplerConfigs.add((AbstractSamplerContainerConfig) c);
			}
		}

		List<AbstractSamplerContainerConfig> tmpRemoveList = new ArrayList<AbstractSamplerContainerConfig>();
		boolean firstLoop = true;
		// try to resolve sampler with child dependencies.
		while (tmpRemoveList.size() > 0 || firstLoop) {
			firstLoop = false;
			for (AbstractSamplerContainerConfig removeItem : tmpRemoveList) {
				remainingSamplerConfigs.remove(removeItem);
			}
			tmpRemoveList.clear();
			for (AbstractSamplerContainerConfig c : remainingSamplerConfigs) {
				boolean childrenFound = canLoadChildren(c.id, c, 0);
				if (childrenFound) {
					addSampler(create(c, seed, pool, this));
					tmpRemoveList.add(c);
				}
			}
		}

		if (remainingSamplerConfigs.size() > 0) {
			throw new SamplerUnresolvedDependencyException(remainingSamplerConfigs);
		}

	}

	/**
	 * Returns true if the children of a sampler can be loaded. Can run recursive.
	 * @param startSamplerId The sampler id the search started.
	 * @param config The config to check.
	 * @param recursionDepth The current recursion depth.
	 * @return True if all children can be loaded.
	 * @throws SamplerRecursionException
	 */
	public boolean canLoadChildren(String startSamplerId, AbstractSamplerContainerConfig config, int recursionDepth) throws SamplerRecursionException {
		if (recursionDepth > Config.MAX_SAMPLER_RECURSION_DEPTH) {
			throw new SamplerRecursionException(startSamplerId);
		}

		for (ChildSamplerConfig childConfig : config.getChildSamplerConfigs()) {
			AbstractSampler sampler = getSampler(childConfig.samplerReferenceId);
			if (sampler == null) {
				return false;
			}
			if (sampler.getConfig() instanceof AbstractSamplerContainerConfig) {
				if(!canLoadChildren(startSamplerId, (AbstractSamplerContainerConfig) sampler.getConfig(), recursionDepth + 1)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Maps a sampler class to its config class.
	 * @param samplerClass The sampler class to get the config from.
	 * @return The config class.
	 */
	public static Class<? extends AbstractSamplerConfiguration> getConfigClass(Class<? extends AbstractSampler> samplerClass) {
		return SAMPLER_TO_CONFIG.get(samplerClass);
	}

	/**
	 * Maps a config class to its sampler class
	 * @param configurationClass The config class to get the sampler from.
	 * @return Teh sampler class.
	 */
	public static Class<? extends AbstractSampler> getSamplerClass(Class<? extends AbstractSamplerConfiguration> configurationClass) {
		for (Map.Entry<Class<? extends AbstractSampler>, Class<? extends AbstractSamplerConfiguration>> entry : SAMPLER_TO_CONFIG.entrySet())
			if (entry.getValue() == configurationClass) {
				return entry.getKey();
			}
		return null;
	}

	/**
	 * Creates a new sampler by its config.
	 * @param config The config to create a sampler for.
	 * @param seed The world seed.
	 * @param noisePool The noise pool.
	 * @param samplers The sampler loader.
	 * @return The created sampler.
	 */
	public static AbstractSampler create(AbstractSamplerConfiguration config, double seed, Algorithms noisePool, Samplers samplers) {
		Class<? extends AbstractSampler> samplerClass = getSamplerClass(config.getClass());
		try {
			return (AbstractSampler) samplerClass.getConstructors()[0].newInstance(config, seed, noisePool, samplers);
		} catch (InstantiationException e) {
			Log.e(Samplers.class, e.getMessage());
		} catch (IllegalAccessException e) {
			Log.e(Samplers.class, e.getMessage());
		} catch (InvocationTargetException e) {
			Log.e(Samplers.class, e.getMessage());
		}
		return null;
	}

	public static Class<? extends AbstractSamplerConfiguration> getConfigClassByName(String name) {
		for(Class<? extends  AbstractSamplerConfiguration> c : Samplers.SAMPLER_TO_CONFIG.values()) {
			if (c.getSimpleName().equals(name)) {
				return c;
			}
		}
		return null;
	}


	public boolean doesASamplerDependOn(AbstractSampler sampler, List<String> foundDependencies) throws SamplerRecursionException {
		final List<AbstractSampler> list = new ArrayList<AbstractSampler>();
		for (AbstractSampler s : loadedSamplers) {
			list.add(s);
		}
		return doesASamplerDependOn(sampler, list, foundDependencies, 0);
	}

	public boolean doesALayerDependOn(AbstractInterpreter interpreter, List<Layer> layers) {
		for (Layer l : layers) {
			if (((LayerConfig)l.getConfig()).interpreterId.equals(interpreter.id)) {
				return true;
			}
		}
		return false;
	}


	public boolean doesSecondDependOnFirst(AbstractSampler sampler01, AbstractSampler sampler02) throws SamplerRecursionException {
		if (!(sampler02.getConfig() instanceof AbstractSamplerContainerConfig)) {
			return false;
		}
		List<AbstractSampler> list = new ArrayList<AbstractSampler>();
		list.add(sampler02);
		return doesASamplerDependOn(sampler01, list, new ArrayList<String>(), 0);
	}

	public boolean doesASamplerDependOn(AbstractSampler sampler, List<AbstractSampler> samplerListToCheck, List<String> foundDependencies, int recursionDepth) throws SamplerRecursionException {

		// exception if max recursion depth is reached
		if (recursionDepth > Config.MAX_SAMPLER_RECURSION_DEPTH) {
			throw new SamplerRecursionException(sampler.getConfig().id);
		}

		for (AbstractSampler samplerToCheck : samplerListToCheck) {
			if (samplerToCheck.getConfig() instanceof AbstractSamplerContainerConfig) {
				// loop through children
				final List<AbstractSampler> children = new ArrayList<AbstractSampler>();

				for (ChildSamplerConfig childConfig : ((AbstractSamplerContainerConfig) samplerToCheck.getConfig()).samplerItems) {
					AbstractSampler childSampler = getSampler(childConfig.samplerReferenceId);

					if (sampler.getConfig().id.equals(childConfig.samplerReferenceId)) {
						if (!foundDependencies.contains(samplerToCheck.getConfig().id)) {
							foundDependencies.add(samplerToCheck.getConfig().id);
						}
					}

					if (childSampler != null) {
						children.add(childSampler);
					}
				}
				doesASamplerDependOn(sampler, children, foundDependencies, recursionDepth + 1);
			}
		}

		return foundDependencies.size() > 0;
	}

	public void addSampler(AbstractSampler sampler) {
		if (getSampler(sampler.getConfig().id) != null) {
			Log.i(Samplers.class, "Sampler can not be added. Id already exists + " + sampler.getConfig().id);
			return;
		}
		loadedSamplers.add(sampler);
		Log.d(Samplers.class, "Loaded sampler " + sampler.getConfig().id);
	}



	public void changeId(String samplerId, String newId) {
		for (AbstractSampler s : loadedSamplers) {
			changeId(s, samplerId, newId);
		}
	}

	/**
	 * Changes the name of a sampler. Recursively uses searches samplers under
	 * the assigned root sampler.
	 * @param rootSampler The root sampler the search.
	 * @param oldId The old sampler id.
	 * @param newId The new sampler id.
	 */
	public void changeId(AbstractSampler rootSampler, String oldId, String newId) {
		if (rootSampler == null) {
			return;
		}
		if (rootSampler.getConfig().id.equals(oldId)) {
			rootSampler.getConfig().id = newId;
		}
		if (rootSampler.getConfig() instanceof AbstractSamplerContainerConfig) {
			AbstractSamplerContainerConfig c = (AbstractSamplerContainerConfig) rootSampler.getConfig();
			for (ChildSamplerConfig child : c.getChildSamplerConfigs()) {
				AbstractSampler childSampler = getSampler(child.samplerReferenceId);
				if (child.samplerReferenceId.equals(oldId)) {
					child.samplerReferenceId = newId;
				}
				changeId(childSampler, oldId, newId);
			}
		}
	}

	public void changeInterpreterId(String oldId, String newId, List<Layer> layers) {
		getInterpreter(oldId).id = newId;
		for (Layer l : layers) {
			if (((LayerConfig)l.getConfig()).interpreterId.equals(oldId)) {
				((LayerConfig)l.getConfig()).interpreterId = newId;
			}
		}
	}

	public void removeSampler(String samplerId) {
		loadedSamplers.remove(getSampler(samplerId));
	}

	public AbstractSampler getSampler(String id) {
		for (AbstractSampler s : loadedSamplers) {
			if (s.getConfig().id.equals(id)) {
				return s;
			}
		}
		return null;
	}

	public int getCount() {
		return loadedSamplers.size();
	}

	public AbstractSampler[] createSamplerList() {
		AbstractSampler[] list = new AbstractSampler[loadedSamplers.size()];
		int i = 0;
		for (AbstractSampler entry : loadedSamplers) {
			list[i] = entry;
			i++;
		}
		return list;
	}

	public void addInterpreter(AbstractInterpreter interpreter) {
		if (getInterpreter(interpreter.id) == null) {
			loadedInterpreter.add(interpreter);
		} else {
			Log.i(Samplers.class, "Interpreter can not be added. Id already exists + " + interpreter.id);
		}

	}

	public AbstractInterpreter getInterpreter(String id) {
		for (AbstractInterpreter interpreter : loadedInterpreter) {
			if (interpreter.id.equals(id)) {
				return interpreter;
			}
		}
		return null;
	}

	public AbstractInterpreter[] createInterpreterList() {
		AbstractInterpreter[] list = new AbstractInterpreter[loadedInterpreter.size()];
		int i = 0;
		for (AbstractInterpreter entry : loadedInterpreter) {
			list[i] = entry;
			i++;
		}
		return list;
	}

	public void removeInterpreter(String id) {
		AbstractInterpreter interpreter = getInterpreter(id);
		loadedInterpreter.remove(interpreter);
	}

	public int getInterpreterCount() {
		return loadedInterpreter.size();
	}
}
