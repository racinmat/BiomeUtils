package kaptainwutax.biomeutils.layer;

import kaptainwutax.biomeutils.layer.composite.VoronoiLayer;
import kaptainwutax.biomeutils.layer.scale.ScaleLayer;
import kaptainwutax.biomeutils.layer.water.OceanTemperatureLayer;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.prng.SeedMixer;

import java.util.HashMap;
import java.util.Map;

public abstract class BiomeLayer {
    public static int scaleCounter = 1;
    public static int oceanScaleCounter = -100;
    public static int layerIdCounter = 0;

    private final MCVersion version;
    private final BiomeLayer[] parents;
    private final long layerSeed;
    private long localSeed;

    protected int scale;
    protected int layerId;

    private Map<Long, Integer> cache = new HashMap<>();

    public BiomeLayer(MCVersion version, long worldSeed, long salt, BiomeLayer... parents) {
        this.version = version;
        this.layerSeed = getLayerSeed(worldSeed, salt);
        this.parents = parents;
        setScaleAndId();
    }

    public BiomeLayer(MCVersion version, long worldSeed, long salt) {
        this(version, worldSeed, salt, (BiomeLayer)null);
    }

    public MCVersion getVersion() {
        return this.version;
    }

    public boolean hasParent() {
        return this.parents.length > 0;
    }

    public BiomeLayer getParent() {
        return this.getParent(0);
    }

    public BiomeLayer getParent(int id) {
        return this.parents[id];
    }

    public boolean isMergingLayer() {
        return this.parents.length > 1;
    }

    public BiomeLayer[] getParents() {
        return this.parents;
    }

    public int get(int x, int z) {
        long v = x & 0xFFFFFFFFL | ((long) z & 0xFFFFFFFFL) << 32;
        Integer r = this.cache.get(v);

        if (r == null) {
            r = this.sample(x, z);
            this.cache.put(v, r);
            return r;
        }

        return r;
    }

    public abstract int sample(int x, int z);

    public static long getLayerSeed(long worldSeed, long salt) {
        long midSalt = SeedMixer.mixSeed(salt, salt);
        midSalt = SeedMixer.mixSeed(midSalt, salt);
        midSalt = SeedMixer.mixSeed(midSalt, salt);
        long layerSeed = SeedMixer.mixSeed(worldSeed, midSalt);
        layerSeed = SeedMixer.mixSeed(layerSeed, midSalt);
        layerSeed = SeedMixer.mixSeed(layerSeed, midSalt);
        return layerSeed;
    }

    public static long getLocalSeed(long layerSeed, int x, int z) {
        layerSeed = SeedMixer.mixSeed(layerSeed, x);
        layerSeed = SeedMixer.mixSeed(layerSeed, z);
        layerSeed = SeedMixer.mixSeed(layerSeed, x);
        layerSeed = SeedMixer.mixSeed(layerSeed, z);
        return layerSeed;
    }

    public static long getLocalSeed(long worldSeed, long salt, int x, int z) {
        return getLocalSeed(getLayerSeed(worldSeed, salt), x, z);
    }

    public int getLayerId() {
        return this.layerId;
    }

    public int getScaleIndex() {
        return this.scale;
    }

    public int getScale() {
        if(this.scale < 0) {
            return getPow2(scaleCounter-(scale-oceanScaleCounter)-4);
        }

        return getPow2(scaleCounter - scale);
    }

    public int getMaxScale() {
        return getPow2(scaleCounter);
    }

    public int getStackSize() {
        return layerIdCounter;
    }

    public int getPow2(int power) {
        int res = 1;
        for (int i = 0; i < power; i++) {
            res *= 2;
        }
        return res;
    }

    public void setScaleAndId(){
        this.layerId = layerIdCounter;
        layerIdCounter++;
        if (this.getParent() == null) {
            this.scale = (this instanceof OceanTemperatureLayer)?-100: 0;

            if(this.isMergingLayer()) {
                for (BiomeLayer biomeLayer : this.getParents()) {
                    this.scale = Math.max(biomeLayer.getScaleIndex(), this.scale);
                }
            }
        } else {
            this.scale = (this instanceof VoronoiLayer)?this.getParent().getScaleIndex()+2: (this instanceof ScaleLayer) ? this.getParent().getScaleIndex() + 1 : this.getParent().getScaleIndex();
        }
        oceanScaleCounter=Math.min(oceanScaleCounter,scale);
        scaleCounter = Math.max(scaleCounter, scale);
    }

    public void setSeed(int x, int z) {
        this.localSeed = BiomeLayer.getLocalSeed(this.layerSeed, x, z);
    }

    public int nextInt(int bound) {
        int i = (int) Math.floorMod(this.localSeed >> 24, bound);
        this.localSeed = SeedMixer.mixSeed(this.localSeed, this.layerSeed);
        return i;
    }

    public int choose(int a, int b) {
        return this.nextInt(2) == 0 ? a : b;
    }

    public int choose(int a, int b, int c, int d) {
        int i = this.nextInt(4);
        return i == 0 ? a : i == 1 ? b : i == 2 ? c : d;
    }
}
