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

import static engine.core.CoreEngine.*;

import java.util.ArrayList;

import javax.sound.sampled.Clip;

import engine.audio.AudioUtil;
import engine.components.DirectionalLight;
import engine.components.GameComponent;
import engine.components.MeshRenderer;
import engine.core.Debug;
import engine.core.GameObject;
import engine.core.Input;
import engine.core.Time;
import engine.core.Transform;
import engine.core.Util;
import engine.core.Vector2f;
import engine.core.Vector3f;
import engine.physics.PhysicsUtil;
import engine.rendering.Bitmap;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.RenderingEngine;
import engine.rendering.Shader;
import engine.rendering.Vertex;
import game.doors.Door;
import game.doors.SecretWall;
import game.enemies.Dog;
import game.enemies.Ghost;
import game.enemies.NaziSergeant;
import game.enemies.NaziSoldier;
import game.enemies.SsSoldier;
import game.objects.Barrel;
import game.objects.Bones;
import game.objects.Clock;
import game.objects.DeadJew;
import game.objects.Furnace;
import game.objects.Hanged;
import game.objects.Kitchen;
import game.objects.Lamp;
import game.objects.Lantern;
import game.objects.LightBeam;
import game.objects.Pendule;
import game.objects.Pillar;
import game.objects.Pipe;
import game.objects.Table;
import game.objects.Tree;
import game.powerUp.Armor;
import game.powerUp.Bag;
import game.powerUp.Bullet;
import game.powerUp.Food;
import game.powerUp.Helmet;
import game.powerUp.Machinegun;
import game.powerUp.Medkit;
import game.powerUp.Shotgun;
import game.powerUp.SuperShotgun;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.6
 * @since 2017
 */
public class Level extends GameComponent {

	//Constants
    public static final float SPOT_WIDTH = 1;
    public static final float SPOT_LENGTH = 1;
    public static final float LEVEL_HEIGHT = 1;

    private static final float NUM_TEX_X = 4f;
    private static final float NUM_TEX_Y = 4f;
    
    private static final float MELEE_RANGE = 0.55f;
    private static final float BULLET_RANGE = 2f;
    private static final float SHELL_RANGE = 3f;

    private static final String PLAYER_RES_LOC = "player/";
    
    //Player's sounds
    private static final Clip misuseNoise = AudioUtil.loadAudio(PLAYER_RES_LOC + "OOF");
    private static final Clip punchNoise = AudioUtil.loadAudio(PLAYER_RES_LOC + "PLSPNCH6");
    private static final Clip punchSolidNoise = AudioUtil.loadAudio(PLAYER_RES_LOC + "PUNCH2");
    private static final Clip barrelNoise = AudioUtil.loadAudio("barrel/BARRELZ");

    //Remove list
    private static ArrayList<Medkit> removeMedkitList;
    private static ArrayList<Food> removeFoodList;
    private static ArrayList<Bullet> removeBulletList;
    private static ArrayList<Bag> removeBagList;
    private static ArrayList<Shotgun> removeShotgunList;
    private static ArrayList<Machinegun> removeMachineGunList;
    private static ArrayList<Ghost> removeGhostList;
    private static ArrayList<Armor> removeArmorList;
    private static ArrayList<SuperShotgun> removeSuperShotgunList;
    private static ArrayList<Helmet> removeHelmets;
    private static ArrayList<Barrel> removeBarrels;
    
    //Player
    private static Player player;

    //Level brain
    private ArrayList<Vector3f> exitPoints;
    private ArrayList<Integer> exitOffsets;
    private ArrayList<Vector2f> collisionPosStart;
    private ArrayList<Vector2f> collisionPosEnd;
    
    //Doors
    private ArrayList<Door> doors;
    private ArrayList<SecretWall> secretWalls;
    
    //Power-Ups
    private ArrayList<Shotgun> shotguns;
    private ArrayList<Medkit> medkits;
    private ArrayList<Food> foods;
    private ArrayList<Bullet> bullets;
    private ArrayList<Bag> bags;
    private ArrayList<Machinegun> machineguns;
    private ArrayList<Armor> armors;
    private ArrayList<SuperShotgun> superShotguns;
    private ArrayList<Helmet> helmets;
    
    //Static objects
    private ArrayList<Tree> trees;
    private ArrayList<Lantern> flares;
    private ArrayList<LightBeam> lightPoints;
    private ArrayList<Bones> bones;
    private ArrayList<NaziSoldier> deadNazi;
    private ArrayList<DeadJew> deadJews;
    private ArrayList<Table> tables;
    private ArrayList<Pipe> pipes;
    private ArrayList<Pendule> pendules;
    private ArrayList<Lamp> lamps;
    private ArrayList<Hanged> hangeds;
    private ArrayList<Pillar> pillars;
    private ArrayList<Clock> clocks;
    private ArrayList<Furnace> furnaces;
    private ArrayList<Kitchen> kitchens;
    private ArrayList<Barrel> barrels;
   
    //Enemies
    private ArrayList<NaziSoldier> naziSoldiers;
    private ArrayList<Dog> dogs;
    private ArrayList<SsSoldier> ssSoldiers;
    private ArrayList<NaziSergeant> naziSeargeants;
    private ArrayList<Ghost> ghosts;

    //Level
    private Mesh m_geometry;
    private Bitmap m_bitmap;
    private Material m_material;
    private Transform m_transform;
    private MeshRenderer m_meshRenderer;
    private GameObject m_objects;

