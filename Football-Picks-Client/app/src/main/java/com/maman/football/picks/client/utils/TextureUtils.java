package com.maman.football.picks.client.utils;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.opengl.GLUtils;
import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.maman.football.picks.client.R;

public class TextureUtils {
    static private Map<String, Integer> textureMap = new HashMap<>();
    static {
        Map<String, Integer> texMap = new HashMap<>();
        texMap.put("49ers", R.drawable._49ers); texMap.put("49ers Away", R.drawable._49ers_away);
        texMap.put("Bears", R.drawable._bears); texMap.put("Bears Away", R.drawable._bears_away);
        texMap.put("Bengals", R.drawable._bengals); texMap.put("Bengals Away", R.drawable._bengals_away);
        texMap.put("Bills", R.drawable._bills); texMap.put("Bills Away", R.drawable._bills_away);
        texMap.put("Broncos", R.drawable._broncos); texMap.put("Broncos Away", R.drawable._broncos_away);
        texMap.put("Browns", R.drawable._browns); texMap.put("Browns Away", R.drawable._browns_away);
        texMap.put("Buccaneers", R.drawable._buccaneers); texMap.put("Buccaneers Away", R.drawable._buccaneers_away);
        texMap.put("Cardinals", R.drawable._cardinals); texMap.put("Cardinals Away", R.drawable._cardinals_away);
        texMap.put("Chargers", R.drawable._chargers); texMap.put("Chargers Away", R.drawable._chargers_away);
        texMap.put("Chiefs", R.drawable._chiefs); texMap.put("Chiefs Away", R.drawable._chiefs_away);
        texMap.put("Colts", R.drawable._colts); texMap.put("Colts Away", R.drawable._colts_away);
        texMap.put("Cowboys", R.drawable._cowboys); texMap.put("Cowboys Away", R.drawable._cowboys_away);
        texMap.put("Dolphins", R.drawable._dolphins); texMap.put("Dolphins Away", R.drawable._dolphins_away);
        texMap.put("Eagles", R.drawable._eagles); texMap.put("Eagles Away", R.drawable._eagles_away);
        texMap.put("Falcons", R.drawable._falcons); texMap.put("Falcons Away", R.drawable._falcons_away);
        texMap.put("Giants", R.drawable._giants); texMap.put("Giants Away", R.drawable._giants_away);
        texMap.put("Jaguars", R.drawable._jaguars); texMap.put("Jaguars Away", R.drawable._jaguars_away);
        texMap.put("Jets", R.drawable._jets); texMap.put("Jets Away", R.drawable._jets_away);
        texMap.put("Lions", R.drawable._lions); texMap.put("Lions Away", R.drawable._lions_away);
        texMap.put("Packers", R.drawable._packers); texMap.put("Packers Away", R.drawable._packers_away);
        texMap.put("Panthers", R.drawable._panthers); texMap.put("Panthers Away", R.drawable._panthers_away);
        texMap.put("Patriots", R.drawable._patriots); texMap.put("Patriots Away", R.drawable._patriots_away);
        texMap.put("Raiders", R.drawable._raiders); texMap.put("Raiders Away", R.drawable._raiders_away);
        texMap.put("Rams", R.drawable._rams); texMap.put("Rams Away", R.drawable._rams_away);
        texMap.put("Ravens", R.drawable._ravens); texMap.put("Ravens Away", R.drawable._ravens_away);
        texMap.put("Redskins", R.drawable._redskins); texMap.put("Redskins Away", R.drawable._redskins_away);
        texMap.put("Saints", R.drawable._saints); texMap.put("Saints Away", R.drawable._saints_away);
        texMap.put("Seahawks", R.drawable._seahawks); texMap.put("Seahawks Away", R.drawable._seahawks_away);
        texMap.put("Steelers", R.drawable._steelers); texMap.put("Steelers Away", R.drawable._steelers_away);
        texMap.put("Texans", R.drawable._texans); texMap.put("Texans Away", R.drawable._texans_away);
        texMap.put("Titans", R.drawable._titans); texMap.put("Titans Away", R.drawable._titans_away);
        texMap.put("Vikings", R.drawable._vikings); texMap.put("Vikings Away", R.drawable._vikings_away);
        textureMap = Collections.unmodifiableMap(texMap);
    }

