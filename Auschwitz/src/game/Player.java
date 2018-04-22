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
import java.util.Random;

import javax.sound.sampled.Clip;

import org.lwjgl.opengl.Display;

import engine.ResourceLoader;
import engine.audio.AudioUtil;
import engine.core.GameComponent;
import engine.core.Input;
import engine.core.Time;
import engine.core.Transform;
import engine.core.Vector2f;
import engine.core.Vector3f;
import engine.rendering.Camera;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.Texture;
import engine.rendering.Vertex;

/**
*
* @author Carlos Rodriguez
* @version 1.4
* @since 2017
*/
public class Player implements GameComponent{

    private static final float GUN_SIZE = 0.1f; 
    private static final float GUN_OFFSET = -0.077f;
    private static final float GUN_OFFSET_X = 0f;
    private static final float GUN_TRANSFORM_MUL = 0.101f;
    
    private static final int MAX_LIFE = 100;

    private static final float MOUSE_SENSITIVITY = 0.25f;
    private static final float SIDE_SENSITIVITY = 0.75f;
    private static final float MAX_LOOK_ANGLE = 0;
    private static final float MIN_LOOK_ANGLE = 0;
    private static final float PLAYER_WIDTH = 0.2f;
    private static float moveSpeed;
    private static float gunFireAnimationTime;
    
    public float damageMin;
    public float damageRange;
    
    public static final String HAND = "hand";
    public static final String PISTOL = "pistol";
    public static final String SHOTGUN = "shotgun";
    public static final String MACHINEGUN = "machinegun";
    public static final String SUPER_SHOTGUN = "superShotgun";
    public static final String CHAINGUN = "chaingun";
    private static final String PLAYER_RES_LOC = "player/";
    private static final String WEAPONS_RES_LOC = "weapons/";
    private static final String HAND_RES_LOC = WEAPONS_RES_LOC + HAND + "/";
    private static final String PISTOL_RES_LOC = WEAPONS_RES_LOC + PISTOL + "/";
    private static final String SHOTGUN_RES_LOC = WEAPONS_RES_LOC + SHOTGUN +"/";
    private static final String MACHINEGUN_RES_LOC = WEAPONS_RES_LOC + MACHINEGUN + "/";
    private static final String SUPER_SHOTGUN_RES_LOC = WEAPONS_RES_LOC + SUPER_SHOTGUN + "/";
    private static final String PISGB0 = "PISGB0";
    private static final String PISFA0 = "PISFA0";
    private static final String PISFC0 = "PISFC0";
    private static final String PISFD0 = "PISFD0";
    private static final String PISFE0 = "PISFE0";
    private static final String EMPTY = "EMPTY";
    private static final String GUNSOUND = "GUN";
    private static final String CLIPSOUND = "CLIPIN";
    private static final String RELOADSOUND = "RELOAD";
    
    private String weaponState;
    
    private static ArrayList<Texture> gunsMaterial;
    private static ArrayList<Texture> gunsAnimationMaterial1;
    private static ArrayList<Texture> gunsAnimationMaterial2;
    private static ArrayList<Texture> gunsAnimationMaterial3;
    private static ArrayList<Texture> gunsAnimationMaterial4;
    private static ArrayList<Texture> crossHairMaterials;
    private static ArrayList<Texture> crossHairAnimationMaterials;
    
    private static ArrayList<Clip> gunsNoiseSounds;
    private static ArrayList<Clip> gunsReloadSounds;
    private static ArrayList<Clip> gunsClippingSounds;
    private static ArrayList<Clip> gunsEmptyNoiseSounds;
    
    private static Material hudMaterial;

    private static final Vector2f centerPosition = new Vector2f(Display.getWidth()/2, Display.getHeight()/2);
    private static final Vector3f zeroVector = new Vector3f(0, 0, 0);
    
    private static Clip gunNoise;
    private static Clip gunEmptyNoise;
    private static Clip gunReload;
    private static Clip gunClipp;
    private static final Clip moveNoise = ResourceLoader.loadAudio(PLAYER_RES_LOC + "MOVE");
    private static final Clip missueNoise = ResourceLoader.loadAudio(PLAYER_RES_LOC + "OOF");
    private static final Clip painNoise = ResourceLoader.loadAudio(PLAYER_RES_LOC + "PLPAIN");
    private static final Clip deathNoise = ResourceLoader.loadAudio(PLAYER_RES_LOC + "PLDETH");

    private static Mesh gunMesh;
    private static Material gunMaterial;
    private static Material gunAnimationMaterial1;
    private static Material gunAnimationMaterial2;
    private static Material gunAnimationMaterial3;
    private static Material gunAnimationMaterial4;
    private static Material crossHairMaterial;
    private static Material crossHairAnimationMaterial;
    private static Material painMaterial;
    private static Transform gunTransform;
    
    private int maxBullets = 100;
    private int maxShells = 50;
    private int maxArmor = 100;

