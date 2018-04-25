/*
 * Copyright 2017 Carlos Rodriguez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package game;

import java.util.ArrayList;

import javax.sound.midi.Sequence;

import org.lwjgl.opengl.Display;

import engine.ResourceLoader;
import engine.audio.AudioUtil;
import engine.core.*;
import engine.rendering.*;
import game.enemies.*;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.1
 * @since 2017
 */
public class Auschwitz implements Game {
	
    private static final int EPISODE_1 = 1;
    private static final int EPISODE_2 = 2;
    private static final int EPISODE_3 = 2;
    private static final int FOV = 70;
    private static Level level;
    private static Shader shader;

    public static int levelNum;
    public static int startingLevel;
    private static ArrayList<Sequence> playlist = new ArrayList<Sequence>();
    public static int track;
    public static int currentEpisode;
    private static boolean isRunning;

    /**
     * The constructor method of the compiling game.
     */
    public Auschwitz() {
        shader = PhongShader.getInstance();
        Transform.setProjection(FOV, Display.getWidth(), Display.getHeight(), 0.01f, 1000f);

        for (int i = 0; i < 13; i++) {
        	playlist.add(ResourceLoader.loadMidi("THEME" + i));
		}

        track = startingLevel - 1;
        levelNum = startingLevel - 1;
        loadLevel(1);

        isRunning = true;
    }
    
    /**
     * Sets the starting level depending of what level you want to start;
     * @param level
     */
    public static void setStartingLevel(int level) {
    	startingLevel = level;
    }

    /**
     * Checks all the inputs.
     */
    public void input() {
        level.input();

        /**
        if (Input.getKey(Input.KEY_1)) {
            System.exit(0);
        }

        if (Input.getKeyDown(Input.KEY_R)) {
            AudioUtil.playMidi(playlist.get(track));
        }
        */
    }

    /**
     * Updates everything renderer every single frame.
     */
    public void update() {
        if (isRunning) {
            level.update();
        }
    }

    /**
     * Renders everything every on screen.
     */
    public void render() {
        if (isRunning) {
            level.render();
        }
    }

    /**
     * Updates everything visible calling the main shader to refresh all the
     * code information.
     * @param worldMatrix The world information in matrix.
     * @param projectedMatrix The projection data in matrix.
     * @param material The material (like textures) to update.
     */
    public static void updateShader(Matrix4f worldMatrix, Matrix4f projectedMatrix, Material material) {
        shader.bind();
        shader.updateUniforms(worldMatrix, projectedMatrix, material);
    }

