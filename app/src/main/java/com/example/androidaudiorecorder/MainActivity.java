package com.example.androidaudiorecorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //Declare Variables
    Button btnRecord1, btnStopRecord1, btnPlay1, btnStop1, btnRecord2, btnStopRecord2, btnPlay2, btnStop2, btnPlayAll;
    String pathSave1 = "";
    String pathSave2 = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    EditText repeatCount;

    final int REQUEST_PERMISSION_CODE = 1000;
    final int RECORDER1 = 1;
    final int RECORDER2 = 2;
    //반복횟수
    int Repnum = 0;
    int cnt = 0;

    // 현재시간을 msec 으로 구한다.
    long now = System.currentTimeMillis();
    // 현재시간을 date 변수에 저장한다.
    Date date = new Date(now);
    // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH_mm_ss");
    // nowDate 변수에 값을 저장한다.
    String formatDate = sdfNow.format(date);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //requset runtime permission
        if (!checkPermissionFromDevice())
            requestPermissions();
        //init VIew
        btnPlay1 = (Button) findViewById(R.id.btnPlay1);
        btnRecord1 = (Button) findViewById(R.id.btnStartRecord1);
        btnStop1 = (Button) findViewById(R.id.btnStop1);
        btnStopRecord1 = (Button) findViewById(R.id.btnStopRecord1);
        btnPlay2 = (Button) findViewById(R.id.btnPlay2);
        btnRecord2 = (Button) findViewById(R.id.btnStartRecord2);
        btnStop2 = (Button) findViewById(R.id.btnStop2);
        btnStopRecord2 = (Button) findViewById(R.id.btnStopRecord2);
        btnPlayAll = (Button) findViewById(R.id.btnPlayAll);
        repeatCount = (EditText) findViewById(R.id.repeatCount);

        //폴더만들기
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "AndroidRecorder");
        // 폴더가 제대로 만들어졌는지 체크 ======
        if (!dir.mkdirs()) {
            Log.e("FILE", "Directory not created");
        } else {
            Toast.makeText(MainActivity.this, "폴더 생성 SUCCESS", Toast.LENGTH_SHORT).show();
        }


        //From Android M , you need to request Run-time permission
        btnRecord1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermissionFromDevice()) {
                    //경로설정
                    pathSave1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "AndroidRecorder/" + "KEY_" + formatDate.replace('/', '_') + ".mp3";
                    setupMediaRecorder(RECORDER1);
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    btnRecord1.setEnabled(false);
                    btnStopRecord1.setEnabled(true);
                    btnPlay1.setEnabled(false);
                    btnStop1.setEnabled(false);

                    Toast.makeText(MainActivity.this, pathSave1, Toast.LENGTH_SHORT).show();
                } else {
                    requestPermissions();
                }

            }
        });

        btnStopRecord1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                btnStopRecord1.setEnabled(false);
                btnPlay1.setEnabled(true);
                btnRecord1.setEnabled(true);
                btnStop1.setEnabled(true);
            }
        });

        btnPlay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnStop1.setEnabled(true);
                btnStopRecord1.setEnabled(false);
                btnRecord1.setEnabled(false);
                // play as many as repeatCount
                String res;
                if (repeatCount.getText().toString().isEmpty())
                    res = "0";
                else
                    res = repeatCount.getText().toString();

                int num = Integer.parseInt(res) - 1;
                if (num <= -1)
                    num = 0;
                Repnum = num;
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(pathSave1);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();

                Toast.makeText(MainActivity.this, "전체 길이 : " + mediaPlayer.getDuration() + " 현재 : " + mediaPlayer.getCurrentPosition(), Toast.LENGTH_LONG).show();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                    }
                });
            }
        });

        btnStop1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStopRecord1.setEnabled(false);
                btnPlay1.setEnabled(true);
                btnRecord1.setEnabled(true);
                btnStop1.setEnabled(false);

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    setupMediaRecorder(RECORDER1);
                }
            }
        });
        /////////////////////////////////////**PRACTICE AREA**///////////////////////////////////////////////////////////////////////////////////

        btnRecord2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermissionFromDevice()) {
                    //경로설정
                    pathSave2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "AndroidRecorder/" + formatDate.replace('/', '_') + ".mp3";
                    setupMediaRecorder(RECORDER2);
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    btnRecord2.setEnabled(false);
                    btnStopRecord2.setEnabled(true);
                    btnPlay2.setEnabled(false);
                    btnStop2.setEnabled(false);

                    Toast.makeText(MainActivity.this, pathSave2, Toast.LENGTH_SHORT).show();
                } else {
                    requestPermissions();
                }

            }
        });

        btnStopRecord2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                btnStopRecord2.setEnabled(false);
                btnPlay2.setEnabled(true);
                btnRecord2.setEnabled(true);
                btnStop2.setEnabled(true);

            }
        });

        btnPlay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnStop2.setEnabled(true);
                btnStopRecord2.setEnabled(false);
                btnRecord2.setEnabled(false);
                // play as many as repeatCount
                String res;
                if (repeatCount.getText().toString().isEmpty())
                    res = "0";
                else
                    res = repeatCount.getText().toString();

                int num = Integer.parseInt(res) - 1;
                if (num <= -1)
                    num = 0;
                Repnum = num;
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(pathSave2);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();

                Toast.makeText(MainActivity.this, "전체 길이 : " + mediaPlayer.getDuration() + " 현재 : " + mediaPlayer.getCurrentPosition(), Toast.LENGTH_LONG).show();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                    }
                });
            }
        });

        btnStop2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStopRecord2.setEnabled(false);
                btnPlay2.setEnabled(true);
                btnRecord2.setEnabled(true);
                btnStop2.setEnabled(false);
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    setupMediaRecorder(RECORDER2);
                }
            }
        });

        btnPlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(pathSave1);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();

                Toast.makeText(MainActivity.this, "전체 길이 : " + mediaPlayer.getDuration() + " 현재 : " + mediaPlayer.getCurrentPosition(), Toast.LENGTH_LONG).show();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(pathSave2);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();

                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                if (mediaPlayer != null) {
                                    mediaPlayer.stop();
                                    mediaPlayer.release();
                                    mediaPlayer = null;
                                }
                            }
                        });
                    }
                });

            }
        });
    }

    private void setupMediaRecorder(int whichRECORDER) {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        if (whichRECORDER == RECORDER1)
            mediaRecorder.setOutputFile(pathSave1);
        else
            mediaRecorder.setOutputFile(pathSave2);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    //press Ctrl+O


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

}

