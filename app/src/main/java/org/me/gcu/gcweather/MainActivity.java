package org.me.gcu.gcweather;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;



import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.widget.ExpandableListView;





public class MainActivity extends AppCompatActivity {

    ListView lvRss;
    ArrayList<String> weatherDescriptions;
    String[] titlesArray;
    TextView description, temp, minmax, listHeader, listHeaderWeather;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        lvRss = (ListView) findViewById(R.id.lvRss);
        description = (TextView) findViewById(R.id.description);
        temp = (TextView) findViewById(R.id.temp);
        minmax = (TextView) findViewById(R.id.minmax);

//        titles = new ArrayList<>();
        weatherDescriptions = new ArrayList<>();
//        description = (TextView) findViewById(R.id.description);



        //Set today's date
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        TextView textDateDisplay = findViewById(R.id.text_date_display);
        textDateDisplay.setText(currentDate);


        ProcessInBackground processInBackground = new ProcessInBackground();
        processInBackground.execute();
        try {
            List<String> result = processInBackground.get();
            prepareListData(result);
            //prepareListData();
           expListView = (ExpandableListView) findViewById(R.id.lvExp);

            listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
//            expListView = new ExpandableListView(this);
            // setting list adapter
//        expListView.setAdapter(listAdapter);
            expListView.setAdapter(listAdapter);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // get the listview

        // preparing list data

//        prepareListData();



    }


    public InputStream getInputStream(URL url){
        try{
            //This open connection returns an instance that represents a connection to the remote
            //object referred to by this url, i.e. returns the the rss feed page
            //the input stream reads from the open connection link

            return url.openConnection().getInputStream();

        }
        catch (IOException e){
            return null;
        }
    }

    public class ProcessInBackground extends AsyncTask<Context, Void, List<String>>{
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        Exception exception = null;
        Context context;
        ArrayList<String> titles;

        //before we go into the background
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            progressDialog.setMessage("Busy loading weather feed...Please wait :)");
//            progressDialog.show();
//        }

        @Override
        protected List<String> doInBackground(Context... params) {
//            context=params[0];
            titles = new ArrayList<>();
            try {
                URL url = new URL("https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/2648579");

                //new instance of xml factory that can be used to create xml pull parsers
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                //not providing support for xml name spaces
                factory.setNamespaceAware(false);

                //helps us to extract the data from the xml document
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(getInputStream(url), "UTF_8");

                boolean insideItem = false;

                int eventType = xpp.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT){
                    if (eventType == XmlPullParser.START_TAG){
                        if (xpp.getName().equalsIgnoreCase("item")){
                            insideItem = true;
                        }
                        else if (xpp.getName().equalsIgnoreCase("title")){
                            if (insideItem){
                                titles.add(xpp.nextText());

                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("description")){
                            if (insideItem){
                                weatherDescriptions.add(xpp.nextText());

                            }
                        }
                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                        insideItem = false;
                    }

                    eventType = xpp.next();
                }

            }
            //if there's something wrong with the URL link:
            catch (MalformedURLException e){
                exception = e;
            }
            catch (XmlPullParserException e){
                exception=e;
            }
            catch (IOException e){
                exception = e;
            }

            return titles;
        }

        @Override
        protected void onPostExecute(List<String> s) {
            super.onPostExecute(s);



            //convert titles array list to an array
            titlesArray = titles.toArray(new String[titles.size()]);

            //convert weatherDescriptions array list to an array
            String[] descriptionsArray = weatherDescriptions.toArray(new String[weatherDescriptions.size()]);

            //Get and set values for current day
            String today = titlesArray[0];
            String[] todayValues = today.split(":|,");
            String temp_min = (todayValues[3].substring(0,5));
//            String temp_max = (todayValues[4]);
//            String max_values = temp_min + " / " + temp_max;

            description.setText(todayValues[1]);
            temp.setText(temp_min);
//            minmax.setText(temp_max);


//            getCallingActivity().context.


            progressDialog.dismiss();
        }
    }



    /*
     * Preparing the list data
     */
    private void prepareListData(List<String> titles) {

        //Get and set values for next day
//        titlesArray = titles.toArray(new String[titles.size()]);
//        System.out.println("LIST OF TITLES");
//        System.out.println(titlesArray[2]);
//        System.out.println("0000000000000000000000000000000000000000000000000000000000000000");
//     String tomorrow = titlesArray[0];
//    String[] tomorrowValues = tomorrow.split(":|,");
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        List<String> today = new ArrayList<>();
        for (String day:
             titles) {
            String[] strings=day.split(":");
            listDataHeader.add(strings[0].trim());
            listDataChild.put(strings[0].trim(), Arrays.asList(strings[1].trim().split(",")));


        }
//        listDataHeader = titles;

        // Adding child data
//        listDataHeader.add(tomorrowValues[0]);
//        listDataHeader.add("Now Showing");
//        listDataHeader.add("Coming Soon..");

        // Adding child data
//        List<String> top250 = new ArrayList<String>();
//        top250.add("The Shawshank Redemption");
//        top250.add("The Godfather");
//        top250.add("The Godfather: Part II");
//        top250.add("Pulp Fiction");

//        List<String> nowShowing = new ArrayList<String>();
//        nowShowing.add("The Conjuring");
//        nowShowing.add("Despicable Me 2");
//        nowShowing.add("Turbo");
//        nowShowing.add("Grown Ups 2");


//        List<String> comingSoon = new ArrayList<String>();
//        comingSoon.add("2 Guns");
//        comingSoon.add("The Smurfs 2");
//        comingSoon.add("The Spectacular Now");
//        comingSoon.add("Thise Canyons");


//        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
//        listDataChild.put(listDataHeader.get(1), nowShowing);
//        listDataChild.put(listDataHeader.get(2), comingSoon);


    }



}
