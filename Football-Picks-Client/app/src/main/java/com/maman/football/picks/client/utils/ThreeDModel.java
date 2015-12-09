package com.maman.football.picks.client.utils;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;

import java.io.DataInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Vector;

import android.content.Context;
import android.util.Log;
import com.maman.football.picks.client.R;

public class ThreeDModel {

    private final ShaderProgram shaderProgram;

    // ordered vertex values
	private FloatBuffer vertexBuffer;
	private FloatBuffer uvBuffer;
	private FloatBuffer normalBuffer;
	private ShortBuffer faceBuffer;

	private int numVertices = 0;
	private int numFaces = 0;
	private final int BYTES_PER_FLOAT = 4;
	private final int BYTES_PER_SHORT = 2;

    private final int uMVPmatrixLocation;
    private final int uMVmatrixLocation; // uniform mat4
    private final int uLightPosLocation; // uniform vec3
    
    private final int aPositionLocation;
    private final int aNormalLocation;
    private final int mTextureCoordinateHandle;
    
    private final int mTextureUniformHandle;
    
	
    public ThreeDModel(Context context) {
        shaderProgram = new ShaderProgram(context);
        shaderProgram.buildShaderProgram(R.raw.vertex_shader_for_football, R.raw.fragment_shader_for_football);

        // Retrieve uniform locations for the shader program
        int program = shaderProgram.getProgram();
        uMVPmatrixLocation = glGetUniformLocation(program, "u_MVPMatrix");
        uMVmatrixLocation  = glGetUniformLocation(program, "u_MVMatrix");
        uLightPosLocation  = glGetUniformLocation(program, "u_LightPos");
        mTextureUniformHandle = glGetUniformLocation(program, "u_Texture");
        
        // Retrieve attribute locations for the shader program
        aPositionLocation = glGetAttribLocation(program, "a_Position");
        aNormalLocation = glGetAttribLocation(program, "a_Normal");
        mTextureCoordinateHandle = glGetAttribLocation(program, "a_TexCoordinate");
    }

    public int getProgram() {
    	return shaderProgram.getProgram();
    }

