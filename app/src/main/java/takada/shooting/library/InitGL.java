package takada.shooting.library;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLUtils;

@SuppressLint("NewApi")
public class InitGL {
	
	public static GL10 gl;
	
	public static void init(GL10 arg){
		
		gl = arg;
	}
	
	public static final FloatBuffer makeFloatBuffer(float[] arr){
		
		ByteBuffer bb  = ByteBuffer.allocateDirect(arr.length*4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}
	
	public static Point setViewPortWithAspectRatio
				(int width, int height, double aspect){
		
		int dstWidth, dstHeight;
		
		if(width < height * aspect){
			
			dstWidth = width;
			dstHeight = (int)(width / aspect);
		}
		else{
			
			dstWidth = (int)(height * aspect);
			dstHeight = height;
		}
		
		int offsetX = (width - dstWidth) / 2;
		int offsetY = (height - dstHeight) / 2;
		
		gl.glViewport(offsetX, offsetY, dstWidth, dstHeight);
		
		return new Point(dstWidth, dstHeight);
	}
	
	public static void setColor(float red, float green, float blue, float alpha){
		
		gl.glColor4f(red, green, blue, alpha);
	}
	
	public static void setColor(int color){
		
		float red   = Color.red(color)   / 255f;
		float green = Color.green(color) / 255f;
		float blue  = Color.blue(color)  / 255f;
		float alpha = Color.alpha(color) / 255f;
		
		setColor(red, green, blue, alpha);
	}
	
	public static void drawLine(PointF start, PointF end){
		
		float[] vertices = {
				start.x,	start.y,
				end.x,		end.y
		};
		
		FloatBuffer polygonVertices = makeFloatBuffer(vertices);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);	
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, polygonVertices);
		