    private Camera playerCamera;
    private Random rand;
    private Vector3f movementVector;

    private static boolean mouseLocked;
    
    private double gunFireTime;
    private double painTime;
    private float width;
    private int health;
    private int armori;
    private int bullets;
    private int shells;
    private boolean armorb;
    private boolean shotgun;
    private boolean machinegun;
    private boolean sShotgun;
    private boolean chaingun;
    
    public boolean isAlive;
    public boolean isHand;
    public boolean isBulletBased;
    public boolean isShellBased;
    public boolean isDoubleShooter;
    
    /**
     * Constructor of the main player.
     * @param position the position in the 3D space.
     */
    public Player(Vector3f position) {
    	
    	if(gunsMaterial == null) {
    		gunsMaterial = new ArrayList<Texture>();
    		
    		gunsMaterial.add(ResourceLoader.loadTexture(HAND_RES_LOC + PISGB0));
    		gunsMaterial.add(ResourceLoader.loadTexture(PISTOL_RES_LOC + PISGB0));
    		gunsMaterial.add(ResourceLoader.loadTexture(SHOTGUN_RES_LOC + PISGB0));
    		gunsMaterial.add(ResourceLoader.loadTexture(MACHINEGUN_RES_LOC + PISGB0));
    		gunsMaterial.add(ResourceLoader.loadTexture(SUPER_SHOTGUN_RES_LOC + PISGB0));
    	}
    	
    	if(gunsAnimationMaterial1 == null) {
    		gunsAnimationMaterial1 = new ArrayList<Texture>();
    		
    		gunsAnimationMaterial1.add(ResourceLoader.loadTexture(HAND_RES_LOC + PISFA0));
    		gunsAnimationMaterial1.add(ResourceLoader.loadTexture(PISTOL_RES_LOC + PISFA0));
    		gunsAnimationMaterial1.add(ResourceLoader.loadTexture(SHOTGUN_RES_LOC + PISFA0));
    		gunsAnimationMaterial1.add(ResourceLoader.loadTexture(MACHINEGUN_RES_LOC + PISFA0));
    		gunsAnimationMaterial1.add(ResourceLoader.loadTexture(SUPER_SHOTGUN_RES_LOC + PISFA0));
    	}
    	
    	if(gunsAnimationMaterial2 == null) {
    		gunsAnimationMaterial2 = new ArrayList<Texture>();
    		
    		gunsAnimationMaterial2.add(ResourceLoader.loadTexture(HAND_RES_LOC + PISFC0));
    		gunsAnimationMaterial2.add(ResourceLoader.loadTexture(PISTOL_RES_LOC + PISFC0));
    		gunsAnimationMaterial2.add(ResourceLoader.loadTexture(SHOTGUN_RES_LOC + PISFC0));
    		gunsAnimationMaterial2.add(ResourceLoader.loadTexture(MACHINEGUN_RES_LOC + PISFC0));
    		gunsAnimationMaterial2.add(ResourceLoader.loadTexture(SUPER_SHOTGUN_RES_LOC + PISFC0));
    	}
    	
    	if(gunsAnimationMaterial3 == null) {
    		gunsAnimationMaterial3 = new ArrayList<Texture>();
    		
    		gunsAnimationMaterial3.add(null);
    		gunsAnimationMaterial3.add(null);
    		gunsAnimationMaterial3.add(ResourceLoader.loadTexture(SHOTGUN_RES_LOC + PISFD0));
    		gunsAnimationMaterial3.add(null);
    		gunsAnimationMaterial3.add(ResourceLoader.loadTexture(SUPER_SHOTGUN_RES_LOC + PISFD0));
    	}
    	
    	if(gunsAnimationMaterial4 == null) {
    		gunsAnimationMaterial4 = new ArrayList<Texture>();
    		
    		gunsAnimationMaterial4.add(null);
    		gunsAnimationMaterial4.add(null);
    		gunsAnimationMaterial4.add(ResourceLoader.loadTexture(SHOTGUN_RES_LOC + PISFE0));
    		gunsAnimationMaterial4.add(null);
    		gunsAnimationMaterial4.add(ResourceLoader.loadTexture(SUPER_SHOTGUN_RES_LOC + PISFE0));
    	}
    	
    	if(crossHairMaterials == null) {
    		crossHairMaterials = new ArrayList<Texture>();
    		crossHairMaterials.add(ResourceLoader.loadTexture("/hud/CROSS0"));
    		crossHairMaterials.add(ResourceLoader.loadTexture("/hud/CROSS1"));
    		crossHairMaterials.add(ResourceLoader.loadTexture("/hud/CROSS2"));
    	}
    	
    	if(crossHairAnimationMaterials == null) {
    		crossHairAnimationMaterials = new ArrayList<Texture>();
    		crossHairAnimationMaterials.add(ResourceLoader.loadTexture("/hud/CROSS01"));
    		crossHairAnimationMaterials.add(ResourceLoader.loadTexture("/hud/CROSS11"));
    		crossHairAnimationMaterials.add(ResourceLoader.loadTexture("/hud/CROSS21"));
    	}
    	
    	if(gunsNoiseSounds == null) {
    		gunsNoiseSounds = new ArrayList<Clip>();
    		
    		gunsNoiseSounds.add(ResourceLoader.loadAudio(HAND_RES_LOC + GUNSOUND));
    		gunsNoiseSounds.add(ResourceLoader.loadAudio(PISTOL_RES_LOC + GUNSOUND));
    		gunsNoiseSounds.add(ResourceLoader.loadAudio(SHOTGUN_RES_LOC + GUNSOUND));
    		gunsNoiseSounds.add(ResourceLoader.loadAudio(MACHINEGUN_RES_LOC + GUNSOUND));
    		gunsNoiseSounds.add(ResourceLoader.loadAudio(SUPER_SHOTGUN_RES_LOC + GUNSOUND));
    	}
    	
    	if(gunsReloadSounds == null) {
    		gunsReloadSounds = new ArrayList<Clip>();
    		
    		gunsReloadSounds.add(null);
    		gunsReloadSounds.add(null);
    		gunsReloadSounds.add(ResourceLoader.loadAudio(SHOTGUN_RES_LOC + RELOADSOUND));
    		gunsReloadSounds.add(null);
    		gunsReloadSounds.add(ResourceLoader.loadAudio(SUPER_SHOTGUN_RES_LOC + RELOADSOUND));
    	}
    	
    	if(gunsClippingSounds == null) {
    		gunsClippingSounds = new ArrayList<Clip>();
    		
    		gunsClippingSounds.add(null);
    		gunsClippingSounds.add(null);
    		gunsClippingSounds.add(ResourceLoader.loadAudio(SHOTGUN_RES_LOC + CLIPSOUND));
    		gunsClippingSounds.add(null);
    		gunsClippingSounds.add(ResourceLoader.loadAudio(SUPER_SHOTGUN_RES_LOC + CLIPSOUND));
    	}
    	
    	if(gunsEmptyNoiseSounds == null) {
    		gunsEmptyNoiseSounds = new ArrayList<Clip>();
    		
    		gunsEmptyNoiseSounds.add(null);
    		gunsEmptyNoiseSounds.add(ResourceLoader.loadAudio(PISTOL_RES_LOC + EMPTY));
    		gunsEmptyNoiseSounds.add(ResourceLoader.loadAudio(SHOTGUN_RES_LOC + EMPTY));
    		gunsEmptyNoiseSounds.add(ResourceLoader.loadAudio(MACHINEGUN_RES_LOC + EMPTY));
    		gunsEmptyNoiseSounds.add(ResourceLoader.loadAudio(SUPER_SHOTGUN_RES_LOC + EMPTY));
    	}
    	
    	if(weaponState == null) {
    		gotPistol();
    	}
    	
    	if(hudMaterial == null) {
    		hudMaterial = new Material(ResourceLoader.loadTexture("hud/MEDIA"));
    	}
    	
    	if(painMaterial == null) {
    		painMaterial = new Material(ResourceLoader.loadTexture("hud/HEALTH0"), new Vector3f(1,0,0));
    	}
    	
        if (gunMesh == null) {
            gunMesh = new Mesh();

            float sizeY = GUN_SIZE;
            float sizeX = (float) ((double) sizeY / (0.5f * 2.0));

            float offsetX = 0.00f;
            float offsetY = 0.00f;

            float texMinX = -offsetX;
            float texMaxX = -1 - offsetX;
            float texMinY = -offsetY;
            float texMaxY = 1 - offsetY;

            Vertex[] verts = new Vertex[]{new Vertex(new Vector3f(-sizeX, 0, 0), new Vector2f(texMaxX, texMaxY)),
                new Vertex(new Vector3f(-sizeX, sizeY, 0), new Vector2f(texMaxX, texMinY)),
                new Vertex(new Vector3f(sizeX, sizeY, 0), new Vector2f(texMinX, texMinY)),
                new Vertex(new Vector3f(sizeX, 0, 0), new Vector2f(texMinX, texMaxY))};

            int[] indices = new int[]{0, 1, 2,
            						  0, 2, 3};

            gunMesh.addVertices(verts, indices);
        }

        playerCamera = new Camera(position);
        health = MAX_LIFE;
        armorb = false;
        armori = 0;
        bullets = 0;
        shells = 0;

        if (gunTransform == null) {
            gunTransform = new Transform(playerCamera.getPos());
        }

        gunFireTime = 0;
        painTime = 0;
        mouseLocked = true;
        isAlive = true;
        Input.setMousePosition(centerPosition);
        Input.setCursor(false);
        movementVector = zeroVector;
        width = PLAYER_WIDTH;
        rand = new Random();
        
    }

