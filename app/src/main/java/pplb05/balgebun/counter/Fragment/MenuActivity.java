package pplb05.balgebun.counter.Fragment;

//https://www.youtube.com/watch?v=ZEEYYvVwJGY

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pplb05.balgebun.R;
import pplb05.balgebun.counter.Adapter.PesananPenjualAdapter;
import pplb05.balgebun.counter.Entity.PesananPenjual;

//import com.example.febriyolaanastasia.balgebun.R;

public class MenuActivity extends Fragment {
    private ArrayList<PesananPenjual> order;
    private PesananPenjualAdapter pesananAdapter;
    private RequestQueue queue;
    private String username;
    Spinner spinnerku;
    ArrayAdapter<CharSequence> adapterSpinner;
    public MenuActivity() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_pesanan, container, false);
        super.onCreate(savedInstanceState);
        Log.d("apakah loading 25", "load");

        SharedPreferences settings = getActivity().getSharedPreferences("BalgebunLogin", Context.MODE_PRIVATE);
        username = settings.getString("username", "");

        TextView counterUsernameText = (TextView)v.findViewById(R.id.counter_name_id);
        counterUsernameText.setText(username);

        Button refreshButton = (Button)v.findViewById(R.id.refresh_pesanan_penjual);

        order = new ArrayList<>();
        getPesananList();

        Log.d("ukuran",""+order.size());

        pesananAdapter = new PesananPenjualAdapter(order,getActivity(),MenuActivity.this);
        GridView fieldMenu = (GridView)v.findViewById(R.id.menu_field);
        fieldMenu.setAdapter(pesananAdapter);
        //pesananAdapter.setOn

        refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getPesananList();
                Log.d("Refresh", "Refreshing");
            }
        });

        return v;
    }


    public void getPesananList() {
        //order.clear();
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "http://aaa.esy.es/coba_wahid2/getPesananPenjual.php";
        final StringRequest stringChess = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE", "Get Pemesanan Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d("try", "try");
                    if (!error) {
                        order.clear();
                        Log.d("if", "" + error);
                        String temp = jObj.getString("user");
                        JSONArray menuTemp = new JSONArray(temp);
                        Log.d("panjang", "" + menuTemp.length());
                        for (int i = 0; i < menuTemp.length(); i++) {
                            JSONObject jsonMenu = new JSONObject(menuTemp.get(i).toString());
                            if (!jsonMenu.getString("status").equals("selesai")) {
                                order.add(new PesananPenjual(
                                        jsonMenu.getString("nama_menu"),
                                        jsonMenu.getString("username_pembeli"),
                                        Integer.parseInt(jsonMenu.getString("jumlah")),
                                        jsonMenu.getString("status")
                                        , i, Integer.parseInt(jsonMenu.getString("id")))
                                );
                                Log.d("i=", "" + i);
                                Log.d("menu", jsonMenu.getString("nama_menu") + "with id " + jsonMenu.getString("id"));
                            }
                        }
                        Log.d("panj", "" + order.size());
                        pesananAdapter.notifyDataSetChanged();
                        Log.d("DoNotify", "DoNotify");


                        Log.d("ABCD", "ABCD");
                    } else {
                        //kalo database kosong
                        order.clear();
                        pesananAdapter.notifyDataSetChanged();
                        Log.d("A", "A");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
                Log.d("hai", "hai");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                return params;
            }

        };

        queue.add(stringChess);
    }
    }

