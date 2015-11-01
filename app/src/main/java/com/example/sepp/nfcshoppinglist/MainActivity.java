package com.example.sepp.nfcshoppinglist;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends ActionBarActivity {


    public static File INTERNAL_STORAGE_DIRECTORY;
    public static String JSON_FILENAME = "config.json";
    public static ArrayList<ShoppingListItem> ITEM_LIST = new ArrayList<ShoppingListItem>();

    private GridView gridview;
    private ArrayList<String> listOfItems;
    private StableArrayAdapter adapter;
    private String itemString;

    public static void saveInternal(String filename, String data) throws IOException {
        saveInternal(filename, data.getBytes());
    }

    public static void saveInternal(String filename, byte[] data) throws IOException {
        File file = new File(INTERNAL_STORAGE_DIRECTORY, filename);
        if (!file.exists())
            file.createNewFile();
        FileOutputStream foss = new FileOutputStream(file);
        foss.write(data);
        foss.flush();
        foss.close();
    }

    public static String loadStringFileInternal(String filename) throws IOException {
        String resultString = "";
        File file = new File(INTERNAL_STORAGE_DIRECTORY, filename);
        if (!file.exists()) {
            return "";
        }

        FileInputStream stream = new FileInputStream(file);

        int _char;
        while ((_char = stream.read()) != -1) {
            resultString += (char) _char;
        }
        stream.close();

        return resultString;
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        INTERNAL_STORAGE_DIRECTORY = getFilesDir();

        setTitle("Shopping list");
        ITEM_LIST = new ArrayList<ShoppingListItem>();

        listOfItems = new ArrayList<String>();

        gridview = (GridView)findViewById(R.id.listOfItems);
        adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, listOfItems);
        gridview.setAdapter(adapter);

        try{
            JSONArray arr = parseJSONFile();
            if(arr != null){
                for (int i = 0; i < arr.length(); i++){
                    ITEM_LIST.add(ShoppingListItem.fromJSONObject((JSONObject) arr.get(i)));
                }
                for (ShoppingListItem i : ITEM_LIST){
                    listOfItems.add(i.getItem());
                    listOfItems.add(Integer.toString(i.getAmount()));
                }
            }
            adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1,
                    listOfItems);
            gridview.setAdapter(adapter);
        } catch (IOException | JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void addToJSONFile(ShoppingListItem item) throws Exception {
        JSONObject jsonObject = item.toJSONObject();
        JSONArray arr = MainActivity.parseJSONFile();
        if (arr == null) {
            arr = new JSONArray();
            arr.put(jsonObject);
        }
        arr.put(jsonObject);
        saveInternal(JSON_FILENAME, arr.toString());
    }

    public static JSONArray parseJSONFile() throws IOException, JSONException {
        String jsonString = loadStringFileInternal(JSON_FILENAME);
        if (jsonString.equals("")) return null;
        JSONTokener tokener = new JSONTokener(jsonString);
        return new JSONArray(tokener);
    }

    public static ShoppingListItem getShoppingListItemByName(String itemName)
            throws JSONException, IOException {
        JSONArray arr = MainActivity.parseJSONFile();
        if(arr!=null) {
            for (int i = 0; i < arr.length(); i++) {
                ShoppingListItem item = ShoppingListItem.fromJSONObject(arr.getJSONObject(i));
                if (item.getItem().equals(itemName))
                    return item;
            }
        }

        return null;
    }

    public static void removeItemByName(String itemName) throws JSONException, IOException {
        JSONArray arr = MainActivity.parseJSONFile();
        JSONArray resultArr = new JSONArray();
        if(arr == null) return;
        for (int i = 0; i < arr.length(); i++) {
            ShoppingListItem item = ShoppingListItem.fromJSONObject((JSONObject)arr.get(i));
            if(item.getItem().equals(itemName)) {
                ShoppingListItem temp = null;
                for(int j = 0; j < ITEM_LIST.size(); j++) {
                    ShoppingListItem iitem = ITEM_LIST.get(j);
                    if(item.getItem().equals(iitem.getItem())) {
                        temp = iitem;
                    }
                }
                if(temp != null) ITEM_LIST.remove(temp);
            }
            else {
                resultArr.put(arr.get(i));
            }
        }
        saveInternal(JSON_FILENAME, resultArr.toString());
    }
}
