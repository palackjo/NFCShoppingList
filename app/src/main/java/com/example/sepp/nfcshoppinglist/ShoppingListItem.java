package com.example.sepp.nfcshoppinglist;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sepp on 28.07.2015.
 */
public class ShoppingListItem {

    private String item;
    private int amount;
    private boolean bought;


    public String getItem() {
        return this.item;
    }

    public int getAmount() {
        return this.amount;
    }

    public boolean getBought() {
        return this.bought;
    }


    public ShoppingListItem(String item, int amount, boolean bought){
        this.item   = item;
        this.amount = amount;
        this.bought = bought;
    }

    public static ShoppingListItem fromJSONObject (JSONObject jsonObject) throws JSONException{
        String item     = (String) jsonObject.get("item");
        int amount      = (int) jsonObject.get("amount");
        boolean bought  = (Boolean) jsonObject.get("bought");

        return new ShoppingListItem(item, amount, bought);
    }

    public JSONObject toJSONObject() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("item", this.item);
        jsonObject.put("amount", this.amount);
        jsonObject.put("bought", this.bought);
        return jsonObject;
    }

}
