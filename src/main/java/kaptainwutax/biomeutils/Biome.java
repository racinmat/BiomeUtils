package kaptainwutax.biomeutils;

import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.seedutils.mc.MCVersion;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class Biome {

    public static Map<Integer, Biome> REGISTRY = new HashMap<>();

    private final int id;
    private final String name;
    private final Category category;
    private final Precipitation precipitation;
    private final float temperature;

    private final Biome parent;
    private Biome child;

    private final MCVersion version;

    public Biome(MCVersion version, int id, String name, Category category, Precipitation precipitation,
                 float temperature, Biome parent) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.precipitation = precipitation;
        this.temperature = temperature;
        this.parent = parent;
        if (this.parent != null) this.parent.child = this;
        REGISTRY.put(this.id, this);
        this.version = version;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Category getCategory() {
        return this.category;
    }

    public Precipitation getPrecipitation() {
        return this.precipitation;
    }

    public float getTemperature() {
        return this.temperature;
    }

    public MCVersion getVersion(){
        return this.version;
    }

    public Temperature getTemperatureGroup() {
        if(this.category == Biome.Category.OCEAN) {
            return Temperature.OCEAN;
        } else if(this.getTemperature() < 0.2F) {
            return Temperature.COLD;
        } else if(this.getTemperature() < 1.0F) {
            return Temperature.MEDIUM;
        }

        return Temperature.WARM;
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    public Biome getParent() {
        return this.parent;
    }

    public boolean hasChild() {
        return this.child != null;
    }

    public Biome getChild() {
        return this.child;
    }

    public static boolean isShallowOcean(int id) {
        return id == Biome.WARM_OCEAN.getId() || id == Biome.LUKEWARM_OCEAN.getId() || id == Biome.OCEAN.getId()
                || id == Biome.COLD_OCEAN.getId() || id == Biome.FROZEN_OCEAN.getId();
    }

    public static boolean isOcean(int id) {
        return id == Biome.WARM_OCEAN.getId() || id == Biome.LUKEWARM_OCEAN.getId() || id == Biome.OCEAN.getId()
                || id == Biome.COLD_OCEAN.getId() || id == Biome.FROZEN_OCEAN.getId()
                || id == Biome.DEEP_WARM_OCEAN.getId() || id == Biome.DEEP_LUKEWARM_OCEAN.getId()
                || id == Biome.DEEP_OCEAN.getId() || id == Biome.DEEP_COLD_OCEAN.getId()
                || id == Biome.DEEP_FROZEN_OCEAN.getId();
    }


    public static boolean isRiver(int id) {
        return id == Biome.RIVER.getId() || id == Biome.FROZEN_RIVER.getId();
    }

    public static boolean areSimilar(int id, Biome b2) {
        if (b2 == null) return false;
        if (id == b2.getId()) return true;

        Biome b = Biome.REGISTRY.get(id);
        if (b == null) return false;

        if (id != Biome.WOODED_BADLANDS_PLATEAU.getId() && id != Biome.BADLANDS_PLATEAU.getId()) {
            if (b.getCategory() != Biome.Category.NONE && b2.getCategory()
                    != Biome.Category.NONE && b.getCategory() == b2.getCategory()) {
                return true;
            }

            return b == b2;
        }

        return b2 == Biome.WOODED_BADLANDS_PLATEAU || b2 == Biome.BADLANDS_PLATEAU;
    }

    public static boolean applyAll(Function<Integer, Boolean> function, int... ints) {
        for(int i : ints) {
            if(!function.apply(i)) {
                return false;
            }
        }

        return true;
    }

    public static int equalsOrDefault(int comparator, int comparable, int fallback) {
        return comparator == comparable ? comparable : fallback;
    }

    public Biome.Data at(int x, int z) {
        return new Biome.Data(this, x, z);
    }

    public static class Data {
        public final Predicate<Biome> predicate;
        public final Biome biome;
        public final int x;
        public final int z;

        public Data(Biome biome, int x, int z) {
            this(b -> b == biome, biome, x, z);
        }

        public Data(Predicate<Biome> predicate, int x, int z) {
            this(predicate, null, x, z);
        }

        protected Data(Predicate<Biome> predicate, Biome biome, int x, int z) {
            this.predicate = predicate;
            this.biome = biome;
            this.x = x;
            this.z = z;
        }

        public boolean test(OverworldBiomeSource source) {
            return this.predicate.test(source.getBiome(this.x, 0, this.z));
        }
    }

    public enum Category {
        NONE("none"), TAIGA("taiga"), EXTREME_HILLS("extreme_hills"),
        JUNGLE("jungle"), MESA("mesa"), PLAINS("plains"), SAVANNA("savanna"),
        ICY("icy"), THE_END("the_end"), BEACH("beach"), FOREST("forest"),
        OCEAN("ocean"), DESERT("desert"), RIVER("river"), SWAMP("swamp"),
        MUSHROOM("mushroom"), NETHER("nether");

        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum Temperature {
        OCEAN("ocean"), COLD("cold"), MEDIUM("medium"), WARM("warm");

        private final String name;

        Temperature(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum Precipitation {
        NONE("none"), RAIN("rain"), SNOW("snow");

        private final String name;

        Precipitation(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    public static final Biome OCEAN = new Biome(MCVersion.v1_7, 0, "ocean", Category.OCEAN, Precipitation.RAIN, 0.5F, null);
    public static final Biome PLAINS = new Biome(MCVersion.v1_7, 1, "plains", Category.PLAINS, Precipitation.RAIN, 0.8F, null);
    public static final Biome DESERT = new Biome(MCVersion.v1_7, 2, "desert", Category.DESERT, Precipitation.NONE, 2.0F, null);
    public static final Biome MOUNTAINS = new Biome(MCVersion.v1_7, 3, "mountains", Category.EXTREME_HILLS, Precipitation.RAIN, 0.2F, null);
    public static final Biome FOREST = new Biome(MCVersion.v1_7, 4, "forest", Category.FOREST, Precipitation.RAIN, 0.7F, null);
    public static final Biome TAIGA = new Biome(MCVersion.v1_7, 5, "taiga", Category.TAIGA, Precipitation.RAIN, 0.25F, null);
    public static final Biome SWAMP = new Biome(MCVersion.v1_7, 6, "swamp", Category.SWAMP, Precipitation.RAIN, 0.8F, null);
    public static final Biome RIVER = new Biome(MCVersion.v1_7, 7, "river", Category.RIVER, Precipitation.RAIN, 0.5F, null);
    public static final Biome NETHER_WASTES = new Biome(MCVersion.v1_7, 8, "nether_wastes", Category.NETHER, Precipitation.NONE, 2.0F, null);
    public static final Biome THE_END = new Biome(MCVersion.v1_7, 9, "the_end", Category.THE_END, Precipitation.NONE, 0.5F, null);
    public static final Biome FROZEN_OCEAN = new Biome(MCVersion.v1_13, 10, "frozen_ocean", Category.OCEAN, Precipitation.SNOW, 0.0F, null);
    public static final Biome FROZEN_RIVER = new Biome(MCVersion.v1_7, 11, "frozen_river", Category.RIVER, Precipitation.SNOW, 0.0F, null);
    public static final Biome SNOWY_TUNDRA = new Biome(MCVersion.v1_7, 12, "snowy_tundra", Category.ICY, Precipitation.SNOW, 0.0F, null);
    public static final Biome SNOWY_MOUNTAINS = new Biome(MCVersion.v1_7, 13, "snowy_mountains", Category.ICY, Precipitation.SNOW, 0.0F, null);
    public static final Biome MUSHROOM_FIELDS = new Biome(MCVersion.v1_7, 14, "mushroom_fields", Category.MUSHROOM, Precipitation.RAIN, 0.9F, null);
    public static final Biome MUSHROOM_FIELD_SHORE = new Biome(MCVersion.v1_7, 15, "mushroom_field_shore", Category.MUSHROOM, Precipitation.RAIN, 0.9F, null);
    public static final Biome BEACH = new Biome(MCVersion.v1_7, 16, "beach", Category.BEACH, Precipitation.RAIN, 0.8F, null);
    public static final Biome DESERT_HILLS = new Biome(MCVersion.v1_7, 17, "desert_hills", Category.DESERT, Precipitation.NONE, 2.0F, null);
    public static final Biome WOODED_HILLS = new Biome(MCVersion.v1_7, 18, "wooded_hills", Category.FOREST, Precipitation.RAIN, 0.7F, null);
    public static final Biome TAIGA_HILLS = new Biome(MCVersion.v1_7, 19, "taiga_hills", Category.TAIGA, Precipitation.RAIN, 0.25F, null);
    public static final Biome MOUNTAIN_EDGE = new Biome(MCVersion.v1_7, 20, "mountain_edge", Category.EXTREME_HILLS, Precipitation.RAIN, 0.2F, null);
    public static final Biome JUNGLE = new Biome(MCVersion.v1_7, 21, "jungle", Category.JUNGLE, Precipitation.RAIN, 0.95F, null);
    public static final Biome JUNGLE_HILLS = new Biome(MCVersion.v1_7, 22, "jungle_hills", Category.JUNGLE, Precipitation.RAIN, 0.95F, null);
    public static final Biome JUNGLE_EDGE = new Biome(MCVersion.v1_7, 23, "jungle_edge", Category.JUNGLE, Precipitation.RAIN, 0.95F, null);
    public static final Biome DEEP_OCEAN = new Biome(MCVersion.v1_13, 24, "deep_ocean", Category.OCEAN, Precipitation.RAIN, 0.5F, null);
    public static final Biome STONE_SHORE = new Biome(MCVersion.v1_7, 25, "stone_shore", Category.NONE, Precipitation.RAIN, 0.2F, null);
    public static final Biome SNOWY_BEACH = new Biome(MCVersion.v1_7, 26, "snowy_beach", Category.BEACH, Precipitation.SNOW, 0.05F, null);
    public static final Biome BIRCH_FOREST = new Biome(MCVersion.v1_7, 27, "birch_forest", Category.FOREST, Precipitation.RAIN, 0.6F, null);
    public static final Biome BIRCH_FOREST_HILLS = new Biome(MCVersion.v1_7, 28, "birch_forest_hills", Category.FOREST, Precipitation.RAIN, 0.6F, null);
    public static final Biome DARK_FOREST = new Biome(MCVersion.v1_7, 29, "dark_forest", Category.FOREST, Precipitation.RAIN, 0.7F, null);
    public static final Biome SNOWY_TAIGA = new Biome(MCVersion.v1_7, 30, "snowy_taiga", Category.TAIGA, Precipitation.SNOW, -0.5F, null);
    public static final Biome SNOWY_TAIGA_HILLS = new Biome(MCVersion.v1_7, 31, "snowy_taiga_hills", Category.TAIGA, Precipitation.SNOW, -0.5F, null);
    public static final Biome GIANT_TREE_TAIGA = new Biome(MCVersion.v1_7, 32, "giant_tree_taiga", Category.TAIGA, Precipitation.RAIN, 0.3F, null);
    public static final Biome GIANT_TREE_TAIGA_HILLS = new Biome(MCVersion.v1_7, 33, "giant_tree_taiga_hills", Category.TAIGA, Precipitation.RAIN, 0.3F, null);
    public static final Biome WOODED_MOUNTAINS = new Biome(MCVersion.v1_7, 34, "wooded_mountains", Category.EXTREME_HILLS, Precipitation.RAIN, 0.2F, null);
    public static final Biome SAVANNA = new Biome(MCVersion.v1_7, 35, "savanna", Category.SAVANNA, Precipitation.NONE, 1.2F, null);
    public static final Biome SAVANNA_PLATEAU = new Biome(MCVersion.v1_7, 36, "savanna_plateau", Category.SAVANNA, Precipitation.NONE, 1.0F, null);
    public static final Biome BADLANDS = new Biome(MCVersion.v1_7, 37, "badlands", Category.MESA, Precipitation.NONE, 2.0F, null);
    public static final Biome WOODED_BADLANDS_PLATEAU = new Biome(MCVersion.v1_7, 38, "wooded_badlands_plateau", Category.MESA, Precipitation.NONE, 2.0F, null);
    public static final Biome BADLANDS_PLATEAU = new Biome(MCVersion.v1_7, 39, "badlands_plateau", Category.MESA, Precipitation.NONE, 2.0F, null);
    public static final Biome SMALL_END_ISLANDS = new Biome(MCVersion.v1_13, 40, "small_end_islands", Category.THE_END, Precipitation.NONE, 0.5F, null);
    public static final Biome END_MIDLANDS = new Biome(MCVersion.v1_13, 41, "end_midlands", Category.THE_END, Precipitation.NONE, 0.5F, null);
    public static final Biome END_HIGHLANDS = new Biome(MCVersion.v1_13, 42, "end_highlands", Category.THE_END, Precipitation.NONE, 0.5F, null);
    public static final Biome END_BARRENS = new Biome(MCVersion.v1_13, 43, "end_barrens", Category.THE_END, Precipitation.NONE, 0.5F, null);
    public static final Biome WARM_OCEAN = new Biome(MCVersion.v1_13, 44, "warm_ocean", Category.OCEAN, Precipitation.RAIN, 0.5F, null);
    public static final Biome LUKEWARM_OCEAN = new Biome(MCVersion.v1_13, 45, "lukewarm_ocean", Category.OCEAN, Precipitation.RAIN, 0.5F, null);
    public static final Biome COLD_OCEAN = new Biome(MCVersion.v1_13, 46, "cold_ocean", Category.OCEAN, Precipitation.RAIN, 0.5F, null);
    public static final Biome DEEP_WARM_OCEAN = new Biome(MCVersion.v1_13, 47, "deep_warm_ocean", Category.OCEAN, Precipitation.RAIN, 0.5F, null);
    public static final Biome DEEP_LUKEWARM_OCEAN = new Biome(MCVersion.v1_13, 48, "deep_lukewarm_ocean", Category.OCEAN, Precipitation.RAIN, 0.5F, null);
    public static final Biome DEEP_COLD_OCEAN = new Biome(MCVersion.v1_13, 49, "deep_cold_ocean", Category.OCEAN, Precipitation.RAIN, 0.5F, null);
    public static final Biome DEEP_FROZEN_OCEAN = new Biome(MCVersion.v1_13, 50, "deep_frozen_ocean", Category.OCEAN, Precipitation.RAIN, 0.5F, null);
    public static final Biome THE_VOID = new Biome(MCVersion.v1_7, 127, "the_void", Category.NONE, Precipitation.NONE, 0.5F, null);
    public static final Biome SUNFLOWER_PLAINS = new Biome(MCVersion.v1_7, 129, "sunflower_plains", Category.PLAINS, Precipitation.RAIN, 0.8F, Biome.PLAINS);
    public static final Biome DESERT_LAKES = new Biome(MCVersion.v1_7, 130, "desert_lakes", Category.DESERT, Precipitation.NONE, 2.0F, Biome.DESERT);
    public static final Biome GRAVELLY_MOUNTAINS = new Biome(MCVersion.v1_7, 131, "gravelly_mountains", Category.EXTREME_HILLS, Precipitation.RAIN, 0.2F, Biome.MOUNTAINS);
    public static final Biome FLOWER_FOREST = new Biome(MCVersion.v1_7, 132, "flower_forest", Category.FOREST, Precipitation.RAIN, 0.7F, Biome.FOREST);
    public static final Biome TAIGA_MOUNTAINS = new Biome(MCVersion.v1_7, 133, "taiga_mountains", Category.TAIGA, Precipitation.RAIN, 0.25F, Biome.TAIGA);
    public static final Biome SWAMP_HILLS = new Biome(MCVersion.v1_7, 134, "swamp_hills", Category.SWAMP, Precipitation.RAIN, 0.8F, Biome.SWAMP);
    public static final Biome ICE_SPIKES = new Biome(MCVersion.v1_7, 140, "ice_spikes", Category.ICY, Precipitation.SNOW, 0.0F, Biome.SNOWY_TUNDRA);
    public static final Biome MODIFIED_JUNGLE = new Biome(MCVersion.v1_7, 149, "modified_jungle", Category.JUNGLE, Precipitation.RAIN, 0.95F, Biome.JUNGLE);
    public static final Biome MODIFIED_JUNGLE_EDGE = new Biome(MCVersion.v1_7, 151, "modified_jungle_edge", Category.JUNGLE, Precipitation.RAIN, 0.95F, Biome.JUNGLE_EDGE);
    public static final Biome TALL_BIRCH_FOREST = new Biome(MCVersion.v1_7, 155, "tall_birch_forest", Category.FOREST, Precipitation.RAIN, 0.6F, Biome.BIRCH_FOREST);
    public static final Biome TALL_BIRCH_HILLS = new Biome(MCVersion.v1_7, 156, "tall_birch_hills", Category.FOREST, Precipitation.RAIN, 0.6F, Biome.BIRCH_FOREST_HILLS);
    public static final Biome DARK_FOREST_HILLS = new Biome(MCVersion.v1_7, 157, "dark_forest_hills", Category.FOREST, Precipitation.RAIN, 0.7F, Biome.DARK_FOREST);
    public static final Biome SNOWY_TAIGA_MOUNTAINS = new Biome(MCVersion.v1_7, 158, "snowy_taiga_mountains", Category.TAIGA, Precipitation.SNOW, -0.5F, Biome.SNOWY_TAIGA);
    public static final Biome GIANT_SPRUCE_TAIGA = new Biome(MCVersion.v1_7, 160, "giant_spruce_taiga", Category.TAIGA, Precipitation.RAIN, 0.25F, Biome.GIANT_TREE_TAIGA);
    public static final Biome GIANT_SPRUCE_TAIGA_HILLS = new Biome(MCVersion.v1_7, 161, "giant_spruce_taiga_hills", Category.TAIGA, Precipitation.RAIN, 0.25F, Biome.GIANT_TREE_TAIGA_HILLS);
    public static final Biome MODIFIED_GRAVELLY_MOUNTAINS = new Biome(MCVersion.v1_7, 162, "modified_gravelly_mountains", Category.EXTREME_HILLS, Precipitation.RAIN, 0.2F, Biome.WOODED_MOUNTAINS);
    public static final Biome SHATTERED_SAVANNA = new Biome(MCVersion.v1_7, 163, "shattered_savanna", Category.SAVANNA, Precipitation.NONE, 1.1F, Biome.SAVANNA);
    public static final Biome SHATTERED_SAVANNA_PLATEAU = new Biome(MCVersion.v1_7, 164, "shattered_savanna_plateau", Category.SAVANNA, Precipitation.NONE, 1.0F, Biome.SAVANNA_PLATEAU);
    public static final Biome ERODED_BADLANDS = new Biome(MCVersion.v1_7, 165, "eroded_badlands", Category.MESA, Precipitation.NONE, 2.0F, Biome.BADLANDS);
    public static final Biome MODIFIED_WOODED_BADLANDS_PLATEAU = new Biome(MCVersion.v1_7, 166, "modified_wooded_badlands_plateau", Category.MESA, Precipitation.NONE, 2.0F, Biome.WOODED_BADLANDS_PLATEAU);
    public static final Biome MODIFIED_BADLANDS_PLATEAU = new Biome(MCVersion.v1_7, 167, "modified_badlands_plateau", Category.MESA, Precipitation.NONE, 2.0F, Biome.BADLANDS_PLATEAU);
    public static final Biome BAMBOO_JUNGLE = new Biome(MCVersion.v1_14, 168, "bamboo_jungle", Category.JUNGLE, Precipitation.RAIN, 0.95F, null);
    public static final Biome BAMBOO_JUNGLE_HILLS = new Biome(MCVersion.v1_14, 169, "bamboo_jungle_hills", Category.JUNGLE, Precipitation.RAIN, 0.95F, null);
    public static final Biome SOUL_SAND_VALLEY = new Biome(MCVersion.v1_16, 170, "soul_sand_valley", Category.NETHER, Precipitation.NONE, 2.0F, null);
    public static final Biome CRIMSON_FOREST = new Biome(MCVersion.v1_16, 171, "crimson_forest", Category.NETHER, Precipitation.NONE, 2.0F, null);
    public static final Biome WARPED_FOREST = new Biome(MCVersion.v1_16, 172, "warped_forest", Category.NETHER, Precipitation.NONE, 2.0F, null);
    public static final Biome BASALT_DELTAS = new Biome(MCVersion.v1_16, 173, "basalt_deltas", Category.NETHER, Precipitation.NONE, 2.0F, null);

}
