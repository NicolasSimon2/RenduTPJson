package com.example.simon.json_parsingnicolas;

import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName(); // pour afficher les erreurs
    private ListView listView;

    // création d'un arraylist prenant en argument un Hashmap, contiendra les différentes information
    ArrayList<HashMap<String, String>>  Contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Contact = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);


        new GetContacts().execute();
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler Http = new HttpHandler();
            // prise en compte de l'url
            String url = "http://api.androidhive.info/contacts/";
            String jsonStr = Http.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // creation d'un Json
                    JSONArray contacts = jsonObj.getJSONArray("contacts");

                    //Loop pour récupérer les informations
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");
                        String address = c.getString("address");
                        String gender = c.getString("gender");

                        // objet phone
                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");

                        // Hash map pour un contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        //Ajout de l'information pour clef -> valeur
                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("address", address);
                        contact.put("gender", gender);
                        contact.put("mobile", mobile);
                        contact.put("home", home);
                        contact.put("office", office);

                        // Ajout des contact à l'array List
                        Contact.add(contact);

                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, Contact,
                    R.layout.list_item, new String[]{ "name", "email","mobile", "address", "gender", "home", "office"},
                    new int[]{R.id.Nom, R.id.Email, R.id.Mobile, R.id.Address, R.id.Gender, R.id.Home, R.id.Office});
            listView.setAdapter(adapter);

        }
    }

}
