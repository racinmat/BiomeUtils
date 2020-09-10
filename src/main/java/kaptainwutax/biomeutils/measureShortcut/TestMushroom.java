package kaptainwutax.biomeutils.measureShortcut;


import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.Stats;
import kaptainwutax.biomeutils.layer.BiomeLayer;
import kaptainwutax.biomeutils.layer.land.BaseBiomesLayer;
import kaptainwutax.biomeutils.layer.land.ContinentLayer;
import kaptainwutax.biomeutils.layer.land.MushroomLayer;
import kaptainwutax.biomeutils.layer.temperature.ClimateLayer;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.seedutils.mc.MCVersion;


public class TestMushroom {
    public final static MCVersion VERSION = MCVersion.v1_16;
    public final static OverworldBiomeSource SOURCE = new OverworldBiomeSource(VERSION, 0L);
    public final static BiomeLayer CONTINENT = getLayer(ContinentLayer.class);
    public final static BiomeLayer COLD = getLayer(ClimateLayer.Cold.class);
    public final static BiomeLayer BASEBIOMES = getLayer(BaseBiomesLayer.class);
    public final static BiomeLayer SPECIAL = getLayer(ClimateLayer.Special.class);
    public final static BiomeLayer MUSHROOM = getLayer(MushroomLayer.class);

    public static BiomeLayer getLayer(Class<? extends BiomeLayer> layerClass) {
        for (int i = 0; i < SOURCE.getLayers().size(); i++) {
            if (SOURCE.getLayer(i).getClass().equals(layerClass)) return SOURCE.getLayer(i);
        }
        return SOURCE.voronoi;
    }

    public static long getLocalSeed(BiomeLayer biomeLayer, long seed, int posX, int posZ) {
        long layerSeed = BiomeLayer.getLayerSeed(seed, biomeLayer.salt);
        return BiomeLayer.getLocalSeed(layerSeed, posX, posZ);
    }

    public static void main(String[] args) {
        Biome biome = Biome.MUSHROOM_FIELDS;
        int count = 0;
        int count2 = 0;
        int[][] center = {{0, 0}};
        long counter=0;
        for (long seed = 0; seed < Long.MAX_VALUE; seed++) { // 2^64
            counter++;
            long localSeed;

            // force mushroom at 0, 0},{0, 1}, {0, -1},{-1, 0}, {1, 0}
            boolean bad = false;
            for (int[] pos : center) {
                // INCREASE MUSHROOM
                localSeed = getLocalSeed(MUSHROOM, seed, pos[0], pos[1]);
                if (Math.floorMod(localSeed >> 24, 100) != 0) {
                    bad = true;
                    break;
                }
            }
            if (bad) continue;

            count++;
            OverworldBiomeSource source = new OverworldBiomeSource(MCVersion.v1_16, seed, 4, 4);

            if (source.getBiome(0, 0, 0) == biome) {
                System.out.println(seed);
                count2++;
            }
            if (counter > 150000) {
                System.out.println("total branch 1: desert, savannah, plains " + Stats.getCount("count1") / (float) Stats.getCount("total") * 100 + "%");
                System.out.println("total branch 2: forest, daark forest, plains, mountains, birch forest, swamp " + Stats.getCount("count2") / (float) Stats.getCount("total") * 100 + "%");
                System.out.println("total branch 3: forest, mountains, taiga, plains " + Stats.getCount("count3") / (float) Stats.getCount("total") * 100 + "%");
                System.out.println("total branch 4: snowy tundra, snowy taiga " + Stats.getCount("count4") / (float) Stats.getCount("total") * 100 + "%");
                System.out.println("total mushroom branch " + Stats.getCount("shroom") / (float) Stats.getCount("total") * 100 + "%");
                System.out.println("total giant taiga " + Stats.getCount("taiga") / (float) Stats.getCount("total") * 100 + "%");
                System.out.println("total mesaa " + Stats.getCount("mesa") / (float) Stats.getCount("total") * 100 + "%");
                System.out.println("total jungle " + Stats.getCount("jungle") / (float) Stats.getCount("total") * 100 + "%");
                System.out.println("total ocean or mushroom " + Stats.getCount("shroomOrOcean") / (float) Stats.getCount("total") * 100 + "%");
                System.out.println();
                System.out.println("###################CLIMATE#######################3");
                System.out.println();
                System.out.println("total Cold Forest " + Stats.getCount("coldForest") / (float) Stats.getCount("totalCold") * 100 + "%");
                System.out.println("total Cold Mountains " + Stats.getCount("coldMountains") / (float) Stats.getCount("totalCold") * 100 + "%");
                System.out.println("total Cold Plains " + Stats.getCount("coldPlains") / (float) Stats.getCount("totalCold") * 100 + "%");
                System.out.println();
                System.out.println("total Temp Normal " + Stats.getCount("tempNormal") / (float) Stats.getCount("totalTemp") * 100 + "%");
                System.out.println("total Temp Desert " + Stats.getCount("tempDesert") / (float) Stats.getCount("totalTemp") * 100 + "%");
                System.out.println();
                System.out.println("total Cool Normal " + Stats.getCount("coolNormal") / (float) Stats.getCount("totalCool") * 100 + "%");
                System.out.println("total Cool Mountains " + Stats.getCount("coolMountains") / (float) Stats.getCount("totalCool") * 100 + "%");
                System.out.println();
                System.out.println("total Special Normal " + Stats.getCount("specialNormal") / (float) Stats.getCount("totalSpe") * 100 + "%");
                System.out.println("total Special Spec " + Stats.getCount("specialSpe") / (float) Stats.getCount("totalSpe") * 100 + "%");
                System.out.println();
                System.out.println("###################BIOME#######################3");
                System.out.println("Targeted " + biome.getName() + " over total: " + count2 / (float) count * 100 + "% compared to standard deviated probability:" + count2 / (float) seed * 100 + "%");

                break;
            }
        }
    }
}