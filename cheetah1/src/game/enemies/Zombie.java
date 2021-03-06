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
package game.enemies;

import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.Clip;

import engine.audio.AudioUtil;
import engine.components.GameComponent;
import engine.components.MeshRenderer;
import engine.core.Time;
import engine.core.Transform;
import engine.core.Vector2f;
import engine.core.Vector3f;
import engine.core.utils.Util;
import engine.physics.PhysicsUtil;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.RenderingEngine;
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.rendering.Vertex;
import game.Auschwitz;
import game.Level;
import game.Player;
import game.pickUps.Key;
import game.projectiles.ZombieMeat;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.1
 * @since 2018
 */
public class Zombie extends GameComponent {

    private static final float MAX_HEALTH = 300f;
    private static final float SHOT_ANGLE = 10.0f;
    private static final float DAMAGE_MIN = 15f;
    private static final float DAMAGE_RANGE = 20f;
    private static final float ZOMBIE_WIDTH = 0.4f;

    private static final int STATE_IDLE = 0;
    private static final int STATE_CHASE = 1;
    private static final int STATE_ATTACK = 2;
    private static final int STATE_DYING = 3;
    private static final int STATE_DEAD = 4;
    private static final int STATE_DONE = 5;
    private static final int STATE_HIT = 6;
    private static final int STATE_POST_DEATH = 7;
    
    private static final String RES_LOC = "zombie/";
    
    private static ArrayList<ZombieMeat> gibs;
    private static ArrayList<ZombieMeat> removeGibs;

    private static Clip seeNoise;
    private static Clip attackNoise;
    private static Clip hitNoise;
    private static Clip deathNoise;

    private static ArrayList<Texture> animation;
    private static ArrayList<Texture> animation1;
    private static ArrayList<Texture> animation2;
    private static ArrayList<Clip> seeNoises;
    private static ArrayList<Clip> attackNoises;
    private static ArrayList<Clip> hitNoises;
    private static ArrayList<Clip> deathNoises;
    private static Mesh mesh;
    private static Random rand;

    private Transform transform;
    private Material material;
    private MeshRenderer meshRenderer;
    private Key key;

    private int state;
    public boolean isQuiet;
    private boolean canAttack;
    private boolean canLook;
    private boolean dead;
    private boolean drops;
    private double deathTime;
    private double health;
    private int zombieSeed;

