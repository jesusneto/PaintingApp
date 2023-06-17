package com.example.paintingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import static com.example.paintingapp.MainActivity.paint_brush;
import static com.example.paintingapp.MainActivity.path;
import static com.example.paintingapp.MainActivity.shape;

public class Display extends View {
    public static ArrayList<Path> pathList = new ArrayList<>();
    //public static ArrayList<Point> pointList = new ArrayList<>();
    public static ArrayList<Rectangle> rectList = new ArrayList<>();
    public static ArrayList<Rectangle> ovalList = new ArrayList<>();
    public static ArrayList<Integer> colorListLine = new ArrayList<>();
    public static ArrayList<Integer> colorListRect = new ArrayList<>();
    public static ArrayList<Integer> colorListOval = new ArrayList<>();
    public ViewGroup.LayoutParams params;
    public static int current_brush = Color.BLACK;

    // mBitmap e mCanvas são usados para permitir salvar arquivos
    public static Bitmap mBitmap;
    private Canvas mCanvas;
    //////////////////////////////////////////////////////////////

    private float x1;
    private float y1;

    public static Point shapePoint;
    private Rectangle r;

    public Display(Context context) {
        super(context);
        init(context);
    }

    public Display(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Display(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint_brush.setAntiAlias(true);
        paint_brush.setColor(Color.BLACK);
        paint_brush.setStyle(Paint.Style.STROKE);
        paint_brush.setStrokeCap(Paint.Cap.ROUND);
        paint_brush.setStrokeJoin(Paint.Join.ROUND);
        paint_brush.setStrokeWidth(10f);

        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    // Cria o bitmap para permitir salvar arquivos
    public void createBitmap() {

        mBitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        mBitmap.eraseColor(Color.WHITE); // salvar o arquivo PNG com fundo branco
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(shape != Shape.LINE) {
                    x1 = x;
                    y1 = y;
                    shapePoint = new Point(x, y);
                    r = new Rectangle(x, y);

                    if(shape == Shape.RECTANGLE)
                        colorListRect.add(current_brush);
                    else if(shape == Shape.OVAL)
                        colorListOval.add(current_brush);
                }

                else if(shape == Shape.LINE) {
                    path.moveTo(x, y);
                }
                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                if((shape == Shape.RECTANGLE || shape == Shape.OVAL) && shapePoint != null) {
                    shapePoint.setX(x);
                    shapePoint.setY(y);
                }

                else if(shape == Shape.LINE) {
                    path.lineTo(x, y);
                    pathList.add(path);
                    colorListLine.add(current_brush);
                }


                invalidate();
                return true;

            case MotionEvent.ACTION_UP:

                if(shape == Shape.RECTANGLE) {
                    r.setRight(x);
                    r.setBottom(y);
                    rectList.add(r);
                    //colorListRect.add(current_brush);
                }

                else if(shape == Shape.OVAL) {
                    r.setRight(x);
                    r.setBottom(y);
                    ovalList.add(r);
                }

                else if(shape == Shape.LINE) {
                    colorListLine.add(current_brush);
                }

                invalidate();
                return true;

            default:
                return true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //if(p == null) return;

        mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);

        paint_brush.setStyle(Paint.Style.STROKE);
        //paint_brush.setColor(Color.WHITE);
        //paint_brush.setStrokeWidth(10f);

        mBitmap.eraseColor(Color.WHITE); // salvar o arquivo PNG com fundo branco

        for (int i = 0; i < pathList.size(); i++) {
            paint_brush.setColor(colorListLine.get(i));
            mCanvas.drawPath(pathList.get(i), paint_brush);
        }

        for (int i = 0; i < rectList.size(); i++) {
            paint_brush.setColor(colorListRect.get(i));
            mCanvas.drawRect(rectList.get(i).getLeft(), rectList.get(i).getTop(), rectList.get(i).getRight(), rectList.get(i).getBottom(), paint_brush);
        }

        for (int i = 0; i < ovalList.size(); i++) {
            paint_brush.setColor(colorListOval.get(i));
            mCanvas.drawOval(ovalList.get(i).getLeft(), ovalList.get(i).getTop(), ovalList.get(i).getRight(), ovalList.get(i).getBottom(), paint_brush);
        } // ultima cor do paint_brush

        if(shape == Shape.RECTANGLE && shapePoint != null){
            paint_brush.setColor(colorListRect.get(colorListRect.size() - 1));
            mCanvas.drawRect(x1, y1, shapePoint.getX(), shapePoint.getY(), paint_brush);
        }

        if(shape == Shape.OVAL && shapePoint != null){
            mCanvas.drawOval(x1, y1, shapePoint.getX(), shapePoint.getY(), paint_brush);
        }

        //mCanvas.drawRect(20, 15, p.getX()+ 10, p.getY() + 15, paint_brush);
        canvas.drawBitmap(mBitmap, 0, 0, new Paint(Paint.DITHER_FLAG));
        invalidate();
    }

    /* VERSÃO ANTERIOR DO MÉTODO onTouchEvent
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1 = x;
                    y1 = y;
                    p = new Point(x, y);
                    r = new Rectangle(x, y);
                    path.moveTo(x, y);
                    pointList.add(new Point(x, y));
                    invalidate();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    p.setX(x);
                    p.setY(y);
                    path.lineTo(x, y);
                    pathList.add(path);
                    colorList.add(current_brush);

                    invalidate();
                    return true;
                case MotionEvent.ACTION_UP:
                    colorList.add(current_brush);
                    pointList.add(new Point(x, y));
                    r.setRight(x);
                    r.setBottom(y);
                    rectList.add(r);
                    invalidate();
                    return true;
                default:
                    return true;
            }
        }
     */

    /* VERSÃO ANTERIOR DO MÉTODO 3
    protected void onDraw(Canvas canvas) {
        if(p == null) return;

        paint_brush.setColor(Color.BLACK);
        // mCanvas.drawPath(pathList.get(i), paint_brush);
        //mCanvas.drawRect(x1, y1, p.getX(), p.getY(), paint_brush);

        mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);

        paint_brush.setStyle(Paint.Style.STROKE);
        //paint_brush.setColor(Color.WHITE);
        //paint_brush.setStrokeWidth(10f);

        mBitmap.eraseColor(Color.WHITE); // salvar o arquivo PNG com fundo branco

        for(int i = 0; i < rectList.size(); i++) {
            mCanvas.drawRect(rectList.get(i).getLeft(), rectList.get(i).getTop(), rectList.get(i).getRight(), rectList.get(i).getBottom(), paint_brush);
        }

        mCanvas.drawRect(x1, y1, p.getX(), p.getY(), paint_brush);
        //mCanvas.drawRect(20, 15, p.getX()+ 10, p.getY() + 15, paint_brush);
        canvas.drawBitmap(mBitmap, 0, 0, new Paint(Paint.DITHER_FLAG));
        invalidate();
    } */

    /* VERSÃO ANTERIOR DO MÉTODO 2
    protected void onDraw(Canvas canvas) {
        for(int i = 0; i < pathList.size(); i++) {
            paint_brush.setColor(colorList.get(i));
            // mCanvas.drawPath(pathList.get(i), paint_brush);
            mCanvas.drawRect(x1, y1, pointList.get(pointList.size() - 1).getX(), pointList.get(pointList.size() - 1).getY(), paint_brush);
            canvas.drawBitmap(mBitmap, 0, 0, new Paint(Paint.DITHER_FLAG));
            invalidate();
        }

    } */

    /* VERSÃO ANTERIOR DO MÉTODO 1
    protected void onDraw(Canvas canvas) {
        for(int i = 0; i < pathList.size(); i++) {
            paint_brush.setColor(colorList.get(i));
            canvas.drawPath(pathList.get(i), paint_brush);
            //canvas.drawBitmap(mBitmap, 0, 0, new Paint(Paint.DITHER_FLAG));
            invalidate();
        }
    } */
}