    private float upAngle = 0;
    
    /**
     * The settings that the player sets if he chooses the fist.
     */
    public void gotHand() {
    	gunMaterial = new Material(gunsMaterial.get(0));
    	gunAnimationMaterial1 = new Material(gunsAnimationMaterial1.get(0));
    	gunAnimationMaterial2 = new Material(gunsAnimationMaterial2.get(0));
        gunNoise = gunsNoiseSounds.get(0);
        gunEmptyNoise = null;
        damageMin = 10f;
        damageRange = 0.1f;
        gunFireAnimationTime = 0.1f;
        moveSpeed = 6f;
        isHand = true;    
        isBulletBased = false;
        isShellBased = false;
        weaponState = HAND;
        
        isDoubleShooter = false;
    }
    
    /**
     * The settings that the player sets if he chooses the pistol of 
     * he's bag.
     */
    public void gotPistol() {
    	gunMaterial = new Material(gunsMaterial.get(1));
    	gunAnimationMaterial1 = new Material(gunsAnimationMaterial1.get(1));
    	gunAnimationMaterial2 = new Material(gunsAnimationMaterial2.get(1));
    	crossHairMaterial = new Material(crossHairMaterials.get(0));
    	crossHairAnimationMaterial = new Material(crossHairAnimationMaterials.get(0));
        gunNoise = gunsNoiseSounds.get(1);
        gunEmptyNoise = gunsEmptyNoiseSounds.get(1);
        gunFireAnimationTime = 0.1f;
        damageMin = 20f;
        damageRange = 30f;
        moveSpeed = 5f;
        isHand = false;
        isBulletBased = true;
        isShellBased = false;
        weaponState = PISTOL;
        
        isDoubleShooter = false;
    }
    
