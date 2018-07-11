/*
 * Copyright 2017 Julio Vergara.
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
package game.objects;

import engine.core.ResourceLoader;
import engine.core.Transform;
import engine.core.Vector2f;
import engine.core.Vector3f;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.MeshRenderer;
import engine.rendering.Shader;
import engine.rendering.Vertex;

/**
 *
 * @author Julio Vergara.
 * @version 1.1
 * @since 2017
 */
public class Bones {
    
    private static Mesh mesh;
    private static Material material;
    private MeshRenderer meshRenderer;
    private float sizeX;
    
    private static final String RES_LOC = "bones/MEDIA";

    private Transform transform;

    /**
     * Constructor of the actual object.
     * @param transform the transform of the data.
     */
    public Bones(Transform transform) {
        if (mesh == null) {
            mesh = new Mesh();

            float sizeY = 0.5f;
            sizeX = (float) ((double) sizeY / (0.67857142857142857142857142857143 * 4.0));

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
			material = new Material(ResourceLoader.loadTexture(RES_LOC), new Vector3f(1,1,1));
        }

        this.transform = transform;
        this.meshRenderer = new MeshRenderer(mesh, this.transform, material);
    }

    /**
     * Updates the object every single frame.
     */
    public void update() {
        Vector3f playerDistance = transform.getPosition().sub(Transform.getCamera().getPos());

        Vector3f orientation = playerDistance.normalized();
        @SuppressWarnings("unused")
		float distance = playerDistance.length();

        float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));

        if (orientation.getX() > 0) {
            angle = 180 + angle;
        }

        transform.setRotation(0, angle + 90, 0);
        transform.setScale(1.7f, 0.5f, 1);

    }

    /**
     * Method that renders the object's mesh.
     * @param shader to render
     */
    public void render(Shader shader) {
    	meshRenderer.render(shader);
    }

    /**
	 * Gets the object's actual transformation.
	 * @return the object's transform data.
	 */
    public Transform getTransform() {
        return transform;
    }
	
    /**
     * Returns the object's size depending on the object's own width,
     * all of this in a Vector2f.
     * @return vector with the size.
     */
    public Vector2f getSize() {
        return new Vector2f(sizeX, sizeX);
    }
    
}
