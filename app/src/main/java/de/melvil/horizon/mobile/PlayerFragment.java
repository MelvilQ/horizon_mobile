package de.melvil.horizon.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class PlayerFragment extends Fragment {

    private Button playPauseButton;
    private Button plus5Button;
    private Button minus5Button;
    private Button nextButton;
    private Button prevButton;
    private Button showTextButton;

    private HorizonActivity parent;

    public PlayerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        parent = (HorizonActivity) activity;
    }

    @Override
    public void onStart(){
        super.onStart();

        playPauseButton = (Button) getActivity().findViewById(R.id.buttonPlay);
        plus5Button = (Button) getActivity().findViewById(R.id.buttonPlus5);
        minus5Button = (Button) getActivity().findViewById(R.id.buttonMinus5);
        nextButton = (Button) getActivity().findViewById(R.id.buttonNext);
        prevButton = (Button) getActivity().findViewById(R.id.buttonPrev);
        showTextButton = (Button) getActivity().findViewById(R.id.buttonText);

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.notifyPlayPause();
                if(parent.isPlaying()) {
                    playPauseButton.setText("❚❚");
                } else {
                    playPauseButton.setText("▶");
                }
            }
        });
        plus5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.notifyJumpForward();
            }
        });
        minus5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.notifyJumpBackward();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.notifyNextFile(parent.isPlaying());
            }
        });
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.notifyPrevFile(parent.isPlaying());
            }
        });
        showTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.notifyShowHideText();
            }
        });

        updateControls(false, false, false);
    }

    public void updateControls(boolean enablePrevNext, boolean enableAudio, boolean enableText){
        playPauseButton.setEnabled(enableAudio);
        plus5Button.setEnabled(enableAudio);
        minus5Button.setEnabled(enableAudio);
        nextButton.setEnabled(enablePrevNext);
        prevButton.setEnabled(enablePrevNext);
        showTextButton.setEnabled(enableText);
    }
}
