package com.maman.football.picks.client;

import java.io.InputStream;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.maman.football.picks.client.utils.BallControl;
import com.maman.football.picks.client.utils.Skybox;
import com.maman.football.picks.client.utils.TextureUtils;
import com.maman.football.picks.client.utils.ThreeDModel;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Pair;

import static android.opengl.GLES20.*;

public class FootballRenderer implements Renderer {

	private final Context context;
	private ThreeDModel football;
    private Skybox skybox;
    private int skyboxTexture;
    private int NUM_GAMES;
    static public final int MAX_NUM_GAMES = 16;
    private int[] ballRotation = new int[MAX_NUM_GAMES];
    private long startTime = 0;
    private final long[] stopTime = new long[MAX_NUM_GAMES];
    private final Random random = new Random();
    
    // Texture
    private int[] mTextureDataHandle; // handle to our texture data
    
    private final float[] modelMatrix = new float[16];
    private final float[] mLightModelMatrix = new float[16];        
    private final float[] viewMatrix = new float[16];
    private final float[] modelViewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    private final float[] mLightPosInWorldSpace = new float[4];
    private final float[] mLightPosInEyeSpace = new float[4];

    private List<Pair<Float,Float>> mBallPositionList;
    private float mBallZPosition = -8f;
    private int mBallSpinRate = 5;
    private int mSpinRateMultiplier = 1;

    // skybox vriables
    private float xRotation, yRotation;


	public FootballRenderer(Context context) {
        this.context = context;
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
       glClearColor(0f, 0.533f, 0.8078f, 1.0f);
       // Use culling to remove back faces.
       glEnable(GL_CULL_FACE);
       // Enable depth testing
//       glEnable(GL_DEPTH_TEST);
       
       football = new ThreeDModel(context);
       InputStream inputStream = this.context.getResources().openRawResource(R.raw.football_data);
       football.createModelFromDataFile(inputStream);
       mTextureDataHandle = TextureUtils.loadTextures(context, PicksActivity.getGameList());


       skybox = new Skybox(context);
       skyboxTexture = TextureUtils.loadCubeMap(context,
               new int[]{R.drawable.left, R.drawable.right,
                       R.drawable.bottom, R.drawable.top,
                       R.drawable.front, R.drawable.back});
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Set the OpenGL view-port to fill the entire surface.
        glViewport(0, 0, width, height);
        
        // The projection matrix helps create the illusion of 3D. It usually only changes whenever
        // the screen changes orientation. Setting near= 1 far= 20 means the frustum goes from -1 to -20.
        //                               FOV        aspect ratio         near  far
        setPerspectiveM(projectionMatrix, 45f, (float)width/(float)height, 1f,  10f);
//        setPerspectiveM(projectionMatrix, 45f, (float)width/(float)height, 1f,  20f);

        // The view matrix is functionally equivalent to a camera; we use it to change our viewpoint.
        // When transformed by the view matrix, each vertex is said to be relative to our eyes or camera.
        // In other words, the view matrix can be said to represent the camera position.
        //                                eye x,y,z   look x,y,z   up x,y,z
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 2f, 0f, 0f, -10f, 0f, 1f, 0f);
        // Our view matrix says, the camera (or eye) is at position 0,0,2 with the top of the camera (or head)
        // in the positive direction of the y axis, and we are looking in the direction 0,0,0. In other words,
        // the camera (or eye) is looking straight down the z-axis going into the screen.

        startTime = System.currentTimeMillis();
        for (int i = 0; i < MAX_NUM_GAMES; i++) {
            ballRotation[i] = 0;
            stopTime[i] = 5000 + random.nextInt(5000);
        }

        NUM_GAMES = PicksActivity.getNumGames();
        mBallPositionList = BallControl.getPositionList(NUM_GAMES);
        mBallZPosition = BallControl.getZposition(NUM_GAMES);
        mBallSpinRate = BallControl.getSpinRate(NUM_GAMES, mSpinRateMultiplier);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
        // clear the rendering surface; important to clear the depth buffer bit 
        glClear(GL_COLOR_BUFFER_BIT); // | GL_DEPTH_BUFFER_BIT);

        //*************************** Drawing the skybox ***************************
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        Matrix.rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        glUseProgram(skybox.getProgram());
        skybox.setUniforms(viewProjectionMatrix, skyboxTexture);
        skybox.bindData();
        skybox.draw();

        long elapsedTime = System.currentTimeMillis() - startTime;

        //************************** Drawing the footballs *************************
        
        // specify the shader programs, then get the location offsets for each matrix input
        glUseProgram(football.getProgram());

