package trial2dgame;

import entity.Entity;
import entity.NPC_Merchant;
import entity.NPC_OldMan;
import monster.MON_GreenSlime;
import monster.MON_LHound;
import object.OBJ_Chest;
import object.OBJ_Coin;
import object.OBJ_Door;
import object.OBJ_Door_Purple;
import object.OBJ_Key;
import object.OBJ_Key_Purple;
import object.OBJ_Lantern;
import object.OBJ_Potion_Red;
import object.OBJ_Scroll_Violet;
import object.OBJ_Shield_Blue;

import java.util.Random;
import java.awt.Point;
import java.util.HashSet;

public class AssetSetter {

    GamePanel gp;
    public int currentWave = 0;
    private final int totalWaves = 3;
    public boolean allMonstersDefeated = false;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    // ===========================
    // HELPER METHODS
    // ===========================

    private void addObject(int mapNum, int index, Entity obj, int tileX, int tileY) {
        gp.obj[mapNum][index] = obj;
        gp.obj[mapNum][index].worldX = tileX * gp.tileSize;
        gp.obj[mapNum][index].worldY = tileY * gp.tileSize;
    }

    private void addNPC(int mapNum, int index, Entity npc, int tileX, int tileY) {
        gp.npc[mapNum][index] = npc;
        gp.npc[mapNum][index].worldX = tileX * gp.tileSize;
        gp.npc[mapNum][index].worldY = tileY * gp.tileSize;
    }

    private void addMonster(int mapNum, int index, Entity monster, int tileX, int tileY) {
        gp.monster[mapNum][index] = monster;
        gp.monster[mapNum][index].worldX = tileX * gp.tileSize;
        gp.monster[mapNum][index].worldY = tileY * gp.tileSize;
    }

    // ===========================
    // RANDOM MONSTER SPAWNER
    // ===========================

    public void spawnRandomMonsters(int mapNum, Class<? extends Entity> monsterClass, int count) {
        Random rand = new Random();
        HashSet<Point> usedTiles = new HashSet<>();

        for (int i = 0; i < count; ) {
            int randomTileX = rand.nextInt(gp.maxWorldCol);
            int randomTileY = rand.nextInt(gp.maxWorldRow);
            Point pos = new Point(randomTileX, randomTileY);

            if (usedTiles.contains(pos)) continue;
            boolean blocked = gp.tileM.tile[gp.tileM.mapTileNum[mapNum][randomTileX][randomTileY]].collision;
            if (blocked) continue;

            Entity monster = null;
            try {
                monster = monsterClass.getConstructor(GamePanel.class).newInstance(gp);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            // find empty slot
            for (int j = 0; j < gp.monster[mapNum].length; j++) {
                if (gp.monster[mapNum][j] == null) {
                    addMonster(mapNum, j, monster, randomTileX, randomTileY);
                    usedTiles.add(pos);
                    i++;
                    break;
                }
            }
        }
    }

    // ===========================
    // OBJECTS
    // ===========================

    public void setObject() {
//        int mapNum = 0, i = 0;
//        addObject(mapNum, i++, new OBJ_Key(gp), 17, 10);
//        addObject(mapNum, i++, new OBJ_Door(gp), 4, 33);
//        addObject(mapNum, i++, new OBJ_Shield_Blue(gp), 33, 12);
//        addObject(mapNum, i++, new OBJ_Potion_Red(gp), 28, 17);
//
//        // MAP 1
//        mapNum = 1;
//        i = 0;
//        OBJ_Chest chest1 = new OBJ_Chest(gp);
//        chest1.setLoot(new OBJ_Key(gp));
//        addObject(mapNum, i++, chest1, 33, 28);
//
//        OBJ_Chest chest2 = new OBJ_Chest(gp);
//        chest2.setLoot(new OBJ_Potion_Red(gp));
//        addObject(mapNum, i++, chest2, 32, 28);
//
//        addObject(mapNum, i++, new OBJ_Lantern(gp), 30, 35);
//        addObject(mapNum, i++, new OBJ_Potion_Red(gp), 16, 28);
//        addObject(mapNum, i++, new OBJ_Coin(gp), 18, 30);
//        addObject(mapNum, i++, new OBJ_Coin(gp), 19, 30);
//        addObject(mapNum, i++, new OBJ_Coin(gp), 20, 30);
//        addObject(mapNum, i++, new OBJ_Coin(gp), 21, 30);
//        addObject(mapNum, i++, new OBJ_Coin(gp), 22, 30);
//        addObject(mapNum, i++, new OBJ_Coin(gp), 23, 30);
//
//        // MAP 2
//        mapNum = 2;
//        i = 0;
//        OBJ_Chest chest3 = new OBJ_Chest(gp);
//        chest3.setLoot(new OBJ_Lantern(gp));
//        addObject(mapNum, i++, chest3, 27, 16);
//        OBJ_Chest chest4 = new OBJ_Chest(gp);
//        chest4.setLoot(new OBJ_Scroll_Violet(gp));
//        addObject(mapNum, i++, chest4, 36, 17);
//
//        // MAP 3
//        mapNum = 3;
//        i = 0;
//        addObject(mapNum, i++, new OBJ_Door_Purple(gp), 25, 22);
//        addObject(mapNum, i++, new OBJ_Key_Purple(gp), 20, 24);
    }

    // ===========================
    // NPC
    // ===========================

    public void setNPC() {
        int mapNum = 0, i = 0;
        addNPC(mapNum, i++, new NPC_OldMan(gp), 25, 14);

        // MAP 1
        mapNum = 1;
        i = 0;
        addNPC(mapNum, i++, new NPC_Merchant(gp), 25, 31);
    }

    // ===========================
    // MONSTER WAVES
    // ===========================

    public void setMonster() {
        currentWave = 0; // reset for new map
        startNextWave(0); // starts wave 1 automatically
    }

    public void startNextWave(int mapNum) {
        if (currentWave >= totalWaves) return; // all waves done

        currentWave++;
        int monsterCount = 0;
        Class<? extends Entity> monsterType = null;

        if (mapNum == 0) {
            monsterType = MON_GreenSlime.class;
            switch (currentWave) {
                case 1 -> monsterCount = 5;
                case 2 -> monsterCount = 6;
                case 3 -> monsterCount = 7;
            }
        } 
        else if (mapNum == 1) {
            monsterType = MON_LHound.class;
            switch (currentWave) {
                case 1 -> monsterCount = 3;
                case 2 -> monsterCount = 4;
                case 3 -> monsterCount = 5;
            }
        }

        if (monsterType != null && monsterCount > 0) {
            spawnRandomMonsters(mapNum, monsterType, monsterCount);
            allMonstersDefeated = false;
        }
    }
}