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
package game.powerUp;

import javax.sound.sampled.Clip;

import engine.ResourceLoader;
import engine.audio.AudioUtil;
import engine.core.GameComponent;
import engine.core.Transform;
import engine.core.Vector2f;
import engine.core.Vector3f;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.Vertex;
import game.Auschwitz;
import game.Level;

/**
*
* @author Carlos Rodriguez
* @version 1.2
* @since 2017
*/
public class Bag implements GameComponent {

    private static final float PICKUP_THRESHHOLD = 0.75f;
    private static final int BULLET_AMOUNT = Level.getPlayer().getMaxBullets();
    private static final int SHELL_AMOUNT = Level.getPlayer().getMaxShells();
    private static final int ARMOR_AMOUNT = Level.getPlayer().getMaxArmor();
	
	private static final String RES_LOC = "bag/MEDIA";
   
    private static final Clip pickupNoise = ResourceLoader.loadAudio(RES_LOC);

    private static Mesh mesh;
    private static Material material;

    private Transform transform;

    /**
     * Constructor of the actual power-up.
     * @param transform the transform of the data.
     */
    public Bag(Transform transform) {
        if (mesh == null) {
            mesh = new Mesh();

            float sizeY = 0.3f;
            float sizeX = (float) ((double) sizeY / (1.318181818181818 * 2.0));

            float offsetX = 0.05f;
            float offsetY = 0.01f;

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

            mesh.addVertices(verts, indices);
        }

        if (material == null) {
            material = new Material(ResourceLoader.loadTexture(RES_LOC));
        }

        this.transform = transform;
    }

    /**
     * Updates the power-up every single frame.
     */
    public void update() {
        Vector3f playerDistance = transform.getPosition().sub(Transform.getCamera().getPos());

        Vector3f orientation = playerDistance.normalized();
        float distance = playerDistance.length();

        float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));

        if (orientation.getX() > 0) {
            angle = 180 + angle;
        }

        transform.setRotation(0, angle + 90, 0);
        

        Level.getPlayer();
		if (distance < PICKUP_THRESHHOLD) {
            Level.getPlayer().setMaxBullets(BULLET_AMOUNT);
            Level.getPlayer().setMaxShells(SHELL_AMOUNT);
            Level.getPlayer().setMaxArmor(ARMOR_AMOUNT);
            Level.removeBags(this);
            AudioUtil.playAudio(pickupNoise, 0);
        }
    }

    /**
     * Method that renders the power-up's mesh.
     */
    public void render() {
        Auschwitz.updateShader(transform.getTransformation(), transform.getPerspectiveTransformation(), material);
        mesh.draw();
    }
}