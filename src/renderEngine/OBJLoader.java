package renderEngine;

import models.RawModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class OBJLoader {

    public static RawModel loadOBJModel(String fileName, Loader loader) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(new File("res/"+fileName+".obj"));
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
            e.printStackTrace();
        }

        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        List<Vector3f> verticies = new ArrayList<Vector3f>();
        List<Vector2f> textures = new ArrayList<Vector2f>();
        List<Vector3f> normalVectors = new ArrayList<Vector3f>();
        List<Integer> indicies = new ArrayList<Integer>();
        float[] verticiesArray = null;
        float[] normalsArray = null;
        float[] texturesArray = null;
        int[] indiciesArray = null;

        try{
            while(true) {
                line = bufferedReader.readLine();
                String[] currentLine = line.split(" ");
                if(line.startsWith("v ")) {
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                    verticies.add(vertex);
                }else if(line.startsWith("vt ")) {
                    Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
                    textures.add(texture);
                }else if(line.startsWith("vn ")) {
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                    normalVectors.add(normal);
                }else if(line.startsWith("f ")) {
                    texturesArray = new float[verticies.size() * 2];
                    normalsArray = new float[verticies.size() * 3];
                    break;
                }
            }

            while(line != null) {
                if(!line.startsWith("f ")) {
                    line = bufferedReader.readLine();
                    continue;
                }
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");

                processVertex(vertex1, indicies, textures, normalVectors, texturesArray, normalsArray);
                processVertex(vertex2, indicies, textures, normalVectors, texturesArray, normalsArray);
                processVertex(vertex3, indicies, textures, normalVectors, texturesArray, normalsArray);
                line = bufferedReader.readLine();
            }

            bufferedReader.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

        verticiesArray = new float[verticies.size() * 3];
        indiciesArray = new int[indicies.size()];

        int vertexPointer = 0;
        for(Vector3f vertex: verticies) {
            verticiesArray[vertexPointer++] = vertex.x;
            verticiesArray[vertexPointer++] = vertex.y;
            verticiesArray[vertexPointer++] = vertex.z;
        }

        for (int i = 0; i < indicies.size(); i++) {
            indiciesArray[i] = indicies.get(i);
        }

        return  loader.loadToVAO(verticiesArray, texturesArray, normalsArray, indiciesArray);
    }

    private static void processVertex(String[] vertexData, List<Integer> indicies, List<Vector2f> textures,
                                      List<Vector3f> normals, float[] textureArray, float[] normalsArray) {
        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indicies.add(currentVertexPointer);
        Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
        textureArray[currentVertexPointer * 2] = currentTex.x;
        textureArray[currentVertexPointer * 2 + 1] = 1 - currentTex.y;
        Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
        normalsArray[currentVertexPointer * 3] = currentNorm.x;
        normalsArray[currentVertexPointer * 3 + 1] = currentNorm.y;
        normalsArray[currentVertexPointer * 3 + 2] = currentNorm.z;

    }
}