    /**
     * The settings that the player sets if he chooses the shotgun
     * of he's bag, only if he have it on it.
     */
    public void gotShotgun() {
    	gunMaterial = new Material(gunsMaterial.get(2));
    	gunAnimationMaterial1 = new Material(gunsAnimationMaterial1.get(2));
    	gunAnimationMaterial2 = new Material(gunsAnimationMaterial2.get(2));
    	gunAnimationMaterial3 = new Material(gunsAnimationMaterial3.get(2));
    	gunAnimationMaterial4 = new Material(gunsAnimationMaterial4.get(2));
    	crossHairMaterial = new Material(crossHairMaterials.get(1));
    	crossHairAnimationMaterial = new Material(crossHairAnimationMaterials.get(1));
        gunNoise = gunsNoiseSounds.get(2);
        gunReload = gunsReloadSounds.get(2);
        gunClipp = gunsClippingSounds.get(2);
        gunEmptyNoise = gunsEmptyNoiseSounds.get(2);
        gunFireAnimationTime = 0.175f;   
        damageMin = 60f;
        damageRange = 60f;
        moveSpeed = 4f;
        isHand = false;
        isBulletBased = false;
        isShellBased = true;
        weaponState = SHOTGUN;
        
        isDoubleShooter = false;
    }
    
    /**
     * The settings that the player sets if he chooses the machine-gun
     * of he's bag, only if he have it on it.
     */
    public void gotMachinegun() {
    	gunMaterial = new Material(gunsMaterial.get(3));
    	gunAnimationMaterial1 = new Material(gunsAnimationMaterial1.get(3));
    	gunAnimationMaterial2 = new Material(gunsAnimationMaterial2.get(3));
    	crossHairMaterial = new Material(crossHairMaterials.get(2));
    	crossHairAnimationMaterial = new Material(crossHairAnimationMaterials.get(2));
        gunNoise = gunsNoiseSounds.get(3);
        gunEmptyNoise = gunsEmptyNoiseSounds.get(3);
        gunFireAnimationTime = 0.08f;   
        damageMin = 20f;
        damageRange = 60f;
        moveSpeed = 4.5f;
        isHand = false;
        isBulletBased = true;
        isShellBased = false;
        weaponState = MACHINEGUN;
        
        isDoubleShooter = false;
    }
    
    /**
     * The settings that the player sets if he chooses the super
     * shotgun of he's bag, only if he have it on it.
     */
    public void gotSShotgun() {
    	gunMaterial = new Material(gunsMaterial.get(4));
    	gunAnimationMaterial1 = new Material(gunsAnimationMaterial1.get(4));
    	gunAnimationMaterial2 = new Material(gunsAnimationMaterial2.get(4));
    	gunAnimationMaterial3 = new Material(gunsAnimationMaterial3.get(4));
    	gunAnimationMaterial4 = new Material(gunsAnimationMaterial4.get(4));
    	crossHairMaterial = new Material(crossHairMaterials.get(1));
    	crossHairAnimationMaterial = new Material(crossHairAnimationMaterials.get(1));
        gunNoise = gunsNoiseSounds.get(4);
        gunReload = gunsReloadSounds.get(4);
        gunClipp = gunsClippingSounds.get(4);
        gunEmptyNoise = gunsEmptyNoiseSounds.get(4);
        gunFireAnimationTime = 0.2f;   
        damageMin = 100f;
        damageRange = 60f;
        moveSpeed = 4f;
        isHand = false;
        isBulletBased = false;
        isShellBased = true;
        weaponState = SUPER_SHOTGUN;
        
        isDoubleShooter = true;
    }