    /**
     * Constructor of the actual enemy.
     * @param transform the transform of the data.
     * @param drops if it does
     */
    public Zombie(Transform transform, boolean drops) {
        rand = new Random();

        this.zombieSeed = Util.randomInRange(1, 2);
        
        animation1 = new ArrayList<Texture>();

            animation1.add(new Texture(RES_LOC + "ZOCHA0"));
            animation1.add(new Texture(RES_LOC + "ZOCHB0"));
            animation1.add(new Texture(RES_LOC + "ZOCHC0"));
            animation1.add(new Texture(RES_LOC + "ZOCHD0"));
            animation1.add(new Texture(RES_LOC + "ZOCHE0"));
            animation1.add(new Texture(RES_LOC + "ZOCHF0"));
            
            animation1.add(new Texture(RES_LOC + "ZOATA0"));
            animation1.add(new Texture(RES_LOC + "ZOATB0"));
            animation1.add(new Texture(RES_LOC + "ZOATC0"));
            
            animation1.add(new Texture(RES_LOC + "ZOHTA0"));
            
            animation1.add(new Texture(RES_LOC + "ZODTA0"));
            animation1.add(new Texture(RES_LOC + "ZODTB0"));
            animation1.add(new Texture(RES_LOC + "ZODTC0"));
            animation1.add(new Texture(RES_LOC + "ZODTD0"));
            animation1.add(new Texture(RES_LOC + "ZODTE0"));
            animation1.add(new Texture(RES_LOC + "ZODTF0"));
            animation1.add(new Texture(RES_LOC + "ZODTG0"));
            animation1.add(new Texture(RES_LOC + "ZODTH0"));

        animation2 = new ArrayList<Texture>();

            animation2.add(new Texture(RES_LOC + "ZOCHA1"));
            animation2.add(new Texture(RES_LOC + "ZOCHB1"));
            animation2.add(new Texture(RES_LOC + "ZOCHC1"));
            animation2.add(new Texture(RES_LOC + "ZOCHD1"));
            
            animation2.add(new Texture(RES_LOC + "ZOATA1"));
            animation2.add(new Texture(RES_LOC + "ZOATB1"));
            animation2.add(new Texture(RES_LOC + "ZOATC1"));
            animation2.add(new Texture(RES_LOC + "ZOATD1"));
            animation2.add(new Texture(RES_LOC + "ZOATE1"));
            
            animation2.add(new Texture(RES_LOC + "ZOHTA1"));
            
            animation2.add(new Texture(RES_LOC + "ZODTA1"));
            animation2.add(new Texture(RES_LOC + "ZODTB1"));
            animation2.add(new Texture(RES_LOC + "ZODTC1"));
            animation2.add(new Texture(RES_LOC + "ZODTD1"));
            animation2.add(new Texture(RES_LOC + "ZODTE1"));
            
            animation2.add(new Texture(RES_LOC + "ZODTA2"));
            animation2.add(new Texture(RES_LOC + "ZODTB2"));
            animation2.add(new Texture(RES_LOC + "ZODTC2"));
            animation2.add(new Texture(RES_LOC + "ZODTD2"));
            animation2.add(new Texture(RES_LOC + "ZODTE2"));
		
		switch(zombieSeed) {
			case 1:
				animation = animation1;
				break;
			case 2:
				animation = animation2;
				break;
		}
        
    	seeNoises = new ArrayList<Clip>();
        	
        	for(int i = 0; i < 3; i++)
        		seeNoises.add(AudioUtil.loadAudio(RES_LOC + "see" + i));
        
    	attackNoises = new ArrayList<Clip>();
        	
        	for(int i = 0; i < 3; i++)
        		attackNoises.add(AudioUtil.loadAudio(RES_LOC + "atack" + i));
        
    	hitNoises = new ArrayList<Clip>();
        	
        	for(int i = 0; i < 3; i++)
        		hitNoises.add(AudioUtil.loadAudio(RES_LOC + "hit" + i));
        
    	deathNoises = new ArrayList<Clip>();
        	
        	for(int i = 0; i < 3; i++)
        		deathNoises.add(AudioUtil.loadAudio(RES_LOC + "death" + i));
        
        gibs = new ArrayList<ZombieMeat>();
        
        removeGibs = new ArrayList<ZombieMeat>();

        if (mesh == null) {
            final float sizeY = 0.9f;
            final float sizeX = (float) ((double) sizeY / (1.558139534883721 * 2.0));

            final float offsetX = 0.0f;
            final float offsetY = 0.0f;

            final float texMinX = -offsetX;
            final float texMaxX = -1 - offsetX;
            final float texMinY = -offsetY;
            final float texMaxY = 1 - offsetY;

            Vertex[] verts = new Vertex[]{new Vertex(new Vector3f(-sizeX, 0, 0), new Vector2f(texMaxX, texMaxY)),
                new Vertex(new Vector3f(-sizeX, sizeY, 0), new Vector2f(texMaxX, texMinY)),
                new Vertex(new Vector3f(sizeX, sizeY, 0), new Vector2f(texMinX, texMinY)),
                new Vertex(new Vector3f(sizeX, 0, 0), new Vector2f(texMinX, texMaxY))};

            int[] indices = new int[]{0, 1, 2,
                                        0, 2, 3};

            mesh = new Mesh(verts, indices, true);
        } 
        
        this.transform = transform;
        this.material = new Material(animation.get(0));
        this.meshRenderer = new MeshRenderer(mesh, getTransform(), material);
        this.state = 0;
        this.canAttack = true;
        this.canLook = true;
        this.dead = false;
        this.deathTime = 0.0;
        this.health = MAX_HEALTH;
        this.drops = drops;
    }

    float offsetX = 0;
    float offsetY = 0;

