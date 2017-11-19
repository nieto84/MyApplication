package com.example.a.myapplication;
import android.app.LauncherActivity;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView mTextView=null;
    TextView mTextView2=null;
    String href_users = "";
    Button  registrar;
    EditText password,email,nombre, nickName;
    String url = "http://10.0.2.2:8080/api/v1/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);
        nombre = (EditText) findViewById(R.id.nombre);
        nickName = (EditText) findViewById(R.id.nickName);
        mTextView = (TextView) findViewById(R.id.text);


        registrar = (Button) findViewById(R.id.registrar);

// Instantiate the RequestQueue.
        RequestQueue queue = VolleySingleton.getInstance().getmRequestQueue();



       JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url , null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        // Check the length of our response (to see if the user has any repos)
                        if (response.length() > 0) {
                            // The user does have repos, so let's loop through them all.
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject links = response.getJSONObject("_links");

                                    for (int j = 0; j < links.length(); j++) {
                                        JSONObject users = links.getJSONObject("users");
                                        href_users = users.getString("href");
                                    }}catch (JSONException e) {
                                    // If there is an error then output this to the logs.
                                }}
                            } else {
                            // The user didn't have any repos.
                            Toast toast2 =
                                    Toast.makeText(getApplicationContext(),
                                            "No se han importado datos", Toast.LENGTH_SHORT);
                            toast2.show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our repo list.
                        Toast toast2 =
                                Toast.makeText(getApplicationContext(),
                                        "Error en la conexión", Toast.LENGTH_SHORT);
                        toast2.show();

                        Log.e("Volley", error.toString());
                }
                }
        );



       registrar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

            User usuario = new User();

            usuario.setFullName(nombre.getText().toString());
            usuario.setUserName(nickName.getText().toString());
            usuario.setPassword(password.getText().toString());

               envioDatosServidor(usuario);
           }
       });

// Add the request to the RequestQueue.
        queue.add(request);
    }

    private void envioDatosServidor(final User usuario){

        RequestQueue queue = VolleySingleton.getInstance().getmRequestQueue();
        JsonObjectRequest MyStringRequest = new JsonObjectRequest(Request.Method.POST, href_users, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.

                Toast toast2 =
                        Toast.makeText(getApplicationContext(),
                                "ok", Toast.LENGTH_SHORT);
                toast2.show();
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Toast toast2 =
                        Toast.makeText(getApplicationContext(),
                                "Error", Toast.LENGTH_SHORT);
                toast2.show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("FullName", usuario.getFullName()); //Add the data you'd like to send to the server.
                MyData.put("UserName", usuario.getUserName());
                MyData.put("Password", usuario.getPassword());
                return MyData;
            }
        };

        queue.add(MyStringRequest);

    }


    private void addToRepoList(String name) {
        mTextView.setText(name);
       // mTextView2.setText(course);
    }

    private void listText(final String str) {
        // This is used for setting the text of our repo list box to a specific string.
        // We will use this to write a "No repos found" message if the user doens't have any.
        this.mTextView.setText(str);
    }

}