    public void setUniforms(float[] modelViewMatrix, float[] modelViewProjectionMatrix) {
    	glUniformMatrix4fv(uMVmatrixLocation, 1, false, modelViewMatrix, 0);
    	glUniformMatrix4fv(uMVPmatrixLocation, 1, false, modelViewProjectionMatrix, 0);
    }
    public void setLightPosition(float[] lightPosInEyeSpace) {
        glUniform3f(uLightPosLocation, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2]);
    }

    public void bindData() {
		final int COORDS_PER_VERTEX = 3; // number of floats used to define each vertex
		final int STRIDE = COORDS_PER_VERTEX * BYTES_PER_FLOAT;

		glVertexAttribPointer(aPositionLocation, COORDS_PER_VERTEX, GL_FLOAT, false, STRIDE, vertexBuffer);
		glEnableVertexAttribArray(aPositionLocation);     
        
		glVertexAttribPointer(aNormalLocation, COORDS_PER_VERTEX, GL_FLOAT, false, STRIDE, normalBuffer);
		glEnableVertexAttribArray(aNormalLocation);

	    final int COORDS_PER_TEXEL = 2; // Size of the texture coordinate data in elements.
		glVertexAttribPointer(mTextureCoordinateHandle, COORDS_PER_TEXEL, GL_FLOAT, false, COORDS_PER_TEXEL * BYTES_PER_FLOAT, uvBuffer);
		glEnableVertexAttribArray(mTextureCoordinateHandle);
    }
    
    public void bindTexture(int textureDataHandle) {
    	glBindTexture(GL_TEXTURE_2D,textureDataHandle);
    	// Tell the texture uniform sampler to use this texture in the shader.
    	glUniform1i(mTextureUniformHandle, textureDataHandle-1);
    }

    public void draw() {
    	glDrawArrays(GL_TRIANGLES, 0, numVertices);
    }
    
	
	public void createModelFromDataFile(InputStream inputStream) {
        DataInputStream dis = new DataInputStream(inputStream);

        try {
			numVertices = dis.readInt();
			int numUVs = dis.readInt();
			int numNormals = dis.readInt();
			numFaces = dis.readInt();
	        
			// each index in the vertexIndicies vector refers to a x,y,z triplet of floats
			ByteBuffer vbyteBuf = ByteBuffer.allocateDirect(numVertices * BYTES_PER_FLOAT * 3);
			vbyteBuf.order(ByteOrder.nativeOrder());
			vertexBuffer = vbyteBuf.asFloatBuffer();
			for(int i = 0; i < numVertices * 3; i++) {
				float f = dis.readFloat();
				vertexBuffer.put(f);
			}
			vertexBuffer.position(0);

			ByteBuffer uvbyteBuf = ByteBuffer.allocateDirect(numUVs * BYTES_PER_FLOAT * 2);
			uvbyteBuf.order(ByteOrder.nativeOrder());
			uvBuffer = uvbyteBuf.asFloatBuffer();
			for(int i = 0; i < numUVs * 2; i++) {
				float f = dis.readFloat();
				uvBuffer.put(f);
			}
			uvBuffer.position(0);

			ByteBuffer normalBuf = ByteBuffer.allocateDirect(numNormals * BYTES_PER_FLOAT * 3);
			normalBuf.order(ByteOrder.nativeOrder());
			normalBuffer = normalBuf.asFloatBuffer();
			for(int i = 0; i < numNormals * 3; i++) {
				float f = dis.readFloat();
				normalBuffer.put(f);
			}
			normalBuffer.position(0);

			ByteBuffer fBuf = ByteBuffer.allocateDirect(numFaces * BYTES_PER_SHORT);
			fBuf.order(ByteOrder.nativeOrder());
			faceBuffer = fBuf.asShortBuffer();
			for(int i = 0; i < numFaces; i++) {
				Short s = dis.readShort();
				faceBuffer.put(s);
			}
			faceBuffer.position(0);
        }
        catch (Exception e) {
        	String err = e.getMessage();
        	Log.e("ThreeDModel", err);
        }
	}

	// The following routines are used in conjunction with parsing in a Wavfront.obj file
	// via class WavefrontObjParser. FIXME - they can be removed from this app. 
	public void buildVertexBuffer(Vector<Short> vertexIndices, Vector<Float> vertices) {
		numVertices = vertexIndices.size();
		// each index in the vertexIndicies vector refers to a x,y,z triplet of floats
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertexIndices.size() * BYTES_PER_FLOAT * 3);
		byteBuf.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuf.asFloatBuffer();
		for(int i=0; i < numVertices; i++) {
			// get the next index
			int index = vertexIndices.get(i);
			// adjust index value as follows: first subtract 1 because Blender data is 1-based,
			// then multiply by 3 because each index refers to a triplet of floats...
			// index 1 refers to vertex[0],vertex[1],vertex[2], index 1 refers to vertex[3],vertex[4],vertex[5], etc.
			index = (index-1) * 3;
			float x=vertices.get(index);
			float y=vertices.get(index+1);
			float z=vertices.get(index+2);
			vertexBuffer.put(x);
			vertexBuffer.put(y);
			vertexBuffer.put(z);
		}
		vertexBuffer.position(0);
	}

	public void buildUVBuffer(Vector<Short> uvIndices, Vector<Float> uvs) {
		// each index in the uvIndices vector refers to a u,v pair of floats
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(uvIndices.size() * BYTES_PER_FLOAT * 2);
		byteBuf.order(ByteOrder.nativeOrder());
		uvBuffer = byteBuf.asFloatBuffer();
		for(int i=0; i < uvIndices.size(); i++) {
			// get the next index
			int index = uvIndices.get(i);
			// adjust index value as follows: first subtract 1 because Blender data is 1-based,
			// then multiply by 2 because each index refers to a pair of floats...
			// index 0 refers to uv[0],uv[1], index 1 refers to uv[2],uv[3], index 2 refers to uv[4],uv[5], etc.
			index = (index-1) * 2;
			float u=uvs.get(index);
			float v=uvs.get(index+1);
			uvBuffer.put(u);
			uvBuffer.put(-v);
		}
		uvBuffer.position(0);
	}
	
	public void buildNormalBuffer(Vector<Short> normalIndices, Vector<Float> normals) {
		// each index in the normalIndicies vector refers to a x,y,z triplet of floats
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(normalIndices.size() * BYTES_PER_FLOAT * 3);
		byteBuf.order(ByteOrder.nativeOrder());
		normalBuffer = byteBuf.asFloatBuffer();
		for(int i=0; i < normalIndices.size(); i++) {
			// get the next index
			int index = normalIndices.get(i);
			// adjust index value as follows: first subtract 1 because Blender data is 1-based,
			// then multiply by 3 because each index refers to a triplet of floats...
			// index 1 refers to normal[0],normal[1],normal[2], index 1 refers to normal[3],normal[4],normal[5], etc.
			index = (index-1) * 3;
			float x=normals.get(index);
			float y=normals.get(index+1);
			float z=normals.get(index+2);
			normalBuffer.put(x);
			normalBuffer.put(y);
			normalBuffer.put(z);
		}
		normalBuffer.position(0);
	}

	public void buildFaceBuffer(Vector<Short> faces) {
		numFaces = faces.size();
		ByteBuffer fBuf = ByteBuffer.allocateDirect(numFaces * BYTES_PER_SHORT);
		fBuf.order(ByteOrder.nativeOrder());
		faceBuffer = fBuf.asShortBuffer();
		faceBuffer.put(toPrimitiveArrayS(faces));
		faceBuffer.position(0);
	}

	private short[] toPrimitiveArrayS(Vector<Short> vector){
		short[] s;
		s=new short[vector.size()];
		for (int i=0; i<vector.size(); i++){
			s[i]=vector.get(i);
		}
		return s;
	}



}