    static public int[] loadTextures(Context context, List<String> gameList) {

    	final int NUM_TEXTURES = textureMap.size()/2;
        final int[] textureObjectIds = new int[NUM_TEXTURES];

        // generate a texture objects; arguments are n, return value, offset
        glGenTextures(NUM_TEXTURES, textureObjectIds, 0);
        for (int texId : textureObjectIds) {
        	if (texId == 0) {
	            if (Globals.LOGIT) {
	            	Log.w("RENDERER", "Failed to generate a new OpenGL texture object.");
	            }
	            return textureObjectIds;
        	}
        }
        
        // OpenGL can't read jpeg or png files directly because they are encoded
        // in compressed format; OpenGL needs the data in raw uncompressed form.
        // We'll use Android's built-in bitmap decoder to decompress.
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // use original, non-scaled image data
        options.inScaled = false;

        int count = 0;
        for (String game : gameList) {
            // Add teams to the list of matchups such that the winning team is listed first;
            // that way, when we rotate the footballs to the winning side, it's the same
            // rotation for all footballs.
            String[] team = game.split(",");
            final Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(),
                    textureMap.get(team[0]), options);
            final Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(),
                    textureMap.get(team[1]), options);
            Bitmap bitmap = combineImages(bitmap1, bitmap2);

        	if (bitmap == null) {
        		if (Globals.LOGIT) {
        			Log.w("RENDERER", "Resource ID could not be decoded.");
        		}
        		glDeleteTextures(NUM_TEXTURES, textureObjectIds, 0);
	            return textureObjectIds;
        	}

        	// Tell OpenGL that future texture calls should be applied to this texture object
        	glBindTexture(GL_TEXTURE_2D, textureObjectIds[count++]);
        
        	// In case we have more fragments to map to than texels (magnification) or
        	// more texels than fragments to map to (minification)..
        	// we need to specify the filtering to use.
        	// trilinear filtering
        	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        	// load bitmaps directly into OpenGL; copy bitmap data into the texture object
        	// that is currently bound in OpenGL
        	GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        	// don't need the bitmap anymore so release its memory
        	bitmap.recycle();
        
        	// Tell OpenGL to generate all the necessary mipmap levels
        	glGenerateMipmap(GL_TEXTURE_2D);
        
        	// We are finished loading the texture; a good practice is to unbind from the texture so
        	// we don't accidently affect it with other texture calls.
        	glBindTexture(GL_TEXTURE_2D, 0);
        }

        return textureObjectIds;
    }

    public static int loadCubeMap(Context context, int[] cubeResources) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);
        
        if (textureObjectIds[0] == 0) {
    		if (Globals.LOGIT) {
                Log.w("RENDERER", "Could not generate a new OpenGL texture object.");
            }
            return 0;
        }
        
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap[] cubeBitmaps = new Bitmap[6];
        for (int i = 0; i < 6; i++) {
            cubeBitmaps[i] =
                BitmapFactory.decodeResource(context.getResources(), cubeResources[i], options);
            
            if (cubeBitmaps[i] == null) {
        		if (Globals.LOGIT) {
                    Log.w("RENDERER", "Resource ID " + cubeResources[i]
                        + " could not be decoded.");
                }
                glDeleteTextures(1, textureObjectIds, 0);
                return 0;
            }
        }
        
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);
        
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
        GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmaps[0], 0);
        GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmaps[1], 0);
        GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmaps[2], 0);
        GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmaps[3], 0);
        GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmaps[4], 0);
        GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmaps[5], 0);
        
        glBindTexture(GL_TEXTURE_2D, 0);
        
        for (Bitmap bitmap : cubeBitmaps) {
            bitmap.recycle();
        }
        
        return textureObjectIds[0];
    }
    
    static private Bitmap combineImages(Bitmap c, Bitmap s) { 
        int width  = c.getWidth(); 
        int height = c.getHeight(); 

        Bitmap cs = Bitmap.createBitmap(width, height*2, Bitmap.Config.ARGB_8888); 

        Canvas comboImage = new Canvas(cs); 

        // FIXME: why did I call these bitmaps "c" and "s" ?

        Rect srcRect  = new Rect(0, 0, width,height);
        Rect des1Rect = new Rect(0, 0, width, height);
        Rect des2Rect = new Rect(0, height, width, height*2);
        comboImage.drawBitmap(c, srcRect, des1Rect, null);
        comboImage.drawBitmap(s, srcRect, des2Rect, null); 
        
        return cs;
      } 

}
