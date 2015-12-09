package com.maman.football.picks.client.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.content.Context;
import static android.opengl.GLES20.*;
import com.maman.football.picks.client.R;

public class Skybox {
    private final ShaderProgram shaderProgram;
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;
    private final int attributeLocation;
    private final FloatBuffer floatBuffer;
    private final ByteBuffer indexArray;

    public Skybox(Context context) {
        final int BYTES_PER_FLOAT = 4;

        // Create a unit cube
        float[] vertexData = new float[] {
            -1,  1,  1, // (0) Top-left near
             1,  1,  1, // (1) Top-right near
            -1, -1,  1, // (2) Bottom-left near
             1, -1,  1, // (3) Bottom-right near
            -1,  1, -1, // (4) Top-left far
             1,  1, -1, // (5) Top-right far
            -1, -1, -1, // (6) Bottom-left far
             1, -1, -1, // (7) Bottom-right far
        };
        
        floatBuffer = ByteBuffer.allocateDirect(vertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        
        indexArray = ByteBuffer.allocateDirect(6 * 6)
            .put(new byte[] {
                // Front
                1, 3, 0, // top-right near, bottom-right near, top-left near
                0, 3, 2, // top-left near,  bottom-right near, bottom-left near
                
                // Back
                4, 6, 5,
                5, 6, 7,
                
                //Left
                0, 2, 4,
                4, 2, 6,
                
                // Right
                5, 7, 1,
                1, 7, 3,
                
                // Top
                5, 1, 4,
                4, 1, 0,
                
                // Bottom
                6, 2, 7,
                7, 2, 3
            });
        indexArray.position(0);
        
        shaderProgram = new ShaderProgram(context);
        shaderProgram.buildShaderProgram(R.raw.vertex_shader_for_skybox, R.raw.fragment_shader_for_skybox);

        // Retrieve uniform locations for the shader program
        int program = shaderProgram.getProgram();
        uMatrixLocation = glGetUniformLocation(program, "u_Matrix");
        uTextureUnitLocation =  glGetUniformLocation(program, "u_TextureUnit");
        // Retrieve attribute location for the shader program
        attributeLocation = glGetAttribLocation(shaderProgram.getProgram(), "a_Position");
    }
    
    public int getProgram() {
    	return shaderProgram.getProgram();
    }
    
    public void setUniforms(float[] matrix, int textureId) {        
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId);
        glUniform1i(uTextureUnitLocation, 0);
    }

    public void bindData() {
        final int POSITION_COMPONENT_COUNT = 3;

        floatBuffer.position(0);
        glVertexAttribPointer(attributeLocation,
        		POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, floatBuffer);
        glEnableVertexAttribArray(attributeLocation);
        floatBuffer.position(0);
    }
    
    public void draw() {
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, indexArray);
    }

}
