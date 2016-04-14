package de.melvil.horizon.mobile;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import java.io.File;

public class AudioService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private final IBinder audioBinder = new AudioBinder();
    private HorizonActivity parent;

    private File currentAudioFile;
    private boolean isPaused = false;

    public AudioService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    public void setHorizonActivity(HorizonActivity a){
        parent = a;
    }

    public void setCurrentAudioFile(String fileName) {
        currentAudioFile = new File(fileName);
        try {
            mediaPlayer.reset();
            isPaused = false;
            mediaPlayer.setDataSource(getApplicationContext(), Uri.fromFile(currentAudioFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (isPaused) {
            mediaPlayer.start();
        } else {
            mediaPlayer.prepareAsync();
        }
    }

    public void pause() {
        mediaPlayer.pause();
        isPaused = true;
    }

    public void jumpForward(int seconds) {
        int newPosition = mediaPlayer.getCurrentPosition() + seconds * 1000;
        if (newPosition > mediaPlayer.getDuration())
            newPosition = mediaPlayer.getDuration() - 1;
        mediaPlayer.seekTo(newPosition);
    }

    public void jumpBackward(int seconds) {
        int newPosition = mediaPlayer.getCurrentPosition() - seconds * 1000;
        if (newPosition < 0)
            newPosition = 0;
        mediaPlayer.seekTo(newPosition);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return audioBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        parent.notifyNextFile(true);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public class AudioBinder extends Binder {
        AudioService getService() {
            return AudioService.this;
        }
    }
}