    /**
     * The player's input system.
     */
    public void input() {
    	Vector2f deltaPos = Input.getMousePosition().sub(centerPosition);
    	
        boolean rotY = deltaPos.getX() != 0;
        boolean rotX = deltaPos.getY() != 0;
        
    	if(!(health <= 0)) {
	        if (Input.getKeyDown(Input.KEY_E) || Input.getKeyDown(Input.KEY_SPACE)) {
	            Auschwitz.getLevel().openDoors(Transform.getCamera().getPos(), true);
	        }
	        
	        if (Input.getKeyDown(Input.KEY_1)) {
	        	if(weaponState == HAND) {
	        		AudioUtil.playAudio(missueNoise, 0);
	        	}else {
	        		gotHand();
	        		AudioUtil.playAudio(moveNoise, 0);
	        	}
	        } else if (Input.getKeyDown(Input.KEY_2)) {
	        	if(weaponState == PISTOL) {
	        		AudioUtil.playAudio(missueNoise, 0);
	        	}else {
	        		gotPistol();
	        		AudioUtil.playAudio(moveNoise, 0);
	        	}
	        } else if (Input.getKeyDown(Input.KEY_3)) {
	        	if(shotgun == false || weaponState == SHOTGUN) {
	        		AudioUtil.playAudio(missueNoise, 0);
	        	}else {
	        		gotShotgun();
	        		AudioUtil.playAudio(moveNoise, 0);
	        	}
	        } else if (Input.getKeyDown(Input.KEY_4)) {
	        	if(machinegun == false || weaponState == MACHINEGUN) {
	        		AudioUtil.playAudio(missueNoise, 0);
	        	}else {
	        		gotMachinegun();
	        		AudioUtil.playAudio(moveNoise, 0);
	        	}
	        } else if (Input.getKeyDown(Input.KEY_5)) {
	        	if(sShotgun == false || weaponState == SUPER_SHOTGUN) {
	        		AudioUtil.playAudio(missueNoise, 0);
	        	}else {
	        		gotSShotgun();
	        		AudioUtil.playAudio(moveNoise, 0);
	        	}
	        } else if (Input.getKeyDown(Input.KEY_6)) {
	        	if(chaingun == false || weaponState == CHAINGUN) {
	        		AudioUtil.playAudio(moveNoise, 0);
	        	}else {
	        		//gotChaingun();
	        		AudioUtil.playAudio(missueNoise, 0);
	        	}
	        }
	        
	        if (Input.getKey(Input.KEY_ESCAPE)) {
	            Input.setCursor(true);
	            mouseLocked = false;
	        }
	        
	        if (Input.getMouseDown(0) || Input.getKeyDown(Input.KEY_LCONTROL)) {
	            if (!mouseLocked) {
	                Input.setMousePosition(centerPosition);
	                Input.setCursor(false);
	                mouseLocked = true;
	            } else {
	            	Vector2f shootDirection = playerCamera.getForward().getXZ().normalized();
	        		
		            Vector2f lineStart = playerCamera.getPos().getXZ();
		            Vector2f lineEnd = lineStart.add(shootDirection.mul(1000.0f));
		
		            Auschwitz.getLevel().checkIntersections(lineStart, lineEnd, true);
		            if(isHand == true) {
		            	gunFireTime = (double) Time.getTime() / Time.SECOND;
		            	AudioUtil.playAudio(gunNoise, 0);
		            }
		            if(bullets != 0 && isBulletBased == true) {
		            	if(isDoubleShooter == true)
		            		setBullets(-2);
		            	else
		            		setBullets(-1);
		            	gunFireTime = (double) Time.getTime() / Time.SECOND;
		            	AudioUtil.playAudio(gunNoise, 0);
		            }
		            if(shells != 0 && isShellBased == true) {
		            	if(isDoubleShooter == true)
		            		setShells(-2);
		            	else
		            		setShells(-1);
		            	gunFireTime = (double) Time.getTime() / Time.SECOND;
		            	AudioUtil.playAudio(gunNoise, 0);
		            }
	            }
	        }
	
	        movementVector = zeroVector;
	
	        if (Input.getKey(Input.KEY_W) || Input.getKey(Input.KEY_UP)) {
	            movementVector = movementVector.add(playerCamera.getForward());
	        }
	        if (Input.getKey(Input.KEY_S) || Input.getKey(Input.KEY_DOWN)) {
	            movementVector = movementVector.sub(playerCamera.getForward());
	        }
	        if (Input.getKey(Input.KEY_A)) {
	            movementVector = movementVector.add(playerCamera.getLeft());
	        }
	        if (Input.getKey(Input.KEY_D)) {
	            movementVector = movementVector.add(playerCamera.getRight());
	        }
	        if(Input.getKey(Input.KEY_LEFT)) {
            	playerCamera.rotateY(deltaPos.getX() - SIDE_SENSITIVITY);
	        }
            if(Input.getKey(Input.KEY_RIGHT)) {
            	playerCamera.rotateY(deltaPos.getX() + SIDE_SENSITIVITY);
            }
	
	        if (mouseLocked) {
	            if (rotY) {
	                playerCamera.rotateY(deltaPos.getX() * MOUSE_SENSITIVITY);
	            }
	            
	            //Looking up and down
	             if(rotX) { float amt = -deltaPos.getY() * MOUSE_SENSITIVITY;
	             if(amt + upAngle > -MIN_LOOK_ANGLE) {
	             playerCamera.rotateX(-MIN_LOOK_ANGLE - upAngle); upAngle =
	             -MIN_LOOK_ANGLE; } else if(amt + upAngle < -MAX_LOOK_ANGLE) {
	             playerCamera.rotateX(-MAX_LOOK_ANGLE - upAngle); upAngle =
	             -MAX_LOOK_ANGLE; } else { playerCamera.rotateX(amt); upAngle +=
	             amt; } }
	            
	            if (rotY || rotX) {
	                Input.setMousePosition(new Vector2f(Display.getWidth() / 2, Display.getHeight() / 2));
	            }
	        }
    	}
    }