		gl.glDrawArrays(GL10.GL_LINES, 0, 2);
	}
	
	public static void drawRectAngle(RectF rect){
	
		float[] vertices = {
				rect.left,	rect.bottom,
				rect.left,	rect.top,
				rect.right,	rect.bottom,
				rect.right,	rect.top
		};
		
		FloatBuffer polygonVertices = makeFloatBuffer(vertices);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);	
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, polygonVertices);
		
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
	}
	
	public static void drawStrokeCircle
		(PointF center, float radius, int divide){
		
		float[] vertices = getCircleVertices(center, radius, divide);
		FloatBuffer polygonVertices = makeFloatBuffer(vertices);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);	
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, polygonVertices);
		
		gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, divide);
	}
	
	public static void drawFillCircle
		(PointF center, float radius, int divide){
	
	float[] edges = getCircleVertices(center, radius, divide);
	float[] vertices = new float [(divide - 2) * 3 * 2];
	
	for(int i=0; i<divide-2; i++){
		
		vertices[i*6  ]= edges[0];			vertices[i*6+1]= edges[1];
		vertices[i*6+2]= edges[(i+1)*2];	vertices[i*6+3]= edges[(i+1)*2+1];
		vertices[i*6+4]= edges[(i+2)*2];	vertices[i*6+5]= edges[(i+2)*2+1];	
	};
	
	FloatBuffer polygonVertices = makeFloatBuffer(vertices);
	
	gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);	
	gl.glVertexPointer(2, GL10.GL_FLOAT, 0, polygonVertices);
	
	gl.glDrawArrays(GL10.GL_TRIANGLES, 0, (divide-2)*3);
}
	
	private static float[] getCircleVertices
			(PointF center, float radius, int divide){
		
		double dRad = 2 * Math.PI / divide;
		
		float[] result = new float[divide * 2];
		
		for(int i=0; i<divide; i++){
			
			result[i*2  ] = (float)(center.x + radius * Math.cos(i * dRad));
			result[i*2+1] = (float)(center.y + radius * Math.sin(i * dRad));
		}
		return result;
	}
	
	public static final int loadTexture(Resources resources, int resID){
	
		Bitmap bmp = BitmapFactory.decodeResource(resources, resID, options);
		if (bmp == null) return 0;
		
		int texID = setTexture(bmp);
		bmp.recycle();
		
		TextureManager.addTexture(resID, texID);
		
		return texID;
	}
	
	public static final int setTexture(Bitmap bitmap){
		
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		gl.glTexParameterf
			(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf
			(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
		
		return textures[0];
	}
	
	private static final BitmapFactory.Options options = new BitmapFactory.Options();
	static{ 
		options.inScaled = false;	
		options.inPreferredConfig = Config.ARGB_8888;
	}
	
	public static void enableDefaultBlend(){
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static class TextureManager{
		
		private static Map<Integer, Integer> textures
			= new Hashtable<Integer, Integer>();
		
		public static void addTexture(int resID, int texID){
			textures.put(resID, texID);
		}
		
		public static void deleteTexture(int resID){
			int[] texID = new int[1];
			texID[0] = textures.get(resID);
			gl.glDeleteTextures(1, texID, 0);
			textures.remove(resID);
		}
		
		public static void deleteAll(){
			List<Integer> keys = new ArrayList<Integer>(textures.keySet());
			for(Integer key : keys){
				deleteTexture(key);
			}
		}
	}
	
	public static float[] textureSTCoords ={
		0.0f,	0.0f,
		0.0f,	1.0f,
		1.0f,	0.0f,
		1.0f,	1.0f
	};
	
	public static void setTextureSTCoords(RectF rect){
		
		if(rect==null) rect = new RectF(0, 0, 1, 1);
		
		textureSTCoords[0]=rect.left;	textureSTCoords[1]=rect.top;
		textureSTCoords[2]=rect.left;	textureSTCoords[3]=rect.bottom;
		textureSTCoords[4]=rect.right;	textureSTCoords[5]=rect.top;
		textureSTCoords[6]=rect.right;	textureSTCoords[7]=rect.bottom;
	}
	
	public static float[] vertices = new float [8];
	static float vLeft, vRight, vTop, vBottom;
	
	public static void drawTexture(PointF center, PointF size, int texID){
		
		vLeft = center.x - size.x / 2;
		vTop = center.y + size.y / 2;
		vRight = vLeft + size.x;
		vBottom = vTop - size.y;
		
		vertices[0] = vLeft;	vertices[1] = vTop;
		vertices[2] = vLeft;	vertices[3] = vBottom;
		vertices[4] = vRight;	vertices[5] = vTop;
		vertices[6] = vRight;	vertices[7] = vBottom;
		
		FloatBuffer polygonVertices = makeFloatBuffer(vertices);
		FloatBuffer texCoords = makeFloatBuffer(textureSTCoords);
		
		gl.glColor4f(0f, 0f, 0f, 1.0f);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texID);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, polygonVertices);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoords);
		
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
	
	private static class FontStructure{
		
		public int fontSheetTexture;
		public int chrmapXnumber, chrmapYnumber;
		public int asciiOffset;
		public float chrSizeX, chrSizeY;
	}
	
	private static FontStructure font;
	
	public static void setupFont
			(Resources res, int resID, int nx, int ny, int offset){
		
		int texID = loadTexture(res, resID);
		if(texID==0) return;
		
		font = new FontStructure();
		
		font.fontSheetTexture = texID;
		font.chrmapXnumber= nx;
		font.chrmapYnumber = ny;
		font.asciiOffset = offset;
		
		font.chrSizeX = 1.0f  / font.chrmapXnumber;
		font.chrSizeY = 1.0f  / font.chrmapYnumber;
	}
	
	public static void drawText(RectF position, String text){
		
		if(font == null) return;
		
		int textLength = text.length();
		if(textLength==0) return;
		
		float drawChrSizeX = position.width() / textLength;
		float drawChrSizeY = position.height();
		float drawCenterY = position.centerY();
		
		for(int i=0; i<textLength; i++){
			
			int code = text.codePointAt(i) - font.asciiOffset;
			float mapX1 = (code%font.chrmapXnumber)*font.chrSizeX;
			float mapY1 = (code/font.chrmapYnumber)*font.chrSizeY;
			float mapX2 = mapX1 + font.chrSizeX;
			float mapY2 = mapY1 + font.chrSizeY;
			
			setTextureSTCoords(new RectF(mapX1, mapY1, mapX2, mapY2));
			
			float drawCenterX = position.left + drawChrSizeX*(i+0.5f);
			drawTexture(
					new PointF(drawCenterX, drawCenterY),
					new PointF(drawChrSizeX, drawChrSizeY),
					font.fontSheetTexture
			);
		}
	}
	
	static FloatBuffer colorMatrix = InitGL.makeFloatBuffer(new float[4]);
	
	public static void changeTexColor(float[] color){
		
		if(color == null){
			gl.glTexEnvf
			(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
			return;
		}
		
		colorMatrix.put(color);
		colorMatrix.position(0);
		
		gl.glTexEnvfv
			(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_COLOR, colorMatrix);
		gl.glTexEnvf
			(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_BLEND);
	}
}