    /**
     * Updates the enemy every single frame.
     * @param delta of time
     */
    public void update(double delta) {
    	
    	//Set Height
        transform.setPosition(transform.getPosition().getX(), 0, transform.getPosition().getZ());
        
        Vector3f playerDistance = transform.getPosition().sub(Level.getPlayer().getCamera().getPos());

        Vector3f orientation = playerDistance.normalized();
        float distance = playerDistance.length();
        setDistance(distance);

        float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));

        if (orientation.getX() > 0) {
            angle = 180 + angle;
        }

        transform.setRotation(0, angle + 90, 0);

        //Action/Animation
        double time = Time.getTime();

        if (!dead && health <= 0) {
        	if(drops)
	            key = new Key(new Transform(transform.getPosition()), true, false);
            dead = true;
            deathTime = time;
            state = STATE_DYING;
            if(seeNoise != null) seeNoise.stop();
            if(attackNoise != null) attackNoise.stop();
            if(hitNoise != null) hitNoise.stop();
            deathNoise = deathNoises.get(new Random().nextInt(deathNoises.size()));
            AudioUtil.playAudio(deathNoise, distance);
        }
    	
    	switch(zombieSeed) {
    		case 1:
    	        if (!dead) {
    	            Player player = Level.getPlayer();

    	            Vector2f playerDirection = transform.getPosition().sub(
    	                    player.getCamera().getPos().add(
    	                            new Vector3f(player.getSize().getX(), 0, player.getSize().getY()).mul(0.5f))).getXZ().normalized();
    	            switch(state) {
    	            	case STATE_IDLE:
    	            		isQuiet = true;
        	                double timeDecimals = (time - (double) ((int) time));

        	                if (timeDecimals >= 0.5) {
        	                	transform.setScale(1.372093023255814f,0.728813559322034f,1);
        	                    material.setDiffuse(animation.get(1));
        	                    canLook = true;
        	                } else {
        	                	transform.setScale(1.558139534883721f,0.641791044776119f,1);
        	                    material.setDiffuse(animation.get(0));
        	                    if (canLook) {
        	                        Vector2f lineStart = transform.getPosition().getXZ();
        	                        Vector2f lineEnd = lineStart.sub(playerDirection.mul(1000.0f));

        	                        Vector2f nearestIntersect = Auschwitz.getLevel().checkIntersections(lineStart, lineEnd, false);
        	                        Vector2f playerIntersect = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, player.getCamera().getPos().getXZ(), player.getSize());

        	                        if (playerIntersect != null && (nearestIntersect == null
        	                                || nearestIntersect.sub(lineStart).length() > playerIntersect.sub(lineStart).length())) {
        	                        	seeNoise = seeNoises.get(new Random().nextInt(seeNoises.size()));
        	                        	AudioUtil.playAudio(seeNoise, distance);
        	                            state = STATE_CHASE;
        	                        }

        	                        canLook = false;
        	                    }
        	                }
    	            		break;
    	            	case STATE_CHASE:
    	            		isQuiet = false;

        	                if (distance > 0.55f) {
        	                	if (rand.nextDouble() < 0.5f * delta)
            	                    state = STATE_ATTACK;
        	                    orientation.setY(0);
        	                    float moveSpeed = 1.5f;

        	                    Vector3f oldPos = transform.getPosition();
        	                    Vector3f newPos = transform.getPosition().add(orientation.mul((float) (-moveSpeed * delta)));

        	                    Vector3f collisionVector = Auschwitz.getLevel().checkCollisions(oldPos, newPos, ZOMBIE_WIDTH, ZOMBIE_WIDTH);

        	                    Vector3f movementVector = collisionVector.mul(orientation.normalized());

        	                    if (!movementVector.equals(orientation.normalized())) {
        	                        Auschwitz.getLevel().openDoors(transform.getPosition(), false);
        	                    }

        	                    if (movementVector.length() > 0) {
        	                        transform.setPosition(transform.getPosition().add(movementVector.mul((float) (-moveSpeed * delta))));
        	                    }
        	                } else {
        	                    state = STATE_ATTACK;
        	                }
        	                
    	                	isQuiet = false;
    	                    timeDecimals = (time - (double) ((int) time));

    	                    while (timeDecimals > 0.5) {
    	                        timeDecimals -= 0.5;
    	                    }

    	                    timeDecimals *= 1.5f;

    	                    if (timeDecimals <= 0.25f) {
    	                    	transform.setScale(1.558139534883721f,0.641791044776119f,1);
    	                        material.setDiffuse(animation.get(0));
    	                    } else if (timeDecimals <= 0.5f) {
    	                    	transform.setScale(1.372093023255814f,0.728813559322034f,1);
    	                        material.setDiffuse(animation.get(1));
    	                    } else if (timeDecimals <= 0.75f) {
    	                    	transform.setScale(1.5f,0.666666666666667f,1);
    	                        material.setDiffuse(animation.get(2));
    	                    } else  if (timeDecimals <= 1f) {
    	                    	transform.setScale(1.523809523809524f,0.65625f,1);
    	                        material.setDiffuse(animation.get(3));
    	                    } else if (timeDecimals <= 1.25f) {
    	                    	transform.setScale(1.560975609756098f,0.640625f,1);
    	                        material.setDiffuse(animation.get(4));
    	                    } else {
    	                    	transform.setScale(1.560975609756098f,0.640625f,1);
    	                        material.setDiffuse(animation.get(5));
    	                    }
    	            		break;
    	            	case STATE_ATTACK:
    	            		isQuiet = true;
        	                timeDecimals = (time - (double) ((int) time));

        	                if (timeDecimals <= 0.25f) {
        	                	//transform.setScale(2.296296296296296f,0.435483870967742f,1);
        	                    material.setDiffuse(animation.get(6));
        	                } else if (timeDecimals <= 0.5f) {
        	                	transform.setScale(1.333333333333333f,0.75f,1);
        	                    material.setDiffuse(animation.get(7));
        	                } else if (timeDecimals <= 0.7f) {
        	                    if (canAttack) {
        	                        Vector2f shootDirection = playerDirection.rotate((rand.nextFloat() - 0.5f) * SHOT_ANGLE);

        	                        Vector2f lineStart = transform.getPosition().getXZ();
        	                        Vector2f lineEnd = lineStart.sub(shootDirection.mul(1000.0f));

        	                        Vector2f nearestIntersect = Auschwitz.getLevel().checkIntersections(lineStart, lineEnd, false);
        	                        canAttack = false;

        	                        Vector2f playerIntersect = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, player.getCamera().getPos().getXZ(), player.getSize());

        	                        if (playerIntersect != null && (nearestIntersect == null
        	                                || nearestIntersect.sub(lineStart).length() > playerIntersect.sub(lineStart).length())) {

        	                        	float damage;
        	                            if(player.getHealth() <= 0) {
        	                            	state = STATE_DONE;
        	                            }else {
        	                            	damage = DAMAGE_MIN + rand.nextFloat() * DAMAGE_RANGE;
        	                            	if(player.isArmor() == false) {
        	                            		player.addHealth((int) -damage, "Zombie");
        	                            	}else {
        	                            		player.addArmor((int) -damage);
        	                            	}
        	                            }
        	                            
        	                        }
        	                        attackNoise = attackNoises.get(new Random().nextInt(attackNoises.size()));
        	                        AudioUtil.playAudio(attackNoise, distance);
        	                    }
        	                    transform.setScale(1.580645161290323f,0.75f,1);
        	                    material.setDiffuse(animation.get(8));
        	                } else {
        	                    canAttack = true;
        	                    transform.setScale(1.333333333333333f,0.63265306122449f,1);
        	                    material.setDiffuse(animation.get(7));
        	                    state = STATE_CHASE;
        	                }
    	            		break;
    	            	case STATE_DONE:
    	            		isQuiet = true;
    	    	        	timeDecimals = (time - (double) ((int) time));

    	    	        	timeDecimals *= 1.5f;

    	    	            if (timeDecimals <= 0.25f) {
    	    	            	transform.setScale(1.558139534883721f,0.641791044776119f,1);
    	    	                material.setDiffuse(animation.get(0));
    	    	            } else if (timeDecimals <= 0.5f) {
    	    	            	transform.setScale(1.372093023255814f,0.728813559322034f,1);
    	    	                material.setDiffuse(animation.get(1));
    	    	            } else if (timeDecimals <= 0.75f) {
    	    	            	transform.setScale(1.5f,0.666666666666667f,1);
    	    	                material.setDiffuse(animation.get(2));
    	    	            } else {
    	    	            	transform.setScale(1.523809523809524f,0.65625f,1);
    	    	                material.setDiffuse(animation.get(3));
    	    	            }
    	            		break;
    	            	case STATE_HIT:
    	            		timeDecimals = (time - (double) ((int) time));
    	    	            if (timeDecimals <= 0.5f) {
    	    	            	transform.setScale(1.25f,0.8f,1);
    	    	                material.setDiffuse(animation.get(10));
    	    	            } else {
    	    	                state = STATE_CHASE;
    	    	            }
    	            		break;
    	            }
    	        }
    	        
    	        switch(state) {
    	        	case STATE_DYING:
    	        		isQuiet = true;
        	            dead = true;

        	            final float time1 = 0.1f;
        	            final float time2 = 0.3f;
        	            final float time3 = 0.5f;
        	            final float time4 = 0.7f;
        	            final float time5 = 0.9f;
        	            final float time6 = 1.1f;

        	            if (time <= deathTime + 0.2f) {
        	            	transform.setScale(1.710526315789474f,0.584615384615385f,1);
        	                material.setDiffuse(animation.get(10));
        	            } else if (time > deathTime + time1 && time <= deathTime + time2) {
        	            	transform.setScale(2.096774193548387f,0.476923076923077f,1);
        	                material.setDiffuse(animation.get(11));
        	            } else if (time > deathTime + time2 && time <= deathTime + time3) {
        	            	transform.setScale(1.896551724137931f,0.527272727272727f,1);
        	                material.setDiffuse(animation.get(12));
        	            } else if (time > deathTime + time3 && time <= deathTime + time4) {
        	            	transform.setScale(1.793103448275862f,0.557692307692308f,1);
        	                material.setDiffuse(animation.get(13));
        	            } else if (time > deathTime + time4 && time <= deathTime + time5) {
        	            	transform.setScale(1.586206896551724f,0.630434782608696f,1);
        	                material.setDiffuse(animation.get(14));
        	            } else if (time > deathTime + time4 && time <= deathTime + time6) {
        	            	transform.setScale(1.586206896551724f,0.630434782608696f,1);
        	                material.setDiffuse(animation.get(15));
        	            } else if (time > deathTime + time6) {
        	                state = STATE_DEAD;
        	            }
    	        		break;
    	        	case STATE_DEAD:
    	        		isQuiet = true;
        	            dead = true;
        	            if(drops) {
        		            key.update(delta);
        		            if (distance < key.PICKUP_THRESHHOLD && drops)
        		            	state = STATE_POST_DEATH;
        	            }
        	            transform.setScale(1.7586206896551724137931034482759f,0.28571428571428571428571428571429f,1);
        	            material.setDiffuse(animation.get(16));
    	        		break;
    	        	case STATE_POST_DEATH:
    	        		transform.setScale(1.7586206896551724137931034482759f,0.28571428571428571428571428571429f,1);
        	            material.setDiffuse(animation.get(17));
        	            break;
    	        }
    			break;
    		case 2:
    	        
    	        if(!gibs.isEmpty())
    	        	for(ZombieMeat giblets : gibs)
    	        		giblets.update(delta);

    	        if (!dead) {
    	        	Player player = Level.getPlayer();
    	        	Vector2f playerDirection = transform.getPosition().sub(
    	                    player.getCamera().getPos().add(
    	                            new Vector3f(player.getSize().getX(), 0, player.getSize().getY()).mul(0.5f))).getXZ().normalized();
    	           switch(state) {
    	           		case STATE_IDLE:
    	           			isQuiet = true;
        	                double timeDecimals = (time - (double) ((int) time));

        	                if (timeDecimals >= 0.5) {
        	                	transform.setScale(1.341935483870968f,0.774074074074074f,1);
        	                    material.setDiffuse(animation.get(1));
        	                    canLook = true;
        	                } else {
        	                	transform.setScale(1.374193548387097f,0.763636363636364f,1);
        	                    material.setDiffuse(animation.get(0));
        	                    if (canLook) {
        	                        Vector2f lineStart = transform.getPosition().getXZ();
        	                        Vector2f lineEnd = lineStart.sub(playerDirection.mul(1000.0f));

        	                        Vector2f nearestIntersect = Auschwitz.getLevel().checkIntersections(lineStart, lineEnd, false);
        	                        Vector2f playerIntersect = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, player.getCamera().getPos().getXZ(), player.getSize());

        	                        if (playerIntersect != null && (nearestIntersect == null
        	                                || nearestIntersect.sub(lineStart).length() > playerIntersect.sub(lineStart).length())) {
        	                        	seeNoise = seeNoises.get(new Random().nextInt(seeNoises.size()));
        	                        	AudioUtil.playAudio(seeNoise, distance);
        	                            state = STATE_CHASE;
        	                        }

        	                        canLook = false;
        	                    }
        	                }
    	           			break;
    	           		case STATE_CHASE:
    	           			isQuiet = false;
        	                if (rand.nextDouble() < 0.5f * delta)
        	                    state = STATE_ATTACK;

        	                if (distance > 2.0f) {
        	                    orientation.setY(0);
        	                    float moveSpeed = 1.0f;

        	                    Vector3f oldPos = transform.getPosition();
        	                    Vector3f newPos = transform.getPosition().add(orientation.mul((float) (-moveSpeed * delta)));

        	                    Vector3f collisionVector = Auschwitz.getLevel().checkCollisions(oldPos, newPos, ZOMBIE_WIDTH, ZOMBIE_WIDTH);

        	                    Vector3f movementVector = collisionVector.mul(orientation.normalized());

        	                    if (!movementVector.equals(orientation.normalized())) {
        	                        Auschwitz.getLevel().openDoors(transform.getPosition(), false);
        	                    }

        	                    if (movementVector.length() > 0) {
        	                        transform.setPosition(transform.getPosition().add(movementVector.mul((float) (-moveSpeed * delta))));
        	                    }
        	                } else {
        	                    state = STATE_ATTACK;
        	                }

    	                	isQuiet = false;
    	                    timeDecimals = (time - (double) ((int) time));

    	                    while (timeDecimals > 0.5) { timeDecimals -= 0.5; }
    	                    
    	                    timeDecimals *= 1.5f;

    	                    if (timeDecimals <= 0.25f) {
    	                    	transform.setScale(1.374193548387097f,0.763636363636364f,1);
    	                        material.setDiffuse(animation.get(0));
    	                    } else if (timeDecimals <= 0.5f) {
    	                    	transform.setScale(1.341935483870968f,0.774074074074074f,1);
    	                        material.setDiffuse(animation.get(1));
    	                    } else if (timeDecimals <= 0.75f) {
    	                    	transform.setScale(1.370967741935484f,0.73448275862069f,1);
    	                        material.setDiffuse(animation.get(2));
    	                    } else {
    	                    	transform.setScale(1.406451612903226f,0.753571428571429f,1);
    	                        material.setDiffuse(animation.get(3));
    	                    }             
    	           			break;
    	           		case STATE_ATTACK:
    	           			isQuiet = true;
        	                timeDecimals = (time - (double) ((int) time));

        	                if (timeDecimals <= 0.25f) {
        	                	transform.setScale(1.370967741935484f,0.73448275862069f,1);
        	                    material.setDiffuse(animation.get(4));
        	                } else if (timeDecimals <= 0.5f) {
        	                	transform.setScale(1.318181818181818f,0.758620689655172f,1);
        	                    material.setDiffuse(animation.get(5));
        	                } else if (timeDecimals <= 0.7f) {
        	                    transform.setScale(1.318181818181818f,0.758620689655172f,1);
        	                    material.setDiffuse(animation.get(6));
        	                } else if (timeDecimals <= 0.9f) {
        	                    if (canAttack) {
        	                    	gibs.add(new ZombieMeat(new Transform(getTransform().getPosition())));
        	                        canAttack = false;
        	                        attackNoise = attackNoises.get(new Random().nextInt(attackNoises.size()));
        	                        AudioUtil.playAudio(attackNoise, distance);
        	                        if(player.getHealth() <= 0)
    	                            	state = STATE_DONE;
        	                    }
        	                    transform.setScale(1.318181818181818f,0.758620689655172f,1);
        	                    material.setDiffuse(animation.get(7));
        	                } else {
        	                    canAttack = true;
        	                    transform.setScale(1.318181818181818f,0.758620689655172f,1);
        	                    material.setDiffuse(animation.get(8));
        	                    state = STATE_CHASE;
        	                }
    	           			break;
    	           		case STATE_DONE:
    	           			isQuiet = true;
    	    	        	timeDecimals = (time - (double) ((int) time));

    	    	        	timeDecimals *= 1.5f;

    	    	            if (timeDecimals <= 0.25f) {
    	    	            	transform.setScale(1.374193548387097f,0.763636363636364f,1);
    	    	                material.setDiffuse(animation.get(0));
    	    	            } else if (timeDecimals <= 0.5f) {
    	    	            	transform.setScale(1.341935483870968f,0.774074074074074f,1);
    	    	                material.setDiffuse(animation.get(1));
    	    	            } else if (timeDecimals <= 0.75f) {
    	    	            	transform.setScale(1.470967741935484f,0.73448275862069f,1);
    	    	                material.setDiffuse(animation.get(2));
    	    	            } else {
    	    	            	transform.setScale(1.406451612903226f,0.753571428571429f,1);
    	    	                material.setDiffuse(animation.get(3));
    	    	            }
    	    	            break;
    	           		case STATE_HIT:
    	           			timeDecimals = (time - (double) ((int) time));
    	    	            if (timeDecimals <= 0.5f) {
    	    	            	transform.setScale(1.355555555555556f,0.742857142857143f,1);
    	    	                material.setDiffuse(animation.get(9));
    	    	            } else {
    	    	                state = STATE_CHASE;
    	    	            }
    	           			break;
    	           			
    	           }
    	        }

    	        if(Level.getPlayer().getWeaponState() != "rocketLauncher") {
    	        	switch(state) {
    	        		case STATE_DYING:
    	        			isQuiet = true;
        		            dead = true;
        		
        		            final float time1 = 0.1f;
        		            final float time2 = 0.3f;
        		            final float time3 = 0.5f;
        		            final float time4 = 0.7f;
        		
        		            if (time <= deathTime + 0.2f) {
            	            	transform.setScale(1.710526315789474f,0.584615384615385f,1);
            	                material.setDiffuse(animation.get(10));
            	            } else if (time > deathTime + time1 && time <= deathTime + time2) {
            	            	transform.setScale(2.096774193548387f,0.476923076923077f,1);
            	                material.setDiffuse(animation.get(11));
            	            } else if (time > deathTime + time2 && time <= deathTime + time3) {
            	            	transform.setScale(1.896551724137931f,0.527272727272727f,1);
            	                material.setDiffuse(animation.get(12));
            	            } else if (time > deathTime + time3 && time <= deathTime + time4) {
            	            	transform.setScale(1.793103448275862f,0.557692307692308f,1);
            	                material.setDiffuse(animation.get(13));
            	            } else if (time > deathTime + time4) {
            	                state = STATE_DEAD;
            	            }
    	        			break;
    	        		case STATE_DEAD:
    	        			isQuiet = true;
        		            dead = true;
        		            if(drops)
        			            key.update(delta);
        		            transform.setScale(1.7586206896551724137931034482759f,0.28571428571428571428571428571429f,1);
        		            material.setDiffuse(animation.get(14));
        		            if(drops && distance < key.PICKUP_THRESHHOLD)
        		            	state = STATE_POST_DEATH;
    	        			break;
    	        		case STATE_POST_DEATH:
    	        			transform.setScale(1.7586206896551724137931034482759f,0.28571428571428571428571428571429f,1);
        		            material.setDiffuse(animation.get(14));
    	        			break;
    	        	}
    	        } else {
    	        	switch(state) {
    	        		case STATE_DYING:
    	        			isQuiet = true;
        		            dead = true;
        		
        		            final float time1 = 0.1f;
        		            final float time2 = 0.3f;
        		            final float time3 = 0.5f;
        		            final float time4 = 0.7f;
        		
        		            if (time <= deathTime + 0.2f) {
        		            	transform.setScale(1.157894736842105f,0.863636363636364f,1);
        		                material.setDiffuse(animation.get(15));
        		            } else if (time > deathTime + time1 && time <= deathTime + time2) {
        		            	transform.setScale(0.975f,1.025641025641026f,1);
        		                material.setDiffuse(animation.get(16));
        		            } else if (time > deathTime + time2 && time <= deathTime + time3) {
        		            	transform.setScale(0.532258064516129f,1.878787878787879f,1);
        		                material.setDiffuse(animation.get(17));
        		            } else if (time > deathTime + time3 && time <= deathTime + time4) {
        		            	transform.setScale(0.508196721311475f,1.967741935483871f,1);
        		                material.setDiffuse(animation.get(18));
        		            } else if (time > deathTime + time4) {
        		                state = STATE_DEAD;
        		            }
    	        			break;
    	        		case STATE_DEAD:
    	        			isQuiet = true;
        		            dead = true;
        		            if(drops)
        			            key.update(delta);
        		            transform.setScale(1.7586206896551724137931034482759f,0.28571428571428571428571428571429f,1);
        		            material.setDiffuse(animation.get(19));
        		            if(drops && distance < key.PICKUP_THRESHHOLD)
        		            	state = STATE_POST_DEATH;
    	        			break;
    	        		case STATE_POST_DEATH:
    	        			transform.setScale(1.7586206896551724137931034482759f,0.28571428571428571428571428571429f,1);
        		            material.setDiffuse(animation.get(19));
    	        			break;
    	        }
    	        
    	    }	        
	        
    	        if(!removeGibs.isEmpty())
			       	for (ZombieMeat gibToRemove : removeGibs) 
			       		gibs.remove(gibToRemove);   
					      
    	        removeGibs.clear();
    			break;
    	}     	
    }

    /**
     * Method that calculates the damage that the enemy receives.
     * @param amt amount.
     */
    public void damage(int amt) {
        if (state == STATE_IDLE) {
            state = STATE_CHASE;
        }

        health -= amt;

        if (health > 0 && amt > 0) {
        	state = STATE_HIT;
        	hitNoise = hitNoises.get(new Random().nextInt(hitNoises.size()));
            AudioUtil.playAudio(hitNoise, transform.getPosition().sub(Level.getPlayer().getCamera().getPos()).length());
        }
    }

    /**
     * Method that renders the enemy's mesh.
     * @param shader to render
     * @param renderingEngine to use
     */
    public void render(Shader shader, RenderingEngine renderingEngine) {
        Vector3f prevPosition = transform.getPosition();
        transform.setPosition(new Vector3f(transform.getPosition().getX() + offsetX, transform.getPosition().getY() + offsetY, transform.getPosition().getZ()));

        meshRenderer.render(shader, renderingEngine);
        
        if (state == STATE_DEAD && drops)
        	key.render(shader, renderingEngine);
        
        if(zombieSeed == 2)
        	if(!gibs.isEmpty())
        		for(ZombieMeat giblets : gibs)
        			giblets.render(shader, renderingEngine);

        transform.setPosition(prevPosition);
    }

    /**
	 * Gets the enemy's actual transformation.
	 * @return the enemy's transform data.
	 */
    public Transform getTransform() {return transform;}

    /**
	 * Gets if the enemy is dead or not.
	 * @return the enemy's life state.
	 */
    public boolean isAlive() {return !dead;}

    /**
     * Returns the enemy's size depending on the enemy's own width,
     * all of this in a Vector2f.
     * @return vector with the size.
     */
    public Vector2f getSize() {return new Vector2f(ZOMBIE_WIDTH, ZOMBIE_WIDTH);}

    /**
     * Gets the enemy's actual health.
     * @return enemy's health.
     */
	public double getHealth() {return health;}
	
	/**
	 * Removes the GIBS when disappears.
	 * @param zombieMeat GIBS.
	 */
	public static void removeGibs(ZombieMeat zombieMeat) { removeGibs.add(zombieMeat); }
    
}