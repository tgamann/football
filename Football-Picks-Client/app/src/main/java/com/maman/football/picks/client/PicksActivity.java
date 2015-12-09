package com.maman.football.picks.client;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;

import com.maman.football.picks.client.utils.Globals;

import java.util.ArrayList;
import java.util.List;

public class PicksActivity extends Activity {
    private static ArrayList<String> mGameList;
	private GLSurfaceView glSurfaceView;
	private boolean rendererSet = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mGameList = savedInstanceState.getStringArrayList(Globals.GAME_LIST_KEY);
        }

		glSurfaceView = new GLSurfaceView(this);

		// Request an OpenGL ES 2.0 compatible context.
        glSurfaceView.setEGLContextClientVersion(2);            
        
        // add this call if this does not work on the emulator (pg 13 of "OpenGL ES2 for Android")
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        
        final FootballRenderer footballRenderer = new FootballRenderer(this);
        glSurfaceView.setRenderer(footballRenderer);
        rendererSet = true;
        
        glSurfaceView.setOnTouchListener(new OnTouchListener() {
            float previousX, previousY;
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        previousX = event.getX();
                        previousY = event.getY();
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                            	footballRenderer.handleTouchDown();
                            }
                        });
                    }
                    else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        final float deltaX = event.getX() - previousX;
                        final float deltaY = event.getY() - previousY;

                        previousX = event.getX();
                        previousY = event.getY();
                        
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                            	footballRenderer.handleTouchDrag(deltaX, deltaY);
                            }
                        });
                    }
                    return true;
                }
                else {
                    return false;
                }
            }
        });
        
        setContentView(glSurfaceView);
	}

    @Override
    protected void onStart() {
        super.onStart();
        // note: onStart() is called before onResume();
        Intent intent = this.getIntent();
        mGameList = intent.getStringArrayListExtra(Globals.GAME_LIST_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        if (rendererSet) {
            glSurfaceView.onResume();
        }        
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onSaveInstanceState (@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(Globals.GAME_LIST_KEY, mGameList);
    }

    static public int getNumGames() {
        return mGameList.size();
    }
    static public List<String> getGameList() {
        return mGameList;
    }
}