        for (int i = 0; i < NUM_GAMES; i++) {
        	// Set the active texture unit.
        	glActiveTexture(GL_TEXTURE0 + i);
        	// Bind the texture to this unit & tell the texture uniform sampler
        	// to use this texture in the shader.
        	football.bindTexture(mTextureDataHandle[i]);
            float xpos = mBallPositionList.get(i).first;
            float ypos = mBallPositionList.get(i).second;
        	float[] ballPosition = {xpos, ypos, mBallZPosition, 1};

	        // The model matrix is used to place objects in world-space. For example, we might
	        // have our tennis football model initially centered at (0,0,0). We can move it by
	        // updating each and every vertex; instead we use a model matrix and transform the
	        // vertices by multiplying them with the model matrix. Here we create a model matrix
	        // to move objects into the distance (i.e. negative z direction).
        	Matrix.setIdentityM(modelMatrix, 0);
        	Matrix.translateM(modelMatrix, 0, ballPosition[0], ballPosition[1], ballPosition[2]);
        	// Rotate our object
            Matrix.rotateM(modelMatrix, 0, 90, 0, 0, 1); // rotate 90 deg around the z-axis
            // now that we've rotated 90 deg, our y-axis is where our x-axis used to be.
            if (elapsedTime < stopTime[i]) {
                // rotate the ball until we reach the stop time.
                ballRotation[i] = (ballRotation[i] + mBallSpinRate) % 360;
            }
            else {
                // complete the rotation until we get to 0.
                if (ballRotation[i] + mBallSpinRate >= 360) {
                    // adjust in case rotation + (n * spin rate) does not divide evenly into 360.
                    ballRotation[i] = 0;
                    mSpinRateMultiplier = 0;
                }
                if (ballRotation[i] != 0){
                    // continue rotation until we get to 0.
                    ballRotation[i] = (ballRotation[i] + mBallSpinRate) % 360;
                }
            }
            Matrix.rotateM(modelMatrix, 0, ballRotation[i], 0, 1, 0); // rotate around the y-axis

        	// The model is at position (0,0,-5), and the camera is pointed straight down the z-axis
        	// in the negative direction. So we are looking point blank at the front of the object.

            Matrix.setIdentityM(viewMatrix, 0);
        	// To combine the model, view, and projection matrices, they must be multiplied
        	// in the following order: (projection * view) * model
        	Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        	Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);

            // Position the light. Note that we need a 4th coordinate so that the translations
            // work when we multiply this by our transformation matrices. Start by positioning
            // the light at the origin in model space.
            final float[] mLightPosInModelSpace = new float[] {0f, 0f, 0f, 1};
            // The directional light covers the football best when the distance from the light to
            // the football is about 3; therefore, we position the light +3 away from the football.
            // -12=black footballs.
            // -8 = some light shining on balls; really looks like some directional light on balls.
            // -6 = nice, shiny refection on footballs.
            Matrix.setIdentityM(mLightModelMatrix, 0);
            Matrix.translateM(mLightModelMatrix, 0, xpos, ypos, mBallZPosition +3);
            // Translate to world space
            Matrix.multiplyMV(mLightPosInWorldSpace,0,mLightModelMatrix,0,mLightPosInModelSpace,0);
            // Translate to eye space
            Matrix.multiplyMV(mLightPosInEyeSpace, 0, viewMatrix, 0, mLightPosInWorldSpace, 0);
            // Pass in the light position in eye space.
            football.setLightPosition(mLightPosInEyeSpace);

        	// Assign the matrix to the shader program variable
        	Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        	football.setUniforms(modelViewMatrix, modelViewProjectionMatrix);
        
            // Bind our object data to the shader program variables. Note that we only need to do
            // this once, as every subsequent football object has the same vertex, normal, and uv points.
            football.bindData();

        	// Now that we've set the shader program vertex, matrix, and texture variables, we're ready to draw.
        	football.draw();
        }
	}

    private void setPerspectiveM(float[] m, float yFovInDegrees, float aspect, float n, float f) {
        final float angleInRadians = (float) (yFovInDegrees * Math.PI / 180.0);
        
        // calculate the focal length
        final float a = (float) (1.0/Math.tan(angleInRadians/2.0));
        
        // fill in the perspective matrix
        m[0] = a / aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;
        
        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;
        
        m[8] = 0f;
        m[9] = 0f;
        m[10] = -((f + n) / (f - n));
        m[11] = -1f;
        
        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2f * f * n) / (f - n));
        m[15] = 0f;
        
    }

    public void handleTouchDrag(float deltaX, float deltaY) {

        xRotation += deltaX /16f;
        yRotation += deltaY /16f;
    }
    public void handleTouchDown() {
        startTime = System.currentTimeMillis();
        mSpinRateMultiplier = (mSpinRateMultiplier % 4) + 1;
        mBallSpinRate = BallControl.getSpinRate(NUM_GAMES, mSpinRateMultiplier);
    }
}
