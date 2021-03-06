/*
 * Copyright 2018 Carlos Rodriguez.
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
package engine.components;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import engine.menu.system.SEngineUtil;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.2
 * @since 2018
 */
public class Constants {
	
	public static String 	TEXTURE_FILTER;
	public static String 	GAME_GRAPHICS;
	public static float 	MIPMAP_LEVEL;
	public static float 	ANISOTROPIC_LEVEL;
	public static float 	POP_IN;
	public static float 	PARTICLES_POP_IN;
	public static float 	LIGHT_POP_IN;
	public static float 	GRASS_POP_IN;
	public static float 	GRAVITY;
	public static int 		EFFECTS_AUDIO_LEVEL;
	public static int 		MUSIC_AUDIO_LEVEL;
	public static short 	PARTICLES_LEVEL;
	public static boolean	CLEAR_LIGHTS;
	public static boolean	GOD;
	
	/**
	 * Loads all the config language and structures all the components.
	 * @param filePath Path of the text file.
	 */
	public static void load(String filePath) {
		//Reset GameObject	
		BufferedReader fileBuffer = null;
		@SuppressWarnings("unused")
		String line, temporaryName, temporaryTreatment, treatment[];
		boolean fileComplete = false;
		
		try {
			fileBuffer = new BufferedReader(new FileReader(filePath));
			//Interpretation
			while (!fileComplete) {
				if((line = fileBuffer.readLine()) != null) {
					if (line != null && !line.isEmpty()) { //Verify if line contain at least a character
						  if(!(line.charAt(0) == ' ' || line.charAt(0) == '#')) {
						    treatment = SEngineUtil.getInstance().splitString(line, ' ');
						    switch(treatment[0]) {
						    	case "TEXTURE_FILTER":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		TEXTURE_FILTER = treatment[0];				    		
						    		break;
						    	case "GAME_GRAPHICS":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		GAME_GRAPHICS = treatment[0];	
						    		break;
						    	case "CLEAR_LIGHTS":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		CLEAR_LIGHTS = Boolean.parseBoolean(treatment[0]);
						    		break;
						    	case "MIPMAP_LEVEL":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		MIPMAP_LEVEL = Float.parseFloat(treatment[0]);
						    		break;
						    	case "ANISOTROPIC_LEVEL":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		ANISOTROPIC_LEVEL = Float.parseFloat(treatment[0]);
						    		break;
						    	case "POP_IN":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		POP_IN = Float.parseFloat(treatment[0]);
						    		break;
						    	case "PARTICLES_POP_IN":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		PARTICLES_POP_IN = Float.parseFloat(treatment[0]);
						    		break;
						    	case "LIGHT_POP_IN":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		LIGHT_POP_IN = Float.parseFloat(treatment[0]);
						    		break;
						    	case "GRASS_POP_IN":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		GRASS_POP_IN = Float.parseFloat(treatment[0]);
						    		break;
						    	case "GRAVITY":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		GRAVITY = Float.parseFloat(treatment[0]);
						    		break;
						    	case "PARTICLES_LEVEL":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		PARTICLES_LEVEL = Short.parseShort(treatment[0]);
						    		break;
						    	case "EFFECTS_AUDIO_LEVEL":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		EFFECTS_AUDIO_LEVEL = Short.parseShort(treatment[0]) - 20;
						    		if(EFFECTS_AUDIO_LEVEL == 0)
						    			EFFECTS_AUDIO_LEVEL = 0;
						    		else if(EFFECTS_AUDIO_LEVEL == 100)
						    			EFFECTS_AUDIO_LEVEL = 100;
						    		break;
						    	case "GOD":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		GOD = Boolean.parseBoolean(treatment[0]);
						    		break;
						    	}
						    }
						}
				}
				else
					fileComplete = true;
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
