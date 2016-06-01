package com.kent.widget.floatheart.opengl;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


class GLRenderer implements Renderer {
    // Geometric variables
    private short indices[];

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private FloatBuffer uvBuffer;

    // Our screenresolution
    private float mScreenWidth = 1280;
    private float mScreenHeight = 768;

    // Misc
    private Context mContext;
    private long mLastTime;
    private ConcurrentHashMap<Long, RunAnimation> mQueueList = new ConcurrentHashMap<>();

    private float heartWidth = 66;
    private float heartHeight = 57;

    public GLRenderer(Context c) {
        mContext = c;
        mLastTime = System.currentTimeMillis() + 100;

        heartWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 33, Resources.getSystem().getDisplayMetrics());
        heartHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28.5f, Resources.getSystem().getDisplayMetrics());
    }

    public void clear(){
        mLastTime = System.currentTimeMillis();
        mQueueList.clear();
    }

    public void setWidthAndHeight(float screenWidth, float screenHeight) {
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
    }

    public void addHeart(int index) {
        long key = SystemClock.elapsedRealtime();
        RunAnimation animation = new RunAnimation(key, index);
        mQueueList.put(key, animation);
    }

    public int getQueueCount() {
        return mQueueList.size();
    }

//    public void onPause() {
//        /* Do stuff to pause the renderer */
//    }

    public void onResume() {
        /* Do stuff to resume the renderer */
        mLastTime = System.currentTimeMillis();
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        // Get the current time
        long now = System.currentTimeMillis();
        // We should make sure we are valid and sane
        if (mLastTime > now) return;

        // clear Screen and Depth Buffer, we have set the clear color as black.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        ///////////////////////////////////////////////////////////
        long time = SystemClock.elapsedRealtime();

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT_FACE);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);


        Iterator<Map.Entry<Long, RunAnimation>> iter = mQueueList.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Long, RunAnimation> entry = iter.next();
            if (entry.getValue().isEnd(time)) {
                iter.remove();
            } else {
                RunAnimation animation = entry.getValue();
                render(animation, time);
            }
        }

        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisable(GLES20.GL_CULL_FACE);

        mLastTime = now;
    }

    private void render(RunAnimation animation, long time) {
        float[] m = animation.play(time);

        // get handle to vertex shader's vPosition member
        int mPositionHandle = GLES20.glGetAttribLocation(RiGraphicTools.sp_Image, "vPosition");

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);

        // Get handle to texture coordinates location
        int mTexCoordLoc = GLES20.glGetAttribLocation(RiGraphicTools.sp_Image, "a_texCoord");

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(mTexCoordLoc);

        // Prepare the texturecoordinates
        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, uvBuffer);

        //// alpha
        // Get handle to texture coordinates location
        int mTextAlpha = GLES20.glGetUniformLocation(RiGraphicTools.sp_Image, "a_text_alpha");
        GLES20.glUniform1f(mTextAlpha, animation.alpha);
        //// alpha

        // Get handle to shape's transformation matrix
        int mtrxhandle = GLES20.glGetUniformLocation(RiGraphicTools.sp_Image, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);

        // Get handle to textures locations
        int mSamplerLoc = GLES20.glGetUniformLocation(RiGraphicTools.sp_Image, "s_texture");

        // Set the sampler texture unit to 0, where we have saved the texture.
        GLES20.glUniform1i(mSamplerLoc, animation.index);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // We need to know the current width and height.
        setWidthAndHeight(width, height);

        // Redo the Viewport, making it fullscreen.
        GLES20.glViewport(0, 0, (int) mScreenWidth, (int) mScreenHeight);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        GLES20.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