    /**
     * Updates the player every single frame.
     */
    public void update() {

        float movAmt = (float) (moveSpeed * Time.getDelta());

        movementVector.setY(0);

        Vector3f oldPos = Transform.getCamera().getPos();
        Vector3f newPos = oldPos.add(movementVector.normalized().mul(movAmt));

        Vector3f collisionVector = Auschwitz.getLevel().checkCollisions(oldPos, newPos, width, width);

        movementVector = movementVector.normalized().mul(collisionVector);

        if (movementVector.length() > 0) {
            playerCamera.move(movementVector, movAmt);
        }

        //Gun movement
        gunTransform.setScale(1, 1, 1);
        gunTransform.setPosition(playerCamera.getPos().add(playerCamera.getForward().normalized().mul(GUN_TRANSFORM_MUL)));
        gunTransform.setPosition(gunTransform.getPosition().add(playerCamera.getLeft().normalized().mul(GUN_OFFSET_X)));
        gunTransform.getPosition().setY(gunTransform.getPosition().getY() + GUN_OFFSET);

        Vector3f playerDistance = gunTransform.getPosition().sub(Transform.getCamera().getPos());

        Vector3f orientation = playerDistance.normalized();

        float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));

        if (orientation.getX() >= 0) {
            angle = 180 + angle;
        }

        gunTransform.setRotation(0, angle + 90, 0);
    }

    /**
     * Method that renders the player's mesh.
     */
    public void render() {	
    	
    	double time = (double) Time.getTime() / Time.SECOND;
    	double gunTime = gunFireTime + gunFireAnimationTime;
    	double gunTime2 = gunTime + gunFireAnimationTime;
    	double gunTime3 = gunTime2 + gunFireAnimationTime;
    	double gunTime4 = gunTime3 + gunFireAnimationTime;
    	
    	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), hudMaterial);
        gunMesh.draw();
        
        if((double)time < painTime + 0.5f) {
        	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), painMaterial);
            gunMesh.draw();
        }
        
		if(isHand == true) {
	        if ((double) time < gunTime) {
	            Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), gunAnimationMaterial1);
	            gunMesh.draw();
	        }else if ((double) time < gunTime2) {
	        	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), gunAnimationMaterial2);
	            gunMesh.draw();
	        }else {
	        	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), gunMaterial);
	            gunMesh.draw();
	        }
        }
		if(isBulletBased == true) {
			if((!(bullets <= 0))) {
		        if ((double) time < gunTime) {
		        	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), crossHairAnimationMaterial);
			        gunMesh.draw();
			        
		            Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), gunAnimationMaterial1);
		            gunMesh.draw();
		        }else if ((double) time < gunTime2) {
		        	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), gunAnimationMaterial2);
		            gunMesh.draw();
		        }else {
		        	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), crossHairMaterial);
		            gunMesh.draw();
		        	
		        	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), gunMaterial);
	            	gunMesh.draw();
		        }
			}else {
				Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), crossHairMaterial);
		        gunMesh.draw();
				
				Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), gunMaterial);
            	gunMesh.draw();
			}
		}
		if(isShellBased == true) {
			if((!(shells <= 0))) {
		        if ((double) time < gunTime) {
		        	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), crossHairAnimationMaterial);
			        gunMesh.draw();
			        
		            Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), gunAnimationMaterial1);
		            gunMesh.draw();
		        }else if ((double) time < gunTime2) {
		        	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), crossHairMaterial);
		            gunMesh.draw();
		        	
		            if(isDoubleShooter == false && getShells() > 0) {         	
		            	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), gunAnimationMaterial2);
			            gunMesh.draw();
		            	AudioUtil.playAudio(gunReload, 0);
		            }
		            if(isDoubleShooter == true && getShells() > 1) {
		            	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), gunAnimationMaterial2);
			            gunMesh.draw();
		            	AudioUtil.playAudio(gunReload, 0);
		            }
		        }else if ((double) time < gunTime3) {
		        	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), crossHairMaterial);
		            gunMesh.draw();
		        	
		            if(isDoubleShooter == false && getShells() > 0) {
		            	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), gunAnimationMaterial3);
			            gunMesh.draw();
		            	AudioUtil.playAudio(gunClipp, 0);
		            }
		            if(isDoubleShooter == true && getShells() > 1) {
		            	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), gunAnimationMaterial3);
			            gunMesh.draw();
		            	AudioUtil.playAudio(gunClipp, 0);
		            }
		        }else if ((double) time < gunTime4) {
		        	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), crossHairMaterial);
		            gunMesh.draw();
		        	
		            if(isDoubleShooter == false && getShells() > 0) {
		            	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), gunAnimationMaterial4);
			            gunMesh.draw();
		            }
		            if(isDoubleShooter == true && getShells() > 1) {
		            	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), gunAnimationMaterial4);
			            gunMesh.draw();
		            }
		        }else {
		        	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), crossHairMaterial);
		            gunMesh.draw();
		        	
		        	Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), gunMaterial);
		            gunMesh.draw();
		        }
			}else {
				Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), crossHairMaterial);
		        gunMesh.draw();
				
				Auschwitz.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), gunMaterial);
	            gunMesh.draw();
			}
		}
        
    }

    /**
     * Method that calculates the damage that the player receives.
     * @param amt amount.
     */
    public void health(int amt) {
    	double time = (double) Time.getTime() / Time.SECOND;
    	double timeDecimals = (time - (double) ((int) time));
        health += amt;

        if (health > MAX_LIFE) {
            health = MAX_LIFE;
        }

        if (health <= 0) {
            AudioUtil.playAudio(deathNoise, 0);
            bullets = 0;
            shells = 0;
            armori = 0;
            armorb = false;
            shotgun = false;
            machinegun = false;
            sShotgun = false;
            isAlive = false;
    		gotPistol();
	        //if(Input.getKey(Input.KEY_E)) {
    		if (timeDecimals <= 5.0f) {
	            Auschwitz.loadLevel(Auschwitz.levelNum-Auschwitz.levelNum);
		        //deathNoise.stop();
    		}
	        //}
            //System.exit(0);
        } else {
            if (amt < 0) {
            	painTime = (double) Time.getTime() / Time.SECOND;
                AudioUtil.playAudio(painNoise, 0);
            }
            System.out.println("Life: "+ health + "/" + getMaxLife() + ".");
        }
    }
    
	/**
	 * Gets the player's actual bullets.
	 * @return player's bullets.
	 */
	public int getBullets() {
		return bullets;
	}
    
    /**
     * Method that set the bullets if player get some, or lose some.
     * @param amt amount of bullets to set.
     */
    public void setBullets(int amt) {
        bullets += amt;

        if (bullets > getMaxBullets()) {
        	bullets = getMaxBullets();
        }
        if(isBulletBased == true) {
		   if (bullets <= 0) {
		      AudioUtil.playAudio(gunEmptyNoise, 0);
		      System.out.println("You need more bullets!");
		   	} else {
		      System.out.println("Bullets: "+ bullets + "/"+getMaxBullets()+".");        
	        }
        }
    }
    
    /**
     * Method that assigns the shotgun to the player object.
     * @param amt amount.
     */
    public void setShotgun(boolean amt) {
        if(amt == true && shotgun == false)
    		gotShotgun();
        shotgun = amt;
    }
    
    /**
     * Method that returns if the player have or not a shotgun on 
     * he's bag.
     */
    public boolean getShotgun() {
        return shotgun;
    }
    
    /**
     * Method that assigns the machine-gun to the player object.
     * @param amt amount.
     */
    public void setMachinegun(boolean amt) {
    	if(amt == true && machinegun == false)
    		gotMachinegun();
        machinegun = amt;
    }
    
    /**
     * Method that returns if the player have or not a machine-gun 
     * on he's bag.
     */
    public boolean getMachinegun() {
        return machinegun;
    }
    
    /**
     * Method that assigns the super shotgun to the player object.
     * @param amt amount.
     */
    public void setSuperShotgun(boolean amt) {
    	if(amt == true && sShotgun == false)
    		gotSShotgun();
    	sShotgun = amt;
    }
    
    /**
     * Method that returns if the player have or not a super shotgun 
     * on he's bag.
     */
    public boolean getSuperShotgun() {
        return sShotgun;
    }
    
    /**
     * Method that assigns the armor to the player object.
     * @param amt amount.
     */
    public void setArmorb(boolean amt) {
        armorb = amt;
    }
    
    /**
     * Method that returns if the player have or not an armor
     * on he's bag.
     */
    public boolean getArmorb() {
        return armorb;
    }

    /**
     * Returns the in game camera.
     * @return the camera to player.
     */
    public Camera getCamera() {
        return playerCamera;
    }

    /**
     * Set the camera to the player and takes it as the only camera
     * in all the "world".
     * @param playerCamera the camera in game.
     */
    public void setCamera(Camera playerCamera) {
        this.playerCamera = playerCamera;
    }

    /**
     * Returns the player's size depending on the player's own width,
     * all of this in a Vector2f.
     * @return vector with the size.
     */
    public Vector2f getSize() {
        return new Vector2f(PLAYER_WIDTH, PLAYER_WIDTH);
    }

    /**
     * Returns all the damage that the player could do, depending of the
     * weapon.
     * @return the possible damage.
     */
    public int getDamage() {
        return (int) (damageMin + rand.nextFloat() * damageRange);
    }

    /**
     * Gets the player's actual health.
     * @return player's health.
     */
    public int getHealth() {
        return health;
    }

	/**
	 * Gets the player's maximum number of bullets that can handle.
	 * @return the maximum number of bullets.
	 */
	public int getMaxBullets() {
		return maxBullets;
	}

	/**
	 * Sets the player's maximum number of bullets that could handle.
	 * @param maxBullets the new maximum numbers.
	 */
	public void setMaxBullets(int maxBullets) {
		this.maxBullets += maxBullets;
		if(this.maxBullets == 600) {
			this.maxBullets = 600;
		}
	}
	
	/**
	 * Saves the player's maximum number of bullets that could handle.
	 * @param maxBullets the new maximum numbers.
	 */
	public void saveMaxBullets(int maxBullets) {
		this.maxBullets = maxBullets;
	}

	/**
	 * Gets the player's actual shells.
	 * @return player's shells.
	 */
	public int getShells() {
		return shells;
	}

	/**
     * Method that set the shells if player get some, or lose some.
     * @param amt amount of shells to set.
     */
	public void setShells(int amt) {
		shells += amt;

        if (shells > getMaxShells()) {
        	shells = getMaxShells();
        }
        
        if(isShellBased == true) {
		   if (shells <= 0) {
		      AudioUtil.playAudio(gunEmptyNoise, 1);
		      System.out.println("You need more shells!");
		   } else {
		      System.out.println("Shells: "+ shells + "/"+getMaxShells()+".");        
	       }
        }
	}
	
	/**
	 * Gets the player's actual armor.
	 * @return player's armor.
	 */
	public int getArmori() {
		return armori;
	}

	/**
     * Method that set the armor if player get some, or lose some.
     * @param amt amount of armor to set.
     */
	public void setArmori(int amt) {
		armori += amt;

        if (armori > getMaxArmor()) {
        	armori = getMaxArmor();
        }
        
        if (armori <= 0) {
        	armori = 0;
        	armorb = false;
        }
        
        System.out.println("Armor: "+ armori + "/"+getMaxArmor()+".");
	}

	/**
	 * Gets the player's maximum number of shells that can handle.
	 * @return the maximum number of shells.
	 */
	public int getMaxShells() {
		return maxShells;
	}

	/**
	 * Sets the player's maximum number of shells that could handle.
	 * @param maxShells the new maximum numbers.
	 */
	public void setMaxShells(int maxShells) {
		this.maxShells += maxShells;
		if(this.maxShells == 200) {
			this.maxShells = 200;
		}
	}
	
	/**
	 * Saves the player's maximum number of shells that could handle.
	 * @param maxShells the new maximum numbers.
	 */
	public void saveMaxShells(int maxShells) {
		this.maxShells = maxShells;
	}
	
	/**
	 * Sets the player's maximum number of armor that could handle.
	 * @param amt the new maximum numbers.
	 */
	public void setMaxArmor(int amt) {
		this.maxArmor = amt;
	}
	
	/**
	 * Gets the player's maximum number of armor that can handle.
	 * @return the maximum number of shells.
	 */
	public int getMaxArmor() {
		return maxArmor;
	}
	
	/**
	 * Saves the player's maximum number of armor that could handle.
	 * @param amt the new maximum numbers.
	 */
	public void saveMaxArmor(int amt) {
		this.maxArmor += amt;
		if(maxArmor == 200) {
			maxArmor = 200;
		}
	}

	/**
	 * Gets the player's maximum number of life that can have.
	 * @return the maximum number of life.
	 */
	public int getMaxLife() {
		return MAX_LIFE;
	}
	
	/**
	 * Gets the player's weapon that currently is using.
	 * @return the weapon that player is using.
	 */
	public String getWeaponState() {
		return weaponState;
	}
	
	/**
	 * Sets the player's weapon that currently is using.
	 * @param amt the weapon that player is using.
	 */
	public void setWeaponState(String amt) {
		this.weaponState = amt;
	}

}
