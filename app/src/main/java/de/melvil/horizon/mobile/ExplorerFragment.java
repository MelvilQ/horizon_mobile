package de.melvil.horizon.mobile;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExplorerFragment extends Fragment {

    private HorizonActivity parent;

    private String langPath;

    private List<HorizonItem> folderItems;
    private int currentFolderIndex = -1;
    private int selectedFolderIndex = -1;
    private List<HorizonItem> fileItemsOfSelectedFolder;
    private int selectedFileIndex = -1;

    private ExplorerAdapter explorerAdapter;

    public ExplorerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explorer, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parent = (HorizonActivity) activity;
    }

    @Override
    public void onStart() {
        super.onStart();
        String lang = "fr";
        langPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Horizon/" + lang;
        listFolders(langPath);
    }

    public List<HorizonItem> getFolders(String langPath){
        List<HorizonItem> items = new ArrayList<>();
        File langDir = new File(langPath);
        File[] genres = langDir.listFiles();
        Arrays.sort(genres);
        for (File genre : genres) {
            if (!genre.isDirectory())
                continue;
            File[] folders = genre.listFiles();
            Arrays.sort(folders);
            for (File folder : folders) {
                if (!folder.isDirectory() || folder.listFiles().length == 0)
                    continue;
                items.add(new HorizonItem(genre.getName() + "/" + folder.getName(), langPath, false, false));
            }
        }
        return items;
    }

    public List<HorizonItem> getFiles(String folder){
        File folderDir = new File(langPath + "/" + folder);
        Map<String, HorizonItem> items = new LinkedHashMap<>();
        File[] files = folderDir.listFiles();
        Arrays.sort(files);
        for (File file : files) {
            String name = file.getName();
            if (name.endsWith(".mp3")) {
                name = name.replace(".mp3", "");
                if (items.containsKey(name))
                    items.get(name).hasAudio(true);
                else
                    items.put(name, new HorizonItem(name, folderDir.getAbsolutePath(), true, false));
            } else if (name.endsWith(".txt")) {
                name = name.replace(".txt", "");
                if (items.containsKey(name))
                    items.get(name).hasText(true);
                else
                    items.put(name, new HorizonItem(name, folderDir.getAbsolutePath(), false, true));
            }
        }
        List<HorizonItem> itemsList = new ArrayList<>(items.values());
        return itemsList;
    }

    public void listFolders(String langPath) {
        folderItems = getFolders(langPath);
        currentFolderIndex = -1;
        ListView listView = (ListView) getView().findViewById(R.id.listView);
        explorerAdapter = new ExplorerAdapter(
                getActivity().getApplicationContext(), R.layout.list_item_explorer, folderItems);
        listView.setAdapter(explorerAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                HorizonItem item = (HorizonItem) adapter.getItemAtPosition(position);
                listFiles(item.getName());
                currentFolderIndex = position;
            }
        });
    }

    public void listFiles(String folder) {
        List<HorizonItem> itemsList = getFiles(folder);
        itemsList.add(0, new HorizonItem("← Back", "", false, false));
        ListView listView = (ListView) getActivity().findViewById(R.id.listView);
        explorerAdapter = new ExplorerAdapter(
                getActivity().getApplicationContext(), R.layout.list_item_explorer, itemsList);
        listView.setAdapter(explorerAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                HorizonItem item = (HorizonItem) adapter.getItemAtPosition(position);
                if (item.getName().equals("← Back")) {
                    listFolders(langPath);
                    return;
                }
                parent.notifySelectionChange(item);
                selectedFileIndex = position - 1;
                selectedFolderIndex = currentFolderIndex;
                fileItemsOfSelectedFolder = getFiles(
                        folderItems.get(selectedFolderIndex).getName());
                explorerAdapter.notifyDataSetChanged();
            }
        });
    }

    public void selectNextItem(boolean audioOnly){
        selectedFileIndex += 1;
        if(selectedFileIndex >= fileItemsOfSelectedFolder.size()){
            selectedFolderIndex += 1;
            if(selectedFolderIndex >= folderItems.size()){
                selectedFolderIndex = 0;
            }
            fileItemsOfSelectedFolder = getFiles(folderItems.get(selectedFolderIndex).getName());
            selectedFileIndex = 0;
        }
        if(audioOnly && !fileItemsOfSelectedFolder.get(selectedFileIndex).hasAudio())
            selectNextItem(true);
        parent.notifySelectionChange(fileItemsOfSelectedFolder.get(selectedFileIndex));
        explorerAdapter.notifyDataSetChanged();
    }

    public void selectPrevItem(boolean audioOnly){
        selectedFileIndex -= 1;
        if(selectedFileIndex < 0){
            selectedFolderIndex -= 1;
            if(selectedFolderIndex < 0){
                selectedFolderIndex = folderItems.size() - 1;
            }
            fileItemsOfSelectedFolder = getFiles(folderItems.get(selectedFolderIndex).getName());
            selectedFileIndex = fileItemsOfSelectedFolder.size() - 1;
        }
        if(audioOnly && !fileItemsOfSelectedFolder.get(selectedFileIndex).hasAudio())
            selectPrevItem(true);
        parent.notifySelectionChange(fileItemsOfSelectedFolder.get(selectedFileIndex));
        explorerAdapter.notifyDataSetChanged();
    }

    private class ExplorerAdapter extends ArrayAdapter<HorizonItem> {

        public ExplorerAdapter(Context context, int resource, List<HorizonItem> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_explorer, null);
            }
            HorizonItem item = getItem(position);
            TextView dirText = (TextView) view.findViewById(R.id.dirText);
            dirText.setText(item.getName());
            TextView audioYesNo = (TextView) view.findViewById(R.id.audioYesNo);
            audioYesNo.setText((item.hasAudio()) ? "Au" : "");
            TextView textYesNo = (TextView) view.findViewById(R.id.textYesNo);
            textYesNo.setText((item.hasText()) ? "Tx" : "");
            if((position - 1 == selectedFileIndex && currentFolderIndex == selectedFolderIndex && selectedFileIndex != -1)
                    || position == selectedFolderIndex && currentFolderIndex == -1){
                view.setBackgroundColor(Color.rgb(127, 255, 212));
                dirText.setTypeface(dirText.getTypeface(), Typeface.BOLD);
            } else {
                view.setBackgroundColor(Color.WHITE);
                dirText.setTypeface(Typeface.create(dirText.getTypeface(), Typeface.NORMAL));
            }
            return view;
        }
    }
}
