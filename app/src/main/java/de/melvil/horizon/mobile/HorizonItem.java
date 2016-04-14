package de.melvil.horizon.mobile;

public class HorizonItem {
    private String name;
    private String path;
    private boolean hasAudio;
    private boolean hasText;

    public HorizonItem(String name, String path, boolean hasAudio, boolean hasText){
        this.name = name;
        this.path = path;
        this.hasAudio = hasAudio;
        this.hasText = hasText;
    }

    public String getName(){
        return name;
    }

    public String getPath(){
        return path;
    }

    public boolean hasAudio(){
        return hasAudio;
    }

    public boolean hasText(){
        return hasText;
    }

    public void hasAudio(boolean yn){
        hasAudio = yn;
    }

    public void hasText(boolean yn){
        hasText = yn;
    }
}