    /**
     * Load the level and also charges the next level when the last end.
     * @param offset count of level offset by offset.
     */
	@SuppressWarnings("static-access")
	public static void loadLevel(int offset) {
        try {
        	int secrets = 0;
            int deadMonsters = 0;
            int totalSecrets = 0;
            int totalMonsters = 0;
            int bulletTemp = 0;
            int shellTemp = 0;
            int armoriTemp = 0;
            boolean displayMonsters = false;
            boolean shotgunTemp = false;
            boolean machinegunTemp = false;
            boolean superShotgunTemp = false;
            boolean armorbTemp = false;
            String weaponStateTemp = "";
            String sector;

            if (level != null) {
                totalMonsters = level.getNaziSoldiers().size() + level.getSsSoldiers().size()
                		+ level.getDogs().size() + + level.getNaziSergeants().size();
                
                totalSecrets = level.getSecretWalls().size();
                
                for (SecretWall secret : level.getSecretWalls()) {
                    if (secret.opens()) {
                    	secrets++;
                    }
                }

                for (NaziSoldier monster : level.getNaziSoldiers()) {
                    if (!monster.isAlive()) {
                        deadMonsters++;
                    }
                }
                
                for (SsSoldier ssSoldier : level.getSsSoldiers()) {
                    if (!ssSoldier.isAlive()) {
                        deadMonsters++;
                    }
                }
                
                for (Dog dog : level.getDogs()) {
                    if (!dog.isAlive()) {
                        deadMonsters++;
                    }
                }
                
                for (NaziSergeant naziSargent : level.getNaziSergeants()) {
                    if (!naziSargent.isAlive()) {
                        deadMonsters++;
                    }
                }

                displayMonsters = true;
                
                armoriTemp = level.getPlayer().getArmori();
                bulletTemp = level.getPlayer().getBullets();
                shellTemp = level.getPlayer().getShells();
                shotgunTemp = level.getPlayer().getShotgun();
                machinegunTemp = level.getPlayer().getMachinegun();
                superShotgunTemp = level.getPlayer().getSuperShotgun();
                weaponStateTemp = level.getPlayer().getWeaponState();
            }

            if(levelNum > 9) {
            	currentEpisode = EPISODE_2;
            } else if(levelNum > 19) {
            	currentEpisode = EPISODE_3;
            } else {
            	currentEpisode = EPISODE_1;
            }
            
            levelNum += offset;
            level = new Level(ResourceLoader.loadBitmap("level" + levelNum).flipX(), 
            		new Material(ResourceLoader.loadTexture("mapTexture" + currentEpisode)));

            if((levelNum/2) * 2 == levelNum) {
            	sector = "B.";
            }else {
            	sector = "A.";
            }
            
            if(level.getPlayer().getArmori() == 0) {
            	if(armoriTemp == 0) {
            		level.getPlayer().setArmori(0);
            	}
            	level.getPlayer().setArmori(armoriTemp);
            }
            if(level.getPlayer().getBullets() == 0) {
            	if(bulletTemp == 0) {
            		level.getPlayer().setBullets(20);
            	}
            	level.getPlayer().setBullets(bulletTemp);
            }
            if(level.getPlayer().getShells() == 0) {
            	if(shellTemp == 0) {
            		level.getPlayer().setShells(20);
            	}
            	level.getPlayer().setShells(shellTemp);
            }
            level.getPlayer().setShotgun(shotgunTemp);
            level.getPlayer().setMachinegun(machinegunTemp);
            level.getPlayer().setSuperShotgun(superShotgunTemp);
            level.getPlayer().setArmorb(armorbTemp);
            level.getPlayer().setWeaponState(weaponStateTemp);
            
            track += offset;

            AudioUtil.playMidi(playlist.get(track));

            while (track >= playlist.size()) {
                track -= playlist.size();
            }

            System.out.println("=============================");
            System.out.println("Level " + levelNum + " floor " + levelNum + sector);
            System.out.println("=============================");

            if (displayMonsters) {    
            	System.out.println("Killed " + deadMonsters + "/" + totalMonsters + " baddies: " +
            	((float) deadMonsters / (float) totalMonsters) * 100f + "%");        	
            	System.out.println("Secrets " + secrets + "/" + totalSecrets + " secrets: " +
                    	((float) secrets / (float) totalSecrets) * 100f + "%");
            	
            	if(level.getPlayer().getWeaponState() == level.getPlayer().HAND){
            		level.getPlayer().gotHand();
            	}else if(level.getPlayer().getWeaponState() == level.getPlayer().PISTOL) {
            		level.getPlayer().gotPistol();
            	}else if(level.getPlayer().getWeaponState() == level.getPlayer().MACHINEGUN && machinegunTemp == true) {
            		level.getPlayer().gotMachinegun();
            	}else if(level.getPlayer().getWeaponState() == level.getPlayer().SHOTGUN && shotgunTemp == true) {
            		level.getPlayer().gotShotgun();
            	}else if(level.getPlayer().getWeaponState() == level.getPlayer().SUPER_SHOTGUN && superShotgunTemp == true) {
            		level.getPlayer().gotSShotgun();
            	}
            }
        } catch (Exception ex) {
            isRunning = false;
            System.out.println("GAME OVER!");
            AudioUtil.stopMidi();
        }

    }

    /**
     * Gets the actual level.
     * @return Level.
     */
    public static Level getLevel() {
        return level;
    }

    /**
     * Gets the shader that has been used.
     * @return Shader.
     */
    public static Shader getShader() {
        return shader;
    }

    /**
     * Allow to stop the game rendering without stop all the program.
     * @param value True or false.
     */
    public static void setIsRunning(boolean value) {
        isRunning = value;
    }
}