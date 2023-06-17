package com.example.paintingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.paintingapp.Display.colorListLine;
import static com.example.paintingapp.Display.colorListOval;
import static com.example.paintingapp.Display.colorListRect;
import static com.example.paintingapp.Display.current_brush;
import static com.example.paintingapp.Display.ovalList;
import static com.example.paintingapp.Display.pathList;
import static com.example.paintingapp.Display.mBitmap;
import static com.example.paintingapp.Display.rectList;
import static com.example.paintingapp.Display.shapePoint;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.Manifest;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {
    // private int PERMISSION_CODE = 1;
    public static Path path = new Path();
    public static Paint paint_brush = new Paint();

    public static Shape shape = Shape.LINE;

    private static String fileName;
    File pathFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myPaintings");
    private Display display;
    private SeekBar penSize;
    private TextView txtPenSize;
    private int defaultColor;

    private File fileSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String date = formatDate.format(new Date());

        fileName = pathFile + "/" + date + ".png";

        if(!pathFile.exists()) {
            pathFile.mkdirs();
        }

        display = findViewById(R.id.view_display);

        // para obter a largura(width) e a altura(height) do bitmap para então salvar a imagem
        display.post(new Runnable() {
            @Override
            public void run() {
                display.createBitmap();
            }
        });

        txtPenSize = findViewById(R.id.txtPenSize);
        penSize = findViewById(R.id.penSize);

        penSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txtPenSize.setText(i + "/100");
                paint_brush.setStrokeWidth(i);
                penSize.setMax(100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //askPermission();

        defaultColor = ContextCompat.getColor(MainActivity.this, R.color.black);
    }



    private void askPermission() {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        Toast.makeText(MainActivity.this, "GRANTED!!!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                });

/*        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
           ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_CODE);
        }*/
    }

    public void saveImage(View view) throws IOException {
        // File file = new File(fileName);
        fileSaved = new File(fileName);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapData = bos.toByteArray();

        FileOutputStream fos = new FileOutputStream(fileSaved); // antes era chamado de file
        fos.write(bitmapData);
        fos.flush();
        fos.close();

        Toast.makeText(this, "Painting saved!!!", Toast.LENGTH_SHORT).show();
    }

    public void shareImage(View view) throws IOException {
        if(fileSaved == null)
            saveImage(view);

        Uri uri = FileProvider.getUriForFile(this, "com.example.paintingapp", fileSaved);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("image/*");
        startActivity(Intent.createChooser(intent, "Share Image"));
    }

    public void pencil(View view) {
        shape = Shape.LINE;
        paint_brush.setColor(Color.BLACK);
        currentColor(paint_brush.getColor());
    }

    public void rectangle(View view) {
        shape = Shape.RECTANGLE;
        shapePoint = null;
    }

    public void oval(View view) {
        shape = Shape.OVAL;
        shapePoint = null;
    }

    public void eraser(View view) {
        pathList.clear();
        colorListLine.clear();
        colorListRect.clear();
        colorListOval.clear();
        path.reset();
        mBitmap.eraseColor(Color.TRANSPARENT);

        rectList.clear();
        ovalList.clear();
        shapePoint = null;
    }

    public void redColor(View view) {
        paint_brush.setColor(Color.RED);
        currentColor(paint_brush.getColor());
    }

    public void yellowColor(View view) {
        paint_brush.setColor(Color.YELLOW);
        currentColor(paint_brush.getColor());
    }

    public void greenColor(View view) {
        paint_brush.setColor(Color.GREEN);
        currentColor(paint_brush.getColor());
    }

    public void magentaColor(View view) {
        paint_brush.setColor(Color.MAGENTA);
        currentColor(paint_brush.getColor());
    }

    public void blueColor(View view) {
        paint_brush.setColor(Color.BLUE);
        currentColor(paint_brush.getColor());
    }

    public void colorPicker(View view) {
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color;
                paint_brush.setColor(color);
                currentColor(paint_brush.getColor());
            }
        });

        ambilWarnaDialog.show();
    }

    public void currentColor(int c) {
        current_brush = c;
        path = new Path();
    }



}