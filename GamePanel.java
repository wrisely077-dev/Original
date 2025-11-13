package trial2dgame;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JPanel;

import ai.PathFinder;
import data.SaveLoad;
import entity.Entity;
import entity.Player;
import environment.EnvironmentManager;
import tile.Map;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3;
    public final int tileSize = originalTileSize * scale - 1; // 48x48 tile
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 px (48x16)
    public final int screenHeight = tileSize * maxScreenRow; // 576 px (48x12)

    // WORLD SETTINGS
    public int maxWorldCol;
    public int maxWorldRow;
    public final int maxMap = 10;
    public int currentMap = 0;

    // FPS
    int FPS = 60;

    // SYSTEM
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    public EventHandler eHandler = new EventHandler(this);
    Config config = new Config(this);
    public PathFinder pFinder = new PathFinder(this);
    public EnvironmentManager eManager = new EnvironmentManager(this);
    Map map = new Map(this);
    SaveLoad saveLoad = new SaveLoad(this);
    public EntityGenerator eGenerator = new EntityGenerator(this);
    Thread gameThread;
    public int currentMusicIndex;
    public boolean musicPlaying = true;

    // ENTITY AND OBJECT
    public Player player = new Player(this, keyH);
    public Entity obj[][] = new Entity[maxMap][20];
    public Entity npc[][] = new Entity[maxMap][10];
    public Entity monster[][] = new Entity[maxMap][20];
    ArrayList<Entity> entityList = new ArrayList<>();

    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int characterState = 4;
    public final int optionsState = 5;
    public final int gameOverState = 6;
    public final int transitionState = 7;
    public final int tradeState = 8;
    public final int mapState = 9;
    public final int battleState = 10;

    public Object currentQuiz1;
    public Object quizIndex;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        pFinder = new PathFinder(this);
        pFinder.instantiateNodes();
    }

    // ===========================
    // NEW CODE: Setup + Save
    // ===========================
    public void setupGame(boolean loadSave) {
        if (loadSave) {
            saveLoad.load();  // load saved objects, NPCs, monsters, player, etc.
        } else {
            aSetter.setObject();
            aSetter.setNPC();
            aSetter.setMonster();
        }
        eManager.setup();
        gameState = titleState;
    }

    public void saveGame() {
        saveLoad.save();
        System.out.println("Game saved successfully.");
    }

    public void resetGame(boolean restart) {
        player.setDefaultPositions();
        player.restoreStatus();
        player.resetCounter();
        aSetter.setNPC();
        aSetter.setMonster();
        eManager.setup();
        if (restart) {
            player.setDefaultValues();
            aSetter.setObject();
            eManager.lighting.resetDay();
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();

                // Stop music if in title state
                if (gameState == titleState && musicPlaying) {
                    stopMusic();
                }

                repaint();
                delta--;
                drawCount++;
            }
            if (timer >= 1000000000) {
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        if (gameState == playState) {
            // PLAYER
            player.update();

            // EVENTS
            eHandler.checkEvent();

            // NPC
            for (int i = 0; i < npc[1].length; i++) {
                if (npc[currentMap][i] != null) {
                    npc[currentMap][i].update();
                }
            }

            // MONSTER
            boolean allDead = true;
            for (int i = 0; i < monster[1].length; i++) {
                if (monster[currentMap][i] != null) {
                    if (monster[currentMap][i].alive && !monster[currentMap][i].dying) {
                        monster[currentMap][i].update();
                        allDead = false;
                    }
                    if (!monster[currentMap][i].alive) {
                        monster[currentMap][i] = null;
                    }
                }
            }

            // === NEW CODE: Check wave completion ===
            if (allDead && !aSetter.allMonstersDefeated) {
                aSetter.allMonstersDefeated = true;
                aSetter.startNextWave(currentMap);
            }

            eManager.update();
        }

        if (gameState == pauseState) {
            // nothing, paused
        }

        // Allow Enter to close dialogue
        if (gameState == dialogueState && keyH.enterPressed) {
            gameState = playState;
        }

        // Stop music when in title state
        if (gameState == titleState && musicPlaying) {
            stopMusic();
        }

        // Handle quiz answer in battle state
        if (gameState == battleState) {
            
        }

        // RESET ENTER KEY AFTER ALL UPDATES
        keyH.enterPressed = false;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        long drawStart = 0;
        if (keyH.showDebugText == true) {
            drawStart = System.nanoTime();
        }
        drawStart = System.nanoTime();

        if (gameState == titleState) {
            ui.draw(g2);
        } else if (gameState == mapState) {
            map.drawFullMapScreen(g2);
        } else {
            // Draw the game world (tiles, entities, objects, environment)
            drawGameWorld(g2);

            // MINI MAP
            map.drawMiniMap(g2);

            // UI
            ui.draw(g2);
        }

        // DEBUG
        if (keyH.showDebugText == true) {
            long drawEnd = System.nanoTime();
            long passed = drawEnd - drawStart;

            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            g2.setColor(Color.white);
            int x = 10;
            int y = 400;
            int lineHeight = 20;

            g2.drawString("WorldX" + player.worldX, x, y);
            y += lineHeight;
            g2.drawString("WorldY" + player.worldY, x, y);
            y += lineHeight;
            g2.drawString("Col" + (player.worldX + player.solidArea.x) / tileSize, x, y);
            y += lineHeight;
            g2.drawString("Row" + (player.worldY + player.solidArea.y) / tileSize, x, y);
            y += lineHeight;
            g2.drawString("Draw Time: " + passed, x, y);
        }
        g.dispose();
    }

    // ===========================
    // Draw Game World
    // ===========================
    public void drawGameWorld(Graphics2D g2) {
        // TILE
        tileM.draw(g2);

        // ADD ENTITIES TO THE LIST
        entityList.add(player);

        for (int i = 0; i < npc[1].length; i++) {
            if (npc[currentMap][i] != null) {
                entityList.add(npc[currentMap][i]);
            }
        }

        for (int i = 0; i < obj[1].length; i++) {
            if (obj[currentMap][i] != null) {
                entityList.add(obj[currentMap][i]);
            }
        }
        for (int i = 0; i < monster[1].length; i++) {
            if (monster[currentMap][i] != null) {
                entityList.add(monster[currentMap][i]);
            }
        }

        // SORT
        Collections.sort(entityList, new Comparator<Entity>() {
            @Override
            public int compare(Entity e1, Entity e2) {
                return Integer.compare(e1.worldY, e2.worldY);
            }
        });

        // DRAW ENTITIES
        for (int i = 0; i < entityList.size(); i++) {
            entityList.get(i).draw(g2);
        }
        entityList.clear();

        // ENVIRONMENT
        eManager.draw(g2);
    }

    public void playMusic(int i) {
        if (!musicPlaying) {
            currentMusicIndex = i;
            music.setFile(i);
            music.setVolume(-30.0f);
            music.play();
            music.loop();
            musicPlaying = true;
        }
    }

    public void stopMusic() {
        music.stop();
        musicPlaying = false;
    }

    public void playSE(int i) {
        se.setFile(i);

        int adjustment = 0;
        switch (i) {
            case 1 -> adjustment = -5;  // coin sound
            case 5 -> adjustment = -30; // monster hit
            case 6 -> adjustment = -10; // player hit
            case 7 -> adjustment = +6;  // claw swing
            default -> adjustment = 0;
        }
        se.setVolumeByScaleAndAdjustment(adjustment);
        se.play();
    }
}