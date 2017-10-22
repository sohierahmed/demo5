package com.example.sohier_ahmed.httprequest;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.Authenticator;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    JSONObject parent;
    ArrayList<HashMap<String, String>> lst;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_HTTPREQUEST=(Button)findViewById(R.id.btn_httpRequest);
        btn_HTTPREQUEST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HTTPREQUEST();
            }});


         parent= new JSONObject();
        Button btn_encode=(Button)findViewById(R.id.btn_encode);
        btn_encode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                  DBHelper dbHelper;
                    dbHelper = new DBHelper(getApplicationContext());
                    dbHelper.CreateDB();

                    dbHelper.OpenDB();
                     final Cursor cursor=  dbHelper.GetSheetNames();

                try {
                    parent=EncodeCursorToJSON(cursor);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String json=parent.toString();
                    TextView txt_json = (TextView)findViewById(R.id.txt_json);
                    txt_json.setText(json);


        }});


        Button btn_decode=(Button)findViewById(R.id.btn_decode);
        btn_decode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                   
                    lst=new ArrayList<>();
                    String json=parent.toString();

                    Decode(json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void HTTPREQUEST()
    {
        final TextView mTextView = (TextView) findViewById(R.id.txt_response);
        Authenticator.setDefault(new ProxyAuthenticator("sohier_ahmed", "PASS_word"));
        System.setProperty("http.proxyHost", "172.16.30.2");
        System.setProperty("http.proxyPort", "8080");
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://www.almasryalyoum.com/";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        mTextView.setText("Response is: "+ response.substring(0,1000));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError ) {
                    Toast.makeText(getApplicationContext(),
                            "timeout",
                            Toast.LENGTH_LONG).show();
                    mTextView.setText(  "timeout "+  error.getNetworkTimeMs());
                }
                if( error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(),
                            "NoConnectionError",
                            Toast.LENGTH_LONG).show();
                    mTextView.setText(  "NoConnectionError "+  error.getMessage());
                }
                if( error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(),
                            "NoConnectionError",
                            Toast.LENGTH_LONG).show();
                    mTextView.setText(  "ServerError "+  error.getMessage());
                }
            }
        });
     /*   stringRequest.setRetryPolicy(new DefaultRetryPolicy(40000
                ,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void Decode(String jsonStr) {
        if (jsonStr != null) {
            try {
                JSONObject parent = new JSONObject(jsonStr);
             /*   String[] names = parent.names();
                JSONArray jsonArray = jsonObject.toJSONArray(new JSONArray(names));*/
                // Getting JSON Array node
                JSONArray sheets = parent.getJSONArray(parent.names().get(0).toString());

                // looping through All Contacts
                for (int i = 0; i < sheets.length(); i++) {

                    JSONObject object = sheets.getJSONObject(i);
                   // List<String>flds=new ArrayList<>();
                    HashMap<String, String> outlet = new HashMap<>();
                 for (int k=0;k<  object.names().length();k++)
                 {
                  //  flds.add(object.getString(object.names().get(k).toString()));
                     outlet.put(object.names().get(k).toString(),object.getString(object.names().get(k).toString()));
                 }
                   /* String id = object.getString(Contract.sheetEntry._ID);
                    String SHEET_CODE = object.getString(Contract.sheetEntry.COL_SHEET_CODE);
                    String Form_code = object.getString(Contract.sheetEntry.COL_FORM_CODE);
                    String NUMBER_OF_COLLECTING = object.getString(Contract.sheetEntry.COL_NUMBER_OF_COLLECTING);
                    String Form_name = object.getString(Contract.formEntry.COL_FORM_NAME);


                    // tmp hash map for single contact


                    outlet.put(Contract.sheetEntry._ID,id);
                    outlet.put(Contract.sheetEntry.COL_SHEET_CODE,SHEET_CODE);
                    outlet.put(Contract.sheetEntry.COL_FORM_CODE,Form_code);
                    outlet.put(Contract.sheetEntry.COL_NUMBER_OF_COLLECTING,NUMBER_OF_COLLECTING);
                    outlet.put(Contract.formEntry.COL_FORM_NAME,Form_name);*/

                    lst.add(outlet);
                }
               // Toast.makeText(this,"lst.size() "+lst.size(),Toast.LENGTH_LONG).show();
                ListAdapter adapter = new SimpleAdapter(MainActivity.this,lst,R.layout.sheet_layout,
                        new String[]{Contract.sheetEntry.COL_SHEET_CODE,Contract.formEntry.COL_FORM_NAME},
                        new int[]{R.id.txt_sheet_name, R.id.txt_sheet_Disc});
                final ListView lstview =(ListView) findViewById(R.id.lst_sheets);
                lstview.setAdapter(adapter);
            } catch (final JSONException e) {


            }
        }
    }

    private JSONObject EncodeCursorToJSON(Cursor cursor)throws JSONException  {


           /*
      {
        "outlets":[
        {
            "outlet_name":"outletName1",
            "outlet_address":"address1",
            "outlet_tel":"tel1",
            "outlet_mob":"mob1"
        }
        ,
        {
            "outlet_name":"outletName2",
            "outlet_address":"address2",
            "outlet_tel":"tel2",
            "outlet_mob":"mob2"
        }
         ,
        {
            "outlet_name":"outletName3",
            "outlet_address":"address3",
            "outlet_tel":"tel3",
            "outlet_mob":"mob3"
        }
        ]
      }
       */
              /*
      {
        "sheets":[
        { "sheet_name":"sheetName1",
            "sheet_code":"code1",
            "form_code":"form_code1"
        }
        ,
        {
           "sheet_name":"sheetName2",
            "sheet_code":"code2",
            "form_code":"form_code2"
        }
         ,
        {
           "sheet_name":"sheetName3",
            "sheet_code":"code3",
            "form_code":"form_code3"
        }
        ]
      }
       */
        JSONObject parent = new JSONObject();
        JSONArray sheets = new JSONArray();
        cursor.moveToNext();
       do{
         //   int rowsCount = cursor.getCount();
           int columnsCount = cursor.getColumnCount();
           JSONObject sheet_info= new JSONObject();
               for (int j=0 ;j<columnsCount;j++)
               {
                   sheet_info.put(cursor.getColumnName(j),cursor.getString(j));
               }


               sheets.put(sheet_info);
       }while (cursor.moveToNext());
        parent.put("sheets",sheets);
        return parent;
    }
   /* private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }*/
    private void EncodeToJSON() throws JSONException {

    /*  {
        "outlets":[
        {
            "outlet_name":"outletName1",
            "outlet_address":"address1",
            "outlet_tel":"tel1",
            "outlet_mob":"mob1"
        }
        ,
        {
            "outlet_name":"outletName2",
            "outlet_address":"address2",
            "outlet_tel":"tel2",
            "outlet_mob":"mob2"
        }
         ,
        {
            "outlet_name":"outletName3",
            "outlet_address":"address3",
            "outlet_tel":"tel3",
            "outlet_mob":"mob3"
        }
        ]
      }*/

        JSONObject parent = new JSONObject();
        JSONArray outlets = new JSONArray();
        JSONObject outlet_info = new JSONObject();
   /*    *//**//* String outlet_name;*//**//*
       // String outlet_address ="address1";
     *//**//*   String outlet_tel="tel1";
        String outlet_mob="mob1";*//**//**/

        outlet_info.put("outlet_name","outletName1");
        outlet_info.put("outlet_address","address1");
        outlet_info.put("outlet_tel","tel1");
        outlet_info.put("outlet_mob","mob1");
        outlets.put(outlet_info);
        parent.put( "outlets",outlets);
        String json=parent.toString();
        TextView txt_json = (TextView)findViewById(R.id.txt_json);
        txt_json.setText(json);
    }
}
