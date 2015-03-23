package com.zhaoyu.findphone;

import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.IOException;

public class SoundAlarmListenerService extends WearableListenerService {

    private static final String TAG = "ExampleFindPhoneApp";

    private static final String FIELD_ALARM_ON = "alarm_on";

    private AudioManager mAudioManager;
    private static int mOrigVolume;
    private int mMaxVolume;
    private Uri mAlarmSound;
    private MediaPlayer mMediaPlayer;
    private Camera camera;
    private static int count=0;

    @Override
    public void onCreate() {
        super.onCreate();
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mOrigVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        mAlarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public void onDestroy() {
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mOrigVolume, 0);
        mMediaPlayer.release();
        super.onDestroy();
    }

    @Override
    public void onPeerDisconnected(Node peer) {

        count +=1;
        //获取当前音量大小
        mOrigVolume =mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        mMediaPlayer.reset();
        //设置音量大小
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mMaxVolume, 0);
        //指定流媒体的类型
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        try {
            //设置多媒体数据来源
            mMediaPlayer.setDataSource(getApplicationContext(), mAlarmSound);
            //mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
        }catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();

        /*if(count ==1){
            count =0;
            camera = Camera.open();
            Camera.Parameters params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);//开启
            camera.setParameters(params);
            camera.startPreview();
        }*/
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onDataChanged: " + dataEvents + " for " + getPackageName());
        }
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.i(TAG, event + " deleted");
            } else {
                if (event.getType() == DataEvent.TYPE_CHANGED) {
                    Boolean alarmOn =
                            DataMap.fromByteArray(event.getDataItem().getData()).get(FIELD_ALARM_ON);
                    if (alarmOn) {
                        mOrigVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                        mMediaPlayer.reset();
                        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mMaxVolume, 0);
                        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                        try {
                            mMediaPlayer.setDataSource(getApplicationContext(), mAlarmSound);
                            mMediaPlayer.prepare();
                        } catch (IOException e) {
                            Log.e(TAG, "Failed to prepare media player to play alarm.", e);
                        }
                        mMediaPlayer.start();
                    } else {
                        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mOrigVolume, 0);
                        if (mMediaPlayer.isPlaying()) {
                            mMediaPlayer.stop();
                        }
                    }
                    if (alarmOn) {
                        camera = Camera.open();
                        Camera.Parameters params = camera.getParameters();
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);//开启
                        camera.setParameters(params);
                        camera.startPreview();
                    }
                    else{
                        camera.stopPreview(); // 关掉亮灯
                        camera.release(); // 关掉照相机
                    }
                }
            }
        }
        //dataEvents.close();
    }
}
