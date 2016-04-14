package de.melvil.horizon.mobile;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

public class HorizonActivity extends ActionBarActivity {

    private PlayerFragment playerFragment;
    private ExplorerFragment explorerFragment;
    private ReaderFragment readerFragment;

    private AudioService audioService;
    private Intent playIntent;

    private boolean playing;

    private boolean showText = false;
    private String lang = "fr";
    private HorizonItem currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_horizon);
        // get player fragment reference
        playerFragment = (PlayerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_player);
        // begin fragment transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // create explorer fragment and show it
        explorerFragment = new ExplorerFragment();
        ft.add(R.id.frame_fragment, explorerFragment, "explorer");
        ft.show(explorerFragment);
        // create reader fragment and hide it
        readerFragment = new ReaderFragment();
        ft.add(R.id.frame_fragment, readerFragment, "reader");
        ft.hide(readerFragment);
        // commit fragment transaction
        ft.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        playIntent = new Intent(this, AudioService.class);
        bindService(playIntent, audioConnection, Context.BIND_AUTO_CREATE);
        startService(playIntent);
        playing = false;
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        audioService = null;
        super.onDestroy();
    }

    public void exitApp(){
        stopService(playIntent);
        audioService = null;
        System.exit(0);
    }

    public void notifySelectionChange(HorizonItem item) {
        currentItem = item;
        if (item != null) {
            if (item.hasText()) {
                // load new text into reader
                readerFragment.loadText(currentItem.getPath() + "/" + currentItem.getName(), lang);
            } else {
                showText = true;
                notifyShowHideText();
            }
        }
        // update player fragment
        playerFragment.updateControls(item != null, item.hasAudio(), item.hasText());
        // update audio service
        if(audioService != null && item.hasAudio()) {
            audioService.setCurrentAudioFile(item.getPath() + "/" + item.getName() + ".mp3");
            if(playing)
                audioService.play();
        } else {
            playing = false;
        }
    }

    public void notifyShowHideText() {
        showText = !showText;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (showText) {
            // hide explorer fragment and show reader fragment
            ft.hide(explorerFragment);
            ft.show(readerFragment);
        } else {
            // hide reader fragment and show explorer fragment
            ft.hide(readerFragment);
            ft.show(explorerFragment);
        }
        ft.commit();
    }

    public void notifyNextFile(boolean audioOnly) {
        explorerFragment.selectNextItem(audioOnly);
    }

    public void notifyPrevFile(boolean audioOnly) {
        explorerFragment.selectPrevItem(audioOnly);
    }

    public void notifyPlayPause(){
        playing = !playing;
        if(playing)
            audioService.play();
        else
            audioService.pause();
    }

    public void notifyJumpForward(){
        audioService.jumpForward(10);
    }

    public void notifyJumpBackward(){
        audioService.jumpBackward(10);
    }

    public boolean isPlaying(){
        return playing;
    }

    //connect to the service
    private ServiceConnection audioConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.AudioBinder binder = (AudioService.AudioBinder) service;
            audioService = binder.getService();
            audioService.setHorizonActivity(HorizonActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            audioService = null;
        }
    };
}