//        GLES20.glEnable(GLES20.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal
//        GLES20.glDepthFunc(GLES20.GL_LEQUAL);    // The type of depth testing to do
//        GLES20.glDisable(GLES20.GL_DITHER);      // Disable dithering for better performance
//        GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        SetupShape();
        // Create the image information
        SetupImage();

        // Create the shaders, solid color
        int vertexShader = RiGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, RiGraphicTools.vs_SolidColor);
        int fragmentShader = RiGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, RiGraphicTools.fs_SolidColor);

        RiGraphicTools.sp_SolidColor = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(RiGraphicTools.sp_SolidColor, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(RiGraphicTools.sp_SolidColor, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(RiGraphicTools.sp_SolidColor);                  // creates OpenGL ES program executables

        // Create the shaders, images
        vertexShader = RiGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, RiGraphicTools.vs_Image);
        fragmentShader = RiGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, RiGraphicTools.fs_Image);

        RiGraphicTools.sp_Image = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(RiGraphicTools.sp_Image, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(RiGraphicTools.sp_Image, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(RiGraphicTools.sp_Image);                  // creates OpenGL ES program executables

        // Set our shader programm
        GLES20.glUseProgram(RiGraphicTools.sp_Image);
    }

    private void SetupImage() {
        // Create our UV coordinates.
        float uvs[] = new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };

        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);


        int[] textureIDs = new int[GLFloatHeartView.HEART_RES_IDS.length];
        GLES20.glGenTextures(GLFloatHeartView.HEART_RES_IDS.length, textureIDs, 0); // Generate texture-ID array for 8 IDs
        // Generate OpenGL texture images
        for (int i = 0; i < GLFloatHeartView.HEART_RES_IDS.length; i++) {
            // Build Texture from loaded bitmap for the currently-bind texture ID
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), GLFloatHeartView.HEART_RES_IDS[i]);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIDs[i]);
            GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, GLES20.GL_TRUE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }
    }

    public void SetupShape() {
        // We have to create the vertices of our shape.
        float vertices[] = new float[]
                {0.0f,  heartHeight, 0.0f,
                        0.0f, 0f, 0.0f,
                        heartWidth, 0f, 0.0f,
                        heartWidth, heartHeight, 0.0f,
                };

        indices = new short[]{0, 1, 2, 0, 2, 3}; // The order of vertexrendering.

        // The vertex buffer.
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);
    }

    private static final boolean SCALE_ENABLE = true;
    private static final boolean ROTATE_ENABLE = true;
    private static final int ANIM_ZOOM_IN_DURATION = 150;
    private static final int ANIM_ZOOM_OUT_DURATION = 300;
    private static final float HEART_ROTATE_RANGE = 20;
    private static final float HEART_SCALES[] = {0.86f, 0.88f, 0.90f, 0.92f, 0.94f, 0.95f, 0.95f, 0.97f, 0.98f, 0.99f, 1.00f, 1.01f, 1.02f, 1.03f, 1.04f, 1.05f, 1.07f, 1.09f, 1.10f};
    private static final int HEART_DURATION[] = {4500, 4600, 4700, 4800, 4900, 5000, 5100, 5200, 5300, 5400, 5500, 5600, 5700, 6800, 6900, 6000};
    private final Random mRandom = new Random();
    private final Interpolator mTranslateInterpolator = new DecelerateInterpolator(0.55f);
    private final Interpolator mZoomInInterpolator = new AccelerateInterpolator(0.55f);
    private final Interpolator mZoomOutInterpolator = new DecelerateInterpolator(0.55f);
    private final Interpolator mAlphaInterpolator = new AccelerateInterpolator(0.8f);
    /**
     * 曲线高度个数分割
     */
    private static final int POINT_COUNT = 3;
    /**
     * 曲度
     */
    private static final float RATIO = 0.2f;

    private class RunAnimation {
        private final Path path;
        private final float rotate;
        private final float scale;
        private final PathMeasure pathMeasure;
        private final List<CPoint> points;

        private final float pathLength;

        long startTime;
        private final float duration;
        boolean isEnd;
        int index;
        float alpha = 1f;

        public RunAnimation(long begin, int index) {
            this.startTime = begin;
            this.index = index;

            rotate = mRandom.nextFloat() * HEART_ROTATE_RANGE - HEART_ROTATE_RANGE / 2;
            scale = SCALE_ENABLE ? HEART_SCALES[mRandom.nextInt(HEART_SCALES.length)] : 1.0f;

            duration = (HEART_DURATION[mRandom.nextInt(HEART_DURATION.length)]);

            CPoint start = new CPoint(mScreenWidth / 2.0f, mScreenHeight);
            points = getPoints(start);
            path = builderPath(points);
            pathMeasure = new PathMeasure(path, false);
            pathLength = pathMeasure.getLength();
        }

        public boolean isEnd(long time) {
            return isEnd || time > (startTime + duration);
        }

        public float[] play(long time) {
            float[] mtrxProjection = new float[16];
            float[] mtrxView = new float[16];
            float[] mtrxProjectionAndView = new float[16];
            for (int i = 0; i < 16; i++) {
                mtrxProjection[i] = 0.0f;
                mtrxView[i] = 0.0f;
                mtrxProjectionAndView[i] = 0.0f;
            }

            // Setup our screen width and height for normal sprite translation.
            Matrix.orthoM(mtrxProjection, 0
                    , 0f, mScreenWidth,
                    0.0f, mScreenHeight,
                    0, 50);

            // Set the camera position (View matrix)
            Matrix.setLookAtM(mtrxView, 0,
                    0f, 0f, 1f,
                    0f, 0f, 0f,
                    0f, 1.0f, 0.0f);

            final long spend = time - startTime;

            if (spend < ANIM_ZOOM_IN_DURATION) {
                float timePercent = spend * 1.0f / ANIM_ZOOM_IN_DURATION;
                float zoomPercent = (scale + 0.1f) * mZoomInInterpolator.getInterpolation(timePercent);
                float x1 = (mScreenWidth - heartWidth * zoomPercent) / 2.0f;
                Matrix.translateM(mtrxView, 0, x1, 0, 0);
                Matrix.scaleM(mtrxView, 0, zoomPercent, zoomPercent, 1);
//                android.util.Log.e("zhm", "onDrawFrame3  " + " x1:" + x1 + "   y:" +  0+ "  zoomPercent:" + zoomPercent);
            } else if (spend < (ANIM_ZOOM_IN_DURATION + ANIM_ZOOM_OUT_DURATION)) {
                float interpolatedTime = getInterpolatedTime(time, ANIM_ZOOM_IN_DURATION);
                float[] pos = getPoint(interpolatedTime);

                float timePercent = (spend - ANIM_ZOOM_IN_DURATION) * 1.0f / ANIM_ZOOM_OUT_DURATION;
                float zoomPercent = scale + (0.1f) * (1 - mZoomOutInterpolator.getInterpolation(timePercent));
                float x1 = (pos[0] - heartWidth * zoomPercent / 2.0f);//(mScreenWidth - heartWidth*zoomPercent) / 2.0f;
                Matrix.scaleM(mtrxView, 0, zoomPercent, zoomPercent, 1);
                Matrix.translateM(mtrxView, 0, x1, pos[1], 0);
//                android.util.Log.e("zhm", "onDrawFrame2  x1:" + x1 + "   Y:" + 0 + "  zoomPercent:" + zoomPercent);
            } else {
                float interpolatedTime = getInterpolatedTime(time, ANIM_ZOOM_IN_DURATION);
                float[] pos = getPoint(interpolatedTime);
                float x = pos[0];
                float y = pos[1];

                Matrix.scaleM(mtrxView, 0, scale, scale, 1);
                Matrix.translateM(mtrxView, 0, (x - heartWidth*scale / 2.0f), y, 0);
                alpha = 1f - mAlphaInterpolator.getInterpolation((spend - ANIM_ZOOM_IN_DURATION) / (duration - ANIM_ZOOM_IN_DURATION));
//                android.util.Log.e("zhm", "onDrawFrame1 " + x + " " + (x - heartWidth / 2.0f) + "   " + y + " rightY:" + y + " alpha:" + alpha);
            }

//        Matrix.setRotateM(mtrxView, 0, angle, 0, 0, 1.0f);
            // Calculate the projection and view transformation
            Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);
            return mtrxProjectionAndView;
        }

        private float getInterpolatedTime(long current, long startOffset) {
            return ((float) (current - (startTime + startOffset))) / (duration);
        }

        private float[] getPoint(float interpolatedTime) {
            float translateTime = mTranslateInterpolator.getInterpolation(interpolatedTime);

            float[] pos = new float[2];
            pathMeasure.getPosTan(pathLength * translateTime, pos, null);
            pos[1] = mScreenHeight - pos[1];
            return pos;
        }

        private List<CPoint> getPoints(CPoint start) {
            List<CPoint> points = new ArrayList<>();
            float w = heartWidth * scale;
            float h = heartHeight * scale;
            for (int i = 0; i < POINT_COUNT; i++) {
                if (i == 0) {
                    points.add(start);
                } else {
                    CPoint tmp = new CPoint(0, 0);
                    tmp.x = mRandom.nextInt((int) (mScreenWidth - w)) + w / 2.0f;
                    float range = 0;
                    float len = (mScreenHeight - h) / (float) (POINT_COUNT - 1);
                    if (i < POINT_COUNT - 1) {
                        range = len * 0.4f;
                    }

                    float dy = mRandom.nextFloat() * range - range / 2.0f;
                    tmp.y = mScreenHeight - len * i + dy;
                    points.add(tmp);
                }
            }
            return points;
        }

        private Path builderPath(List<CPoint> points) {
            Path p = new Path();
            if (points.size() > 1) {
                for (int j = 0; j < points.size(); j++) {
                    CPoint point = points.get(j);
                    if (j == 0) {
                        CPoint next = points.get(j + 1);
                        point.dx = ((next.x - point.x) * RATIO);
                        point.dy = ((next.y - point.y) * RATIO);
                    } else if (j == points.size() - 1) {
                        CPoint prev = points.get(j - 1);
                        point.dx = ((point.x - prev.x) * RATIO);
                        point.dy = ((point.y - prev.y) * RATIO);
                    } else {
                        CPoint next = points.get(j + 1);
                        CPoint prev = points.get(j - 1);
                        point.dx = ((next.x - prev.x) * RATIO);
                        point.dy = ((next.y - prev.y) * RATIO);
                    }

                    // create the cubic-spline path
                    if (j == 0) {
                        p.moveTo(point.x, point.y);
                    } else {
                        CPoint prev = points.get(j - 1);
                        p.cubicTo(prev.x + prev.dx, (prev.y + prev.dy),
                                point.x - point.dx, (point.y - point.dy),
                                point.x, point.y);
                    }
                }
            }
            return p;
        }
    }

    private static class CPoint {
        public float x = 0f;
        public float y = 0f;

        /**
         * startTime-axis distance
         */
        public float dx = 0f;

        /**
         * y-axis distance
         */
        public float dy = 0f;

        public CPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "CPoint:[startTime="+ x +", y="+ y +"]";
        }
    }
}