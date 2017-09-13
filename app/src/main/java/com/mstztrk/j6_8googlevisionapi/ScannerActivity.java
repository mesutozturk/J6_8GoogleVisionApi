package com.mstztrk.j6_8googlevisionapi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScannerActivity extends AppCompatActivity {
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    SurfaceView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        initComp();
    }

    private void initComp() {
        sv = (SurfaceView) findViewById(R.id.scanner_sv);
        // Tum Barkodları ve QR Okumak Icin Barcode Detector Olustur
        barcodeDetector = new BarcodeDetector
                .Builder(ScannerActivity.this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {

            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    final String barcodeStr = barcodes.valueAt(0).displayValue;
                    int format = barcodes.valueAt(0).format;
                    Log.i("barcode", barcodeStr);
                    if (format == Barcode.QR_CODE && barcodes.valueAt(0).phone != null) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + barcodeStr));

                        if (ActivityCompat.checkSelfPermission(ScannerActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        ScannerActivity.this.startActivity(intent);
                    }

                }
            }
        });
        cameraSource = new CameraSource
                .Builder(ScannerActivity.this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(35.0f)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(680, 480)
                .build();
        sv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(ScannerActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ScannerActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                    return;
                }
                try {
                    cameraSource.start(sv.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });
    }
}