    /**
     * Constructor of the level in the game.
     * @param bitmap to load and use.
     * @param material to load and use.
     */
    public Level(Bitmap bitmap, Material material) {	
        if(m_objects == null) this.m_objects = new GameObject();
        
        //Level stuff
        if(m_bitmap == null) this.m_bitmap = bitmap;
        if(m_material == null) this.m_material = material;
        if(m_transform == null) this.m_transform = new Transform();
        if(collisionPosStart == null) this.collisionPosStart = new ArrayList<Vector2f>();
        if(collisionPosEnd == null) this.collisionPosEnd = new ArrayList<Vector2f>();
        
        generateLevel(m_renderingEngine);
        
        //Player
        m_objects.add(player);
        //Enemies
        m_objects.add(naziSoldiers);
        m_objects.add(ssSoldiers);
        m_objects.add(naziSeargeants);
        m_objects.add(dogs);
        m_objects.add(ghosts);
        //Objects
        m_objects.add(doors);
        m_objects.add(secretWalls);
        m_objects.add(trees);
        m_objects.add(flares);
        m_objects.add(lightPoints);
        m_objects.add(bones);
        m_objects.add(tables);
        m_objects.add(deadNazi);
        m_objects.add(deadJews);
        m_objects.add(pipes);
        m_objects.add(pendules);
        m_objects.add(lamps);
        m_objects.add(hangeds);
        m_objects.add(pillars);
        m_objects.add(clocks);
        m_objects.add(furnaces);
        m_objects.add(kitchens);
        m_objects.add(barrels);
        //Power-ups
        m_objects.add(medkits);
        m_objects.add(foods);
        m_objects.add(bullets);
        m_objects.add(bags);
        m_objects.add(shotguns);
        m_objects.add(machineguns);
        m_objects.add(armors);
        m_objects.add(helmets);
        m_objects.add(superShotguns);      
        
        m_renderingEngine.setMainCamera(player.getCamera());
    }

    /**
     * Inputs accessible in the level.
     */
	public void input() {
		double time = (double) Time.getTime() / Time.SECOND;
    	double timeDecimals = (time - (double) ((int) time));
    	
		if (!player.isAlive) {
	        if(Input.getKeyDown(Input.KEY_E) || Input.getKeyDown(Input.KEY_SPACE)) {
	    		if (timeDecimals <= 5.0f) {
		            Auschwitz.reloadLevel();
		            player.gotPistol();
	    		}
	        }
        }

        if (player.fires && !player.isReloading) {
            for (NaziSoldier monster : naziSoldiers) {
            	if(player.isBulletBased) {
	                if (Math.abs(monster.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < BULLET_RANGE && player.getBullets()!=0) {
	                    monster.damage(player.getDamage());
	                }
            	}else if(player.isShellBased) {
            		if (Math.abs(monster.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < SHELL_RANGE && player.getShells()!=0) {
            			monster.damage(player.getDamage());
            		}
            	}else if(player.isMelee) {
    	            if (Math.abs(monster.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < MELEE_RANGE && player.isAlive) {
    	               AudioUtil.playAudio(punchNoise, 0);
    	               monster.damage(player.getDamage());
    	            }
                }
            }
            
            for (Dog dog : dogs) {
            	if(player.isBulletBased) {
	                if (Math.abs(dog.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < BULLET_RANGE && player.getBullets()!=0) {
	                	dog.damage(player.getDamage());
	                }
            	}else if(player.isShellBased) {
            		if (Math.abs(dog.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < SHELL_RANGE && player.getShells()!=0) {
            			dog.damage(player.getDamage());
            		}
            	}else if(player.isMelee) {
    	            if (Math.abs(dog.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < MELEE_RANGE && player.isAlive) {
    	               AudioUtil.playAudio(punchNoise, 0);
    	               dog.damage(player.getDamage());
    	            }
                }
            }
            
            for (SsSoldier ssSoldier : ssSoldiers) {
            	if(player.isBulletBased) {
	                if (Math.abs(ssSoldier.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < BULLET_RANGE && player.getBullets()!=0) {
	                	ssSoldier.damage(player.getDamage());
	                }
            	}else if(player.isShellBased) {
            		if (Math.abs(ssSoldier.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < SHELL_RANGE && player.getShells()!=0) {
            			ssSoldier.damage(player.getDamage());
            		}
            	}else if(player.isMelee) {
    	            if (Math.abs(ssSoldier.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < MELEE_RANGE && player.isAlive) {
    	               AudioUtil.playAudio(punchNoise, 0);
    	               ssSoldier.damage(player.getDamage());
    	            }
                }
            }
            
            for (NaziSergeant naziSergeants : naziSeargeants) {
            	if(player.isBulletBased) {
	                if (Math.abs(naziSergeants.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < BULLET_RANGE && player.getBullets()!=0) {
	                	naziSergeants.damage(player.getDamage());
	                }
            	}else if(player.isShellBased) {
            		if (Math.abs(naziSergeants.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < SHELL_RANGE && player.getShells()!=0) {
            			naziSergeants.damage(player.getDamage());
            		}
            	}else if(player.isMelee) {
    	            if (Math.abs(naziSergeants.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < MELEE_RANGE && player.isAlive) {
    	               AudioUtil.playAudio(punchNoise, 0);
    	               naziSergeants.damage(player.getDamage());
    	            }
                }
            }
            
            for (Ghost ghost : ghosts) {
            	if(player.isBulletBased) {
	                if (Math.abs(ghost.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < BULLET_RANGE && player.getBullets()!=0) {
	                	ghost.damage(player.getDamage());
	                }
            	}else if(player.isShellBased) {
            		if (Math.abs(ghost.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < SHELL_RANGE && player.getShells()!=0) {
            			ghost.damage(player.getDamage());
            		}
            	}else if(player.isMelee) {
    	            if (Math.abs(ghost.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < MELEE_RANGE && player.isAlive) {
    	               ghost.damage(player.getDamage());
    	            }
                }
            }
            
            for (Lamp lamp : lamps) {
            	if(player.isBulletBased) {
	                if (Math.abs(lamp.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < BULLET_RANGE && player.getBullets()!=0) {
	                	lamp.damage(player.getDamage());
	                }
            	}else if(player.isShellBased) {
            		if (Math.abs(lamp.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < SHELL_RANGE && player.getShells()!=0) {
            			lamp.damage(player.getDamage());
            		}
            	}else if(player.isMelee) {
    	            if (Math.abs(lamp.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < MELEE_RANGE && player.isAlive) {
    	               AudioUtil.playAudio(punchSolidNoise, 0);
    	               lamp.damage(player.getDamage());
    	            }
                }
            }
            
            for (Pillar pillar : pillars) {
            	if(player.isBulletBased) {
	                if (Math.abs(pillar.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < BULLET_RANGE && player.getBullets()!=0) {
	                	pillar.damage(player.getDamage());
	                }
            	}else if(player.isShellBased) {
            		if (Math.abs(pillar.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < SHELL_RANGE && player.getShells()!=0) {
            			pillar.damage(player.getDamage());
            		}
            	}else if(player.isMelee) {
    	            if (Math.abs(pillar.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < MELEE_RANGE && player.isAlive) {
    	               AudioUtil.playAudio(punchSolidNoise, 0);
    	               pillar.damage(player.getDamage());
    	            }
                }
            }
            
            for (Barrel barrel : barrels) {
            	if(player.isBulletBased) {
	                if (Math.abs(barrel.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < BULLET_RANGE && player.getBullets()!=0) {
	                	barrel.damage(player.getDamage());
	                	AudioUtil.playAudio(barrelNoise, 0);
	                }
            	}else if(player.isShellBased) {
            		if (Math.abs(barrel.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < SHELL_RANGE && player.getShells()!=0) {
            			barrel.damage(player.getDamage());
            			AudioUtil.playAudio(barrelNoise, 0);
            		}
            	}else if(player.isMelee) {
    	            if (Math.abs(barrel.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < MELEE_RANGE && player.isAlive) {
    	               barrel.damage(player.getDamage());
    	               AudioUtil.playAudio(barrelNoise, 0);
    	            }
                }
            }
            
            for (Hanged hanged : hangeds) {
                if(player.isMelee == true) {
    	           if (Math.abs(hanged.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < 1.25f && player.isAlive) {
    	                AudioUtil.playAudio(punchNoise, 0);
    	           }
                }
            }
            
            for (Table table : tables) {
                if(player.isMelee == true) {
    	           if (Math.abs(table.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < 1.25f && player.isAlive) {
    	                AudioUtil.playAudio(punchSolidNoise, 0);
    	           }
                }
            }
            
            for (Clock clock : clocks) {
                if(player.isMelee == true) {
    	           if (Math.abs(clock.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < 1.25f && player.isAlive) {
    	                AudioUtil.playAudio(punchSolidNoise, 0);
    	           }
                }
            }
            
            for (Furnace furnace : furnaces) {
                if(player.isMelee == true) {
    	           if (Math.abs(furnace.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < 1.25f && player.isAlive) {
    	                AudioUtil.playAudio(punchSolidNoise, 0);
    	           }
                }
            }
            
        }

        player.input();
    }

    /**
     * Updates everything rendered in the level.
     */
    public void update() {
        
    	m_objects.update();

        m_objects.updateAndKillToRenderPipeline(deadNazi);
        
        m_objects.removeListToRenderPipeline(removeMedkitList);
        m_objects.removeListToRenderPipeline(removeFoodList);
        m_objects.removeListToRenderPipeline(removeBulletList);
        m_objects.removeListToRenderPipeline(removeBagList);
        m_objects.removeListToRenderPipeline(removeShotgunList);
        m_objects.removeListToRenderPipeline(removeMachineGunList);
        m_objects.removeListToRenderPipeline(removeGhostList);
        m_objects.removeListToRenderPipeline(removeArmorList);
        m_objects.removeListToRenderPipeline(removeHelmets);
        m_objects.removeListToRenderPipeline(removeSuperShotgunList);
        m_objects.removeListToRenderPipeline(removeBarrels);
        
        removeMedkitList.clear();
        removeFoodList.clear();
        removeBulletList.clear();
        removeBagList.clear();
        removeShotgunList.clear();
        removeMachineGunList.clear();
        removeGhostList.clear();
        removeArmorList.clear();
        removeSuperShotgunList.clear();
        removeHelmets.clear();
        removeBarrels.clear();
    }

    /**
     * Renders everything in the level.
     * @param shader to render
     */
    public void render(Shader shader) {
    	m_meshRenderer.render(shader);
    	m_objects.render(shader);
        
    	m_objects.sortNumberComponents(secretWalls);
    	m_objects.sortNumberComponents(naziSoldiers);
    	m_objects.sortNumberComponents(dogs);
    	m_objects.sortNumberComponents(ssSoldiers);
    	m_objects.sortNumberComponents(naziSeargeants);

    }
    
    /**
     * Method that opens the accessible doors in the level.
     * @param position coordinates.
     * @param playSound If can play a sound.
     */
    public void openDoors(Vector3f position, boolean playSound) {
        boolean worked = false;

        for (Door door : doors) {
            if (Math.abs(door.getTransform().getPosition().sub(position).length()) < 1f) {
                worked = true;
                door.open(0.5f, 3f);
            }
        }
        
        for (SecretWall secretWall : secretWalls) {
        	if (Math.abs(secretWall.getTransform().getPosition().sub(position).length()) < 1f) {
                worked = true;
                secretWall.open(0.5f, 3f);
                player.playerText.get("Notification").setText("You've found a secret!");
                player.notificationTime = (double) Time.getTime() / Time.SECOND;
            }
        }

        if (playSound) {
            for (int i = 0; i < exitPoints.size(); i++) {
                if (Math.abs(exitPoints.get(i).sub(position).length()) < 1f) {
                    Auschwitz.loadLevel(exitOffsets.get(i), true);
                }
            }
        }

        if (!worked && playSound) {
            AudioUtil.playAudio(misuseNoise, 0);
        }
    }

    /**
     * Method that checks the collisions between all the level objects, the level
     * itself and vice-versa.
     * @param oldPos Objects' old position.
     * @param newPos Objects' new position.
     * @param objectWidth Objects' width.
     * @param objectLength Objects' length.
     * @return Collision's vector.
     */
    public Vector3f checkCollisions(Vector3f oldPos, Vector3f newPos, float objectWidth, float objectLength) {
        Vector2f collisionVector = new Vector2f(1, 1);
        Vector3f movementVector = newPos.sub(oldPos);

        if (movementVector.length() > 0) {
            Vector2f blockSize = new Vector2f(SPOT_WIDTH, SPOT_LENGTH);
            Vector2f objectSize = new Vector2f(objectWidth, objectLength);

            Vector2f oldPos2 = new Vector2f(oldPos.getX(), oldPos.getZ());
            Vector2f newPos2 = new Vector2f(newPos.getX(), newPos.getZ());

            for (int i = 0; i < m_bitmap.getWidth(); i++) {
                for (int j = 0; j < m_bitmap.getHeight(); j++) {
                    if ((m_bitmap.getPixel(i, j) & 0xFFFFFF) == 0) // If it's a black (wall) pixel
                    {
                        collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, blockSize.mul(new Vector2f(i, j)), blockSize));
                    }
                }
            }

            for (Door door : doors)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, door.getTransform().getPosition().getXZ(), door.getSize()));
            
            for (SecretWall secretWall : secretWalls)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, secretWall.getTransform().getPosition().getXZ(), secretWall.getSize()));
            /**
            for (NaziSoldier monster : naziSoldiers)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, monster.getTransform().getPosition().getXZ(), monster.getSize()));
            
            for (SsSoldier ssSoldier : ssSoldiers)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, ssSoldier.getTransform().getPosition().getXZ(), ssSoldier.getSize()));
            
            for (Dog dog : dogs)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, dog.getTransform().getPosition().getXZ(), dog.getSize()));
            
            for (NaziSergeant naziSergeants : naziSeargeants)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, naziSergeants.getTransform().getPosition().getXZ(), naziSergeants.getSize()));
            */
            for (Bones bone : bones)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, bone.getTransform().getPosition().getXZ(), bone.getSize()));
            
            for (DeadJew deadJew : deadJews)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, deadJew.getTransform().getPosition().getXZ(), deadJew.getSize()));
            
            for (Tree tree : trees)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, tree.getTransform().getPosition().getXZ(), tree.getSize()));
            
            for (Table table : tables)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, table.getTransform().getPosition().getXZ(), table.getSize()));
            
            for (Pipe pipe : pipes)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, pipe.getTransform().getPosition().getXZ(), pipe.getSize()));
            
            for (Pendule pendule : pendules)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, pendule.getTransform().getPosition().getXZ(), pendule.getSize()));
            
            //for (Lantern flare : flares)
                //collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, flare.getTransform().getPosition().getXZ(), flare.getSize()));
            
            for (Lamp lamp : lamps)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, lamp.getTransform().getPosition().getXZ(), lamp.getSize()));
            
            for (Hanged jew : hangeds)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, jew.getTransform().getPosition().getXZ(), jew.getSize()));
            
            for (Pillar pillar : pillars)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, pillar.getTransform().getPosition().getXZ(), pillar.getSize()));
            
            for (Clock clock : clocks)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, clock.getTransform().getPosition().getXZ(), clock.getSize()));
            
            for (Furnace furnace : furnaces)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, furnace.getTransform().getPosition().getXZ(), furnace.getSize()));
            
            for (Barrel barrel : barrels)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, barrel.getTransform().getPosition().getXZ(), barrel.getSize()));
            
        }

        return new Vector3f(collisionVector.getX(), 0, collisionVector.getY());
    }

    /**
     * Checks if there's any intersections between the objects.
     * @param lineStart The start position of the line-checker.
     * @param lineEnd The end position of the line-checker.
     * @param hurtMonsters If which intersection could hurt something.
     * @return Nearest Intersection.
     */
    public Vector2f checkIntersections(Vector2f lineStart, Vector2f lineEnd, boolean hurtMonsters) {
        Vector2f nearestIntersect = null;

        for (int i = 0; i < collisionPosStart.size(); i++) {
            Vector2f collision = PhysicsUtil.lineIntersect(lineStart, lineEnd, collisionPosStart.get(i), collisionPosEnd.get(i));

            if (collision != null && (nearestIntersect == null
                    || nearestIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                nearestIntersect = collision;
            }
        }

        for (Door door : doors) {
            Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, door.getTransform().getPosition().getXZ(), door.getSize());

            if (collision != null && (nearestIntersect == null
                    || nearestIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                nearestIntersect = collision;
            }
        }
        
        for (SecretWall secretWall : secretWalls) {
            Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, secretWall.getTransform().getPosition().getXZ(), secretWall.getSize());

            if (collision != null && (nearestIntersect == null
                    || nearestIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                nearestIntersect = collision;
            }
        }

        if (hurtMonsters) {
            Vector2f monsterIntersect = null;
            NaziSoldier nearestMonster = null;
            
            Vector2f dogIntersect = null;
            Dog nearestDog = null;
            
            Vector2f ssSoldierIntersect = null;
            SsSoldier nearestSsSoldier = null;
            
            Vector2f naziSergeantsIntersect = null;
            NaziSergeant nearestNaziSargent = null;
            
            Vector2f ghostIntersect = null;
            Ghost nearestghost = null;

            for (NaziSoldier monster : naziSoldiers) {
                Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, monster.getTransform().getPosition().getXZ(), monster.getSize());

                if (collision != null && (monsterIntersect == null
                        || monsterIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                    monsterIntersect = collision;
                    nearestMonster = monster;
                }
                
            }
            
            for (Dog dog : dogs) {
                Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, dog.getTransform().getPosition().getXZ(), dog.getSize());

                if (collision != null && (dogIntersect == null
                        || dogIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                	dogIntersect = collision;
                    nearestDog = dog;
                }
            }
            
            for (SsSoldier ssSoldier : ssSoldiers) {
                Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, ssSoldier.getTransform().getPosition().getXZ(), ssSoldier.getSize());

                if (collision != null && (ssSoldierIntersect == null
                        || ssSoldierIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                	ssSoldierIntersect = collision;
                	nearestSsSoldier = ssSoldier;
                }
            }
            
            for (NaziSergeant naziSargent : naziSeargeants) {
                Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, naziSargent.getTransform().getPosition().getXZ(), naziSargent.getSize());

                if (collision != null && (naziSergeantsIntersect == null
                        || naziSergeantsIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                	naziSergeantsIntersect = collision;
                	nearestNaziSargent = naziSargent;
                }
            }
            
            for (Ghost ghost : ghosts) {
                Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, ghost.getTransform().getPosition().getXZ(), ghost.getSize());

                if (collision != null && (ghostIntersect == null
                        || ghostIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                	ghostIntersect = collision;
                	nearestghost = ghost;
                }
            }

            if((player.isBulletBased && player.getBullets()!=0)||(player.isShellBased && player.getShells()!=0)) {
	            if (monsterIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > monsterIntersect.sub(lineStart).length())) {
	                nearestMonster.damage(player.getDamage());
	            }
	            
	            if (dogIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > dogIntersect.sub(lineStart).length())) {
	            	nearestDog.damage(player.getDamage());
	            }
	            
	            if (ssSoldierIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > ssSoldierIntersect.sub(lineStart).length())) {
	            	nearestSsSoldier.damage(player.getDamage());
	            }
	            if (naziSergeantsIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > naziSergeantsIntersect.sub(lineStart).length())) {
	            	nearestNaziSargent.damage(player.getDamage());
	            }
	            
	            if (ghostIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > ghostIntersect.sub(lineStart).length())) {
	            	nearestghost.damage(player.getDamage());
	            }
        	}
            if(player.isMelee && player.isAlive) {
        		if (monsterIntersect != null && (nearestIntersect == null
	                    || /*nearestIntersect.sub(lineStart).length() >*/ monsterIntersect.sub(lineStart).length() < MELEE_RANGE)) {
	                nearestMonster.damage(player.getDamage());
	            }
	            
	            if (dogIntersect != null && (nearestIntersect == null
	                    || /*nearestIntersect.sub(lineStart).length() >*/ dogIntersect.sub(lineStart).length() < MELEE_RANGE)) {
	            	nearestDog.damage(player.getDamage());
	            }
	            
	            if (ssSoldierIntersect != null && (nearestIntersect == null
	                    || /*nearestIntersect.sub(lineStart).length() >*/ ssSoldierIntersect.sub(lineStart).length() < MELEE_RANGE)) {
	            	nearestSsSoldier.damage(player.getDamage());
	            }
	            
	            if (naziSergeantsIntersect != null && (nearestIntersect == null
	                    || /*nearestIntersect.sub(lineStart).length() >*/ naziSergeantsIntersect.sub(lineStart).length() < MELEE_RANGE)) {
	            	nearestNaziSargent.damage(player.getDamage());
	            }
	            
	            if (ghostIntersect != null && (nearestIntersect == null
	                    || /*nearestIntersect.sub(lineStart).length() >*/ ghostIntersect.sub(lineStart).length() < MELEE_RANGE)) {
	            	nearestghost.damage(player.getDamage());
	            }
        	}
            
            for (Barrel barrel : barrels) {
                if(barrel.boom == true) {
                    if (monsterIntersect != null && (nearestIntersect == null
                                                     || nearestIntersect.sub(lineStart).length() > monsterIntersect.sub(lineStart).length())) {
                        nearestMonster.damage(barrel.damage);
                    }
                    
                    if (dogIntersect != null && (nearestIntersect == null
                                                 || nearestIntersect.sub(lineStart).length() > dogIntersect.sub(lineStart).length())) {
                        nearestDog.damage(barrel.damage);
                    }
                    
                    if (ssSoldierIntersect != null && (nearestIntersect == null
                                                       || nearestIntersect.sub(lineStart).length() > ssSoldierIntersect.sub(lineStart).length())) {
                        nearestSsSoldier.damage(barrel.damage);
                    }
                    
                    if (naziSergeantsIntersect != null && (nearestIntersect == null
                                                           || nearestIntersect.sub(lineStart).length() > naziSergeantsIntersect.sub(lineStart).length())) {
                        nearestNaziSargent.damage(barrel.damage);
                    }
                    
                    if (ghostIntersect != null && (nearestIntersect == null
                                                   || nearestIntersect.sub(lineStart).length() > ghostIntersect.sub(lineStart).length())) {
                        nearestghost.damage(barrel.damage);
                    }
                }
            }
        }

        return nearestIntersect;
    }
    
    /**
     * Adds a face for the level's mesh.
     * @param indices of the mesh.
     * @param startLocation of the mesh.
     * @param direction of face.
     */
    private void addFace(ArrayList<Integer> indices, int startLocation, boolean direction) {
		if(direction) {
			indices.add(startLocation + 2);
			indices.add(startLocation + 1);
			indices.add(startLocation + 0);
			indices.add(startLocation + 3);
			indices.add(startLocation + 2);
			indices.add(startLocation + 0);
		} else {
			indices.add(startLocation + 0);
			indices.add(startLocation + 1);
			indices.add(startLocation + 2);
			indices.add(startLocation + 0);
			indices.add(startLocation + 2);
			indices.add(startLocation + 3);
		}
	}
    
    /**
     * Calculates the texture's coordinates of
     * the level's mesh.
     * @param value of the coordinates.
     * @return Texture's coordinates.
     */
    private float[] calcTextCoords(int value) {
    	int texX = value / 16;
        int texY = texX % 4;
        texX /= 4;
        
        float[] result = new float[4];

        result[0] = 1f - texX / NUM_TEX_X;
        result[1] = result[0] - 1 / NUM_TEX_X;
        result[2] = 1f - texY / NUM_TEX_Y;
        result[3] = result[2] - 1 / NUM_TEX_Y;
        
    	return result;
    }
    
    /**
     * Adds a vertex for the level's mesh.
     * @param vertices of the mesh
     * @param i pixel in bitmap.
     * @param j pixel in bitmap.
     * @param offset of height.
     * @param x position.
     * @param y position.
     * @param z position.
     * @param texCoords of the mesh.
     */
    private void addVertices(ArrayList<Vertex> vertices, int i, int j, float offset, boolean x, boolean y, boolean z, float[] texCoords) {
		if(x && z)
		{
			vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, offset * LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(texCoords[0],texCoords[2])));
			vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, offset * LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(texCoords[1],texCoords[2])));
			vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, offset * LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(texCoords[1],texCoords[3])));
			vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, offset * LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(texCoords[0],texCoords[3])));
		}
		else if(x && y)
		{
			vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, j * LEVEL_HEIGHT, offset * SPOT_LENGTH), new Vector2f(texCoords[0],texCoords[2])));
			vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, j * LEVEL_HEIGHT, offset * SPOT_LENGTH), new Vector2f(texCoords[1],texCoords[2])));
			vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, (j + 1) * LEVEL_HEIGHT, offset * SPOT_LENGTH), new Vector2f(texCoords[1],texCoords[3])));
			vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, (j + 1) * LEVEL_HEIGHT, offset * SPOT_LENGTH), new Vector2f(texCoords[0],texCoords[3])));
		}
		else if(y && z)
		{
			vertices.add(new Vertex(new Vector3f(offset * SPOT_WIDTH, i * LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(texCoords[0],texCoords[2])));
			vertices.add(new Vertex(new Vector3f(offset * SPOT_WIDTH, i * LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(texCoords[1],texCoords[2])));
			vertices.add(new Vertex(new Vector3f(offset * SPOT_WIDTH, (i + 1) * LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(texCoords[1],texCoords[3])));
			vertices.add(new Vertex(new Vector3f(offset * SPOT_WIDTH, (i + 1) * LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(texCoords[0],texCoords[3])));
		}
		else
		{
			Debug.printErrorMessage("Invalid plane used in level generator", "Level generation error!");
			System.err.println("Invalid plane used in level generator");
			new Exception().printStackTrace();
			System.exit(1);
		}
	}

    /**
     * Method that generates the level for the game.
     * @param engine of the level.
     */
    private void generateLevel(RenderingEngine engine) {
        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        ArrayList<Integer> indices = new ArrayList<Integer>();
        
        //Remove list
    	if(removeMedkitList == null) Level.removeMedkitList = new ArrayList<Medkit>();
    	if(removeFoodList == null) Level.removeFoodList = new ArrayList<Food>();
    	if(removeBulletList == null) Level.removeBulletList = new ArrayList<Bullet>();
    	if(removeBagList == null) Level.removeBagList = new ArrayList<Bag>();
    	if(removeShotgunList == null) Level.removeShotgunList = new ArrayList<Shotgun>();
    	if(removeMachineGunList == null) Level.removeMachineGunList = new ArrayList<Machinegun>();
    	if(removeGhostList == null) Level.removeGhostList = new ArrayList<Ghost>();
    	if(removeArmorList == null) Level.removeArmorList = new ArrayList<Armor>();
    	if(removeSuperShotgunList == null) Level.removeSuperShotgunList = new ArrayList<SuperShotgun>();
    	if(removeHelmets == null) Level.removeHelmets = new ArrayList<Helmet>();
    	if(removeBarrels == null) Level.removeBarrels = new ArrayList<Barrel>();
        //Doors and stuff
        if(doors == null) this.doors = new ArrayList<Door>();
        if(secretWalls == null) this.secretWalls = new ArrayList<SecretWall>();
        if(exitOffsets == null) this.exitOffsets = new ArrayList<Integer>();
        if(exitPoints == null) this.exitPoints = new ArrayList<Vector3f>();
        //Enemies
        if(naziSoldiers == null) this.naziSoldiers = new ArrayList<NaziSoldier>();
        if(dogs == null) this.dogs = new ArrayList<Dog>();
        if(ssSoldiers == null) this.ssSoldiers = new ArrayList<SsSoldier>();
        if(naziSeargeants == null) this.naziSeargeants = new ArrayList<NaziSergeant>();
        if(ghosts == null) this.ghosts = new ArrayList<Ghost>();
        //Power-ups
        if(medkits == null) this.medkits = new ArrayList<Medkit>();
        if(foods == null) this.foods = new ArrayList<Food>();
        if(bullets == null) this.bullets = new ArrayList<Bullet>();
        if(shotguns == null) this.shotguns = new ArrayList<Shotgun>();
        if(bags == null) this.bags = new ArrayList<Bag>();
        if(machineguns == null) this.machineguns = new ArrayList<Machinegun>();
        if(armors == null) this.armors = new ArrayList<Armor>();
        if(superShotguns == null) this.superShotguns = new ArrayList<SuperShotgun>();
        if(helmets == null) this.helmets = new ArrayList<Helmet>();
        //Objects
        if(trees == null) this.trees = new ArrayList<Tree>();
        if(flares == null) this.flares = new ArrayList<Lantern>();
        if(lightPoints == null) this.lightPoints = new ArrayList<LightBeam>();
        if(bones == null) this.bones = new ArrayList<Bones>();
        if(deadNazi == null) this.deadNazi = new ArrayList<NaziSoldier>();
        if(pipes == null) this.pipes = new ArrayList<Pipe>();
        if(pendules == null) this.pendules = new ArrayList<Pendule>();
        if(lamps == null) this.lamps = new ArrayList<Lamp>();
        if(hangeds == null) this.hangeds = new ArrayList<Hanged>();
        if(deadJews == null) this.deadJews = new ArrayList<DeadJew>();
        if(tables == null) this.tables = new ArrayList<Table>();
        if(pillars == null) this.pillars = new ArrayList<Pillar>();
        if(clocks == null) this.clocks = new ArrayList<Clock>();
        if(furnaces == null) this.furnaces = new ArrayList<Furnace>();
        if(kitchens == null) this.kitchens = new ArrayList<Kitchen>();
        if(barrels == null) this.barrels = new ArrayList<Barrel>();

        for (int i = 1; i < m_bitmap.getWidth() - 1; i++) {
            for (int j = 1; j < m_bitmap.getHeight() - 1; j++) {
                if ((m_bitmap.getPixel(i, j) & 0xFFFFFF) == 0) // If it isn't a black (wall) pixel
                	continue;
                	//LEVEL_HEIGHT = level.getPixel(i, j) & 0x0000FF;
                    if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 16) {
                        Transform doorTransform = new Transform();

                        boolean xDoor = (m_bitmap.getPixel(i, j - 1) & 0xFFFFFF) == 0 && (m_bitmap.getPixel(i, j + 1) & 0xFFFFFF) == 0;
                        boolean yDoor = (m_bitmap.getPixel(i - 1, j) & 0xFFFFFF) == 0 && (m_bitmap.getPixel(i + 1, j) & 0xFFFFFF) == 0;

                        if ((yDoor && xDoor) || !(yDoor || xDoor)) {
                            System.err.println("Level Generation Error at (" + i + ", " + j + "): Doors must be between two solid walls.");
                            Debug.printErrorMessage("Level Generation Error at (" + i + ", " + j + "): Doors must be between two solid walls.", "Level generation error!");
                            new Exception().printStackTrace();
                            System.exit(1);
                        }

                        if (yDoor) {
                            doorTransform.setPosition(i, 0,j + SPOT_LENGTH / 2);
                            doors.add(new Door(doorTransform, m_material, doorTransform.getPosition().add(new Vector3f(-0.9f, 0, 0))));
                        } else if (xDoor) {
                            doorTransform.setPosition(i + SPOT_LENGTH / 2, 0, j);
                            doorTransform.setRotation(0, 90, 0);
                            doors.add(new Door(doorTransform, m_material, doorTransform.getPosition().add(new Vector3f(0, 0, -0.9f))));
                        }
                    }else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 20) {
                        Transform wallTransform = new Transform();

                        boolean xSecretWall = (m_bitmap.getPixel(i, j - 1) & 0xFFFFFF) == 0 && (m_bitmap.getPixel(i, j + 1) & 0xFFFFFF) == 0;
                        boolean ySecretWall = (m_bitmap.getPixel(i - 1, j) & 0xFFFFFF) == 0 && (m_bitmap.getPixel(i + 1, j) & 0xFFFFFF) == 0;

                        if ((ySecretWall && xSecretWall) || !(ySecretWall || xSecretWall)) {
                            System.err.println("Level Generation Error at (" + i + ", " + j + "): Secret Walls must be between two solid walls.");
                            Debug.printErrorMessage("Level Generation Error at (" + i + ", " + j + "): Secret Walls must be between two solid walls.", "Level generation error!");
                            new Exception().printStackTrace();
                            System.exit(1);
                        }

                        if (ySecretWall) {
                            wallTransform.setPosition(i, 0, j);
                            secretWalls.add(new SecretWall(wallTransform, m_material, wallTransform.getPosition().add(new Vector3f(-0.9f, 0, 0))));
                        } else if (xSecretWall) {
                            wallTransform.setPosition((i+ (SPOT_LENGTH / 2)*2), 0, j);
                            wallTransform.setRotation(0, 90, 0);
                            secretWalls.add(new SecretWall(wallTransform, m_material, wallTransform.getPosition().add(new Vector3f(0, 0, -0.9f))));
                        }
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 128) {
                    	naziSoldiers.add(new NaziSoldier(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 1) {
                        player = new Player(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0.4375f, (j + 0.5f) * SPOT_LENGTH), engine);
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 192) {
                        medkits.add(new Medkit(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 100) {
                        trees.add(new Tree(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 50) {
                    	flares.add(new Lantern(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, LEVEL_HEIGHT * 0.75f, (j + 0.5f) * SPOT_LENGTH)), engine));
                    	//lightPoints.add(new LightBeam(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.04f, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 51) {
                    	//lightPoints.add(new LightBeam(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.04f, (j + 0.5f) * SPOT_LENGTH))));
                    	lamps.add(new Lamp(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 55) {
                        bones.add(new Bones(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 60) {
                        deadNazi.add(new NaziSoldier(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 70) {
                        deadJews.add(new DeadJew(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.05f, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 80) {
                        foods.add(new Food(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 90) {
                        dogs.add(new Dog(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 110) {
                        ssSoldiers.add(new SsSoldier(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 120) {
                    	tables.add(new Table(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 121) {
                    	furnaces.add(new Furnace(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), engine));
                	} else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 122) {
                    	kitchens.add(new Kitchen(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                        foods.add(new Food(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 123) {
                    	clocks.add(new Clock(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 130) {
                    	superShotguns.add(new SuperShotgun(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 140) {
                    	naziSeargeants.add(new NaziSergeant(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 150) {
                    	pipes.add(new Pipe(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0.00000000001f * LEVEL_HEIGHT, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 151) {
                        pendules.add(new Pendule(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 152) {
                        hangeds.add(new Hanged(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 153) {
                        pillars.add(new Pillar(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0.0000000001f * LEVEL_HEIGHT, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 154) {
                        armors.add(new Armor(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 155) {
                        helmets.add(new Helmet(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                        //barrels.add(new Barrel(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) == 160) {
                        barrels.add(new Barrel(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((m_bitmap.getPixel(i, j) & 0x0000FF) < 128 && (m_bitmap.getPixel(i, j) & 0x0000FF) > 96) {
                        int offset = (m_bitmap.getPixel(i, j) & 0x0000FF) - 96;
                        exitPoints.add(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0f, (j + 0.5f) * SPOT_LENGTH));
                        exitOffsets.add(offset);
                    }
                    
                    float[] texCoords = calcTextCoords((m_bitmap.getPixel(i, j) & 0x00FF00) >> 8);
                    
                    //Generate Floor
    				addFace(indices, vertices.size(), true);
    				addVertices(vertices, i, j, 0, true, false, true, texCoords);
    				
    				//Generate Ceiling
    				addFace(indices, vertices.size(), false);
    				addVertices(vertices, i, j, 1, true, false, true, texCoords);
                    
                    texCoords = calcTextCoords((m_bitmap.getPixel(i, j) & 0xFF0000) >> 16);
                    
                    SecretWall.xHigher = texCoords[0];
                    SecretWall.xLower = texCoords[1];
                    SecretWall.yHigher = texCoords[2];
                    SecretWall.yLower = texCoords[3];

                    //Generate Walls
                    if ((m_bitmap.getPixel(i, j - 1) & 0xFFFFFF) == 0) {
                        collisionPosStart.add(new Vector2f(i * SPOT_WIDTH, j * SPOT_LENGTH));
                        collisionPosEnd.add(new Vector2f((i + 1) * SPOT_WIDTH, j * SPOT_LENGTH));
                        addFace(indices,vertices.size(),false);
                        addVertices(vertices, i, 0, j, true, true, false, texCoords);
                    }
                    if ((m_bitmap.getPixel(i, j + 1) & 0xFFFFFF) == 0) {
                        collisionPosStart.add(new Vector2f(i * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
                        collisionPosEnd.add(new Vector2f((i + 1) * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
                        addFace(indices,vertices.size(),true);
                        addVertices(vertices, i, 0, (j + 1), true, true, false, texCoords);
                    }
                    if ((m_bitmap.getPixel(i - 1, j) & 0xFFFFFF) == 0) {
                        collisionPosStart.add(new Vector2f(i * SPOT_WIDTH, j * SPOT_LENGTH));
                        collisionPosEnd.add(new Vector2f(i * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
                        addFace(indices,vertices.size(),true);
                        addVertices(vertices, 0, j, i, false, true, true, texCoords);
                    }
                    if ((m_bitmap.getPixel(i + 1, j) & 0xFFFFFF) == 0) {
                        collisionPosStart.add(new Vector2f((i + 1) * SPOT_WIDTH, j * SPOT_LENGTH));
                        collisionPosEnd.add(new Vector2f((i + 1) * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
                        addFace(indices,vertices.size(),false);
                        addVertices(vertices, 0, j, (i + 1), false, true, true, texCoords);
                    }
            }
        }
        Vertex[] vertArray = new Vertex[vertices.size()];
        Integer[] intArray = new Integer[indices.size()];
        
        vertices.toArray(vertArray);
        indices.toArray(intArray);
        
        if(m_geometry == null)
        	m_geometry = new Mesh(vertArray, Util.toIntArray(intArray), true, true);
        if(m_meshRenderer == null)
        	m_meshRenderer = new MeshRenderer(m_geometry, m_transform, m_material);
        
        engine.setFogDensity(0.07f);
        engine.setFogGradient(1.5f);
        engine.setFogColor(new Vector3f(0.5f,0.5f,0.5f));
        engine.setAmbientLight(new Vector3f(0.75f,0.75f,0.75f));
        engine.addLight(new DirectionalLight(new Vector3f(0.75f,0.75f,0.75f), 
        		1f, new Vector3f(m_bitmap.getWidth()/2,10,m_bitmap.getHeight()/2)));
    }
	
	/**
	 * Returns all the secret walls in the array-list.
	 * @return Secret walls.
	 */
	public ArrayList<SecretWall> getSecretWalls() {return secretWalls;}
	
	/**
	 * Returns all the Nazi soldiers in the array-list.
	 * @return Nazi Soldiers.
	 */
	public ArrayList<NaziSoldier> getNaziSoldiers() {return naziSoldiers;}

	/**
	 * Returns all the Dogs in the array-list.
	 * @return Dogs.
	 */
	public ArrayList<Dog> getDogs() {return dogs;}
	
	/**
	 * Returns all the SS soldiers in the array-list.
	 * @return SS soldiers.
	 */
	public ArrayList<SsSoldier> getSsSoldiers() {return ssSoldiers;}
	
	/**
	 * Returns all the Nazi sergeants in the array-list.
	 * @return Nazi sergeants.
	 */
	public ArrayList<NaziSergeant> getNaziSergeants() {return naziSeargeants;}
	
	/**
	 * Get access to the main player object in game.
	 * @return Player.
	 */
	public static Player getPlayer() {return player;}
	
	/**
	  * Removes the medical kits when the player grabs it.
	  * @param medkit Medical kit.
	  */
	public static void removeMedkit(Medkit medkit) {removeMedkitList.add(medkit);}
	    
	/**
	 * Removes the food when the player grabs it.
	 * @param food Food.
	 */
	public static void removeFood(Food food) {removeFoodList.add(food);}
	    
	/**
	 * Removes the bullet packs when the player grabs it.
	 * @param bullet Bullet pack.
	 */
	public static void removeBullets(Bullet bullet) {removeBulletList.add(bullet);}
	    
	/**
	 * Removes the bags when the player grabs it.
	 * @param bag Bag.
	 */
	public static void removeBags(Bag bag) {removeBagList.add(bag);}
	    
	/**
	 * Removes the shotguns when the player grabs it.
	 * @param shotgun Shotgun.
	 */
	public static void removeShotgun(Shotgun shotgun) {removeShotgunList.add(shotgun);}
	    
	/**
	 * Removes the machine-guns when the player grabs it.
	 * @param machineGun Machine-Gun.
	 */
	public static void removeMachineGun(Machinegun machineGun) {removeMachineGunList.add(machineGun);}
	
	/**
	 * Removes the ghost when disappears.
	 * @param ghost Ghost.
	 */
	public static void removeGhost(Ghost ghost) {removeGhostList.add(ghost);}
	
	/**
	 * Removes the armor when disappears.
	 * @param armor Armor.
	 */
	public static void removeArmor(Armor armor) {removeArmorList.add(armor);}
	
	/**
	 * Removes the super shotguns when the player grabs it.
	 * @param sShotgun Super shotgun.
	 */
	public static void removeSuperShotgun(SuperShotgun sShotgun) {removeSuperShotgunList.add(sShotgun);}
	
	/**
	 * Removes the helmet when disappears.
	 * @param helmet Helmet.
	 */
	public static void removeHelmet(Helmet helmet) {removeHelmets.add(helmet);}
	
	/**
	 * Removes the barrel when disappears.
	 * @param barrel Barrels.
	 */
	public static void removeBarrel(Barrel barrel) {removeBarrels.add(barrel);}

}