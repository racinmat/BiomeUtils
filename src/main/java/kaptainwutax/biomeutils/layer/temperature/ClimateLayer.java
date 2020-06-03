package kaptainwutax.biomeutils.layer.temperature;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.layer.BiomeLayer;
import kaptainwutax.biomeutils.layer.composite.CrossLayer;

public class ClimateLayer {

	public static class Cold extends BiomeLayer {
		public Cold(long worldSeed, long salt, BiomeLayer parent) {
			super(worldSeed, salt, parent);
		}

		public Cold(long worldSeed, long salt) {
			this(worldSeed, salt, null);
		}

		@Override
		public int sample(int x, int z) {
			int value = this.parent.get(x, z);

			if(Biome.isShallowOcean(value)) {
				return value;
			}

			this.setSeed(x, z);
			int i = this.nextInt(6);
			if(i == 0)return Biome.FOREST.getId();
			return i == 1 ? Biome.MOUNTAINS.getId() : Biome.PLAINS.getId();
		}
	}

	public static class Temperate extends CrossLayer {
		public Temperate(long worldSeed, long salt, BiomeLayer parent) {
			super(worldSeed, salt, parent);
		}

		public Temperate(long worldSeed, long salt) {
			this(worldSeed, salt, null);
		}

		@Override
		public int sample(int n, int e, int s, int w, int center) {
			return center != Biome.PLAINS.getId() || n != Biome.MOUNTAINS.getId() && e != Biome.MOUNTAINS.getId()
					&& w != Biome.MOUNTAINS.getId() && s != Biome.MOUNTAINS.getId() && n != Biome.FOREST.getId()
					&& e != Biome.FOREST.getId() && w != Biome.FOREST.getId()
					&& s != Biome.FOREST.getId() ? center : Biome.DESERT.getId();
		}
	}

	public static class Cool extends CrossLayer {
		public Cool(long worldSeed, long salt, BiomeLayer parent) {
			super(worldSeed, salt, parent);
		}

		public Cool(long worldSeed, long salt) {
			this(worldSeed, salt, null);
		}

		@Override
		public int sample(int n, int e, int s, int w, int center) {
			return center != Biome.FOREST.getId() || n != Biome.PLAINS.getId() && e != Biome.PLAINS.getId()
					&& w != Biome.PLAINS.getId() && s != Biome.PLAINS.getId() && n != Biome.DESERT.getId()
					&& e != Biome.DESERT.getId() && w != Biome.DESERT.getId()
					&& s != Biome.DESERT.getId() ? center : Biome.MOUNTAINS.getId();
		}
	}

	public static class Special extends BiomeLayer {
		public Special(long worldSeed, long salt, BiomeLayer parent) {
			super(worldSeed, salt, parent);
		}

		public Special(long worldSeed, long salt) {
			this(worldSeed, salt, null);
		}

		@Override
		public int sample(int x, int z) {
			int i = this.parent.get(x, z);

			if(Biome.isShallowOcean(i))return i;
			this.setSeed(x, z);

			if(this.nextInt(13) == 0) {
				i |= (1 + this.nextInt(15)) << 8;
			}

			return i;
		}
	}

}