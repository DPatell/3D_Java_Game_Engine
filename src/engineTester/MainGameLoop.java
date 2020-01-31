package engineTester;

import Terrain.Terrain;
import entities.Light;
import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.*;
import shaders.StaticShader;
import textures.ModelTexture;
import entities.Camera;
import entities.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();

		RawModel model = OBJLoader.loadOBJModel("dragon", loader);
		
		TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("blue")));
		ModelTexture texture = staticModel.getTexture();
		texture.setShineDamper(10);
		texture.setReflectivity(1);
		
		Entity entity = new Entity(staticModel, new Vector3f(0,0,-25),0,0,0,3);
		Light light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1));

		Terrain terrain = new Terrain(0, 0, loader, new ModelTexture(loader.loadTexture("grass")));
		
		Camera camera = new Camera();
		MasterRenderer renderer = new MasterRenderer();
		while(!Display.isCloseRequested()){
			entity.increaseRotation(0, 0.25f, 0);
			camera.move();
			renderer.processTerrains(terrain);
			renderer.processEntity(entity);
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}