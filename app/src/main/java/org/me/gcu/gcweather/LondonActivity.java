//  ==============================
//  **  Name: DEBBIE ADEJUMO    **
//  **  Student ID: S1719011    **
//  ==============================


package org.me.gcu.gcweather;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
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

import java.util.Calendar;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.widget.ExpandableListView;





public class LondonActivity extends AppCompatActivity {


    String[] titlesArray;
    TextView description, temp, minMax, windSpeedValue, humidityValue;
    ImageView todaysWeatherImage;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_london);
        todaysWeatherImage=findViewById(R.id.todaysWeatherImage);
        description = findViewById(R.id.description);
        temp = findViewById(R.id.temp);
        minMax = findViewById(R.id.minmax);
        windSpeedValue = findViewById(R.id.windSpeed);
        humidityValue= findViewById(R.id.humidity);


        //Set today's date
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        TextView textDateDisplay = findViewById(R.id.text_date_display);
        textDateDisplay.setText(currentDate);


        ProcessInBackground processInBackground = new ProcessInBackground();
        processInBackground.execute();
        try {
            List<String[]> result = processInBackground.get();
            prepareListData(result);
            //prepareListData();
            expListView = (ExpandableListView) findViewById(R.id.lvExp);

            listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

            // setting list adapter
            expListView.setAdapter(listAdapter);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void onLeftArrowClick(View v){
        Intent myIntent = new Intent(getBaseContext(), JoburgActivity.class);
        startActivity(myIntent);
    }

    public void onRightArrowClick(View v){
        Intent myIntent = new Intent(getBaseContext(), NairobiActivity.class);
        startActivity(myIntent);
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

    public class ProcessInBackground extends AsyncTask<Context, Void, List<String[]>>{
        ProgressDialog progressDialog = new ProgressDialog(LondonActivity.this);
        Exception exception = null;
        ArrayList<String> titles;
        ArrayList<String> weatherDescriptions;
        List<String[]> weatherData;



        @Override
        protected List<String[]> doInBackground(Context... params) {
            titles = new ArrayList<>();
            weatherDescriptions = new ArrayList<>();
            weatherData= new ArrayList<>();

            try {
                URL url = new URL("https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/2643743");

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

            //convert titles array list to an array
            titlesArray = titles.toArray(new String[titles.size()]);

            //convert weatherDescriptions array list to an array
            String[] descriptionsArray = weatherDescriptions.toArray(new String[weatherDescriptions.size()]);
            weatherData.add(titlesArray);
            weatherData.add(descriptionsArray);
            return weatherData;
        }

        @Override
        protected void onPostExecute(List<String[]> s) {
            super.onPostExecute(s);



            //convert titles array list to an array
            titlesArray = titles.toArray(new String[titles.size()]);

            //convert weatherDescriptions array list to an array
            String[] descriptionsArray = weatherDescriptions.toArray(new String[weatherDescriptions.size()]);

            //Get and set values for current day
            String today = titlesArray[0];
            String[] todayValues = today.split(":|,");
            String temp_min = (todayValues[3].substring(0,5));
//
//            String temp_max = (todayValues[4]);
            String[] todayDescription = descriptionsArray[1].split(":|,");
            String min_max = ((todayDescription[1].substring(0,5)) + " /" + (todayDescription[3].substring(0,5)));
            String windSpeed = (todayDescription[7]);
            String humidity = (todayDescription[13]);

            description.setText(todayValues[1]);
            temp.setText(temp_min);
            minMax.setText(min_max);
            windSpeedValue.setText(windSpeed);
            humidityValue.setText(humidity);




            String todaysDescription = todayValues[1];

            if (todaysDescription.contains("Sun")){
                todaysWeatherImage.setImageResource(R.drawable.sunny);
            }
            else if(todaysDescription.contains("Thunder")||todaysDescription.contains("thunder")){
                todaysWeatherImage.setImageResource(R.drawable.storm2);
            }
            else if(todaysDescription.contains("Rain")||todaysDescription.contains("rain")){
                todaysWeatherImage.setImageResource(R.drawable.drop);
            }
            else if(todaysDescription.contains("drizzle")){
                todaysWeatherImage.setImageResource(R.drawable.drizzle);
            }
            else if(todaysDescription.contains("Snow")||todaysDescription.contains("snow")){
                todaysWeatherImage.setImageResource(R.drawable.snowflake);
            }
            else if(todaysDescription.contains("Hail")||todaysDescription.contains("hail")){
                todaysWeatherImage.setImageResource(R.drawable.hail);
            }
            else if(todaysDescription.contains("Cloud")||todaysDescription.contains("cloud")){
                todaysWeatherImage.setImageResource(R.drawable.clouds);
            }
            else if(todaysDescription.contains("Clear")||todaysDescription.contains("clear")){
                todaysWeatherImage.setImageResource(R.drawable.sun);
            }
            else if(todaysDescription.contains("Mist")||todaysDescription.contains("mist")){
                todaysWeatherImage.setImageResource(R.drawable.haze);
            }



            progressDialog.dismiss();
        }
    }



    /*
     * Preparing the list data
     */
    private void prepareListData(List<String[]> weatherData) {

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();


        List<String> today = new ArrayList<>();
        String[] titles = weatherData.get(0);
        String [] descriptions = weatherData.get(1);


        String[] todayValues = titles[0].split(":|,");
        String[] dayTwoValues = titles[1].split(":|,");
        String[] dayThreeValues = titles[2].split(":|,");


        String[] todayDescriptions = descriptions[1].split(":|,");
        String[] dayTwoDescriptions = descriptions[1].split(":|,");
        String[] dayThreeDescriptions = descriptions[2].split(":|,");


        // Adding child data
        listDataHeader.add(todayValues[0]);
        listDataHeader.add(dayTwoValues[0]);
        listDataHeader.add(dayThreeValues[0]);

        // Adding child data
        List<String> day1 = new ArrayList<String>();
        day1.add("Temperature: "+ (todayDescriptions[1].substring(0,5)) + "/" + (todayDescriptions[3].substring(0,5)));
        day1.add("Wind direction: "+ todayDescriptions[5]);
        day1.add("Wind speed: "+ todayDescriptions[7]);
        day1.add("Humidity: "+ todayDescriptions[13]);

        List<String> day2 = new ArrayList<String>();
        day2.add("Temperature: "+ (dayTwoDescriptions[1].substring(0,5)) + "/" + (dayTwoDescriptions[3].substring(0,5)));
        day2.add("Wind direction: "+ dayTwoDescriptions[5]);
        day2.add("Wind speed: "+ dayTwoDescriptions[7]);
        day2.add("Humidity: "+ dayThreeDescriptions[13]);


        List<String> day3 = new ArrayList<String>();
        day3.add("Temperature: "+ (dayThreeDescriptions[1].substring(0,5)) + "/" + (dayThreeDescriptions[3].substring(0,5)));
        day3.add("Wind direction: "+ dayThreeDescriptions[5]);
        day3.add("Wind speed: "+ dayThreeDescriptions[7]);
        day3.add("Humidity: "+ dayThreeDescriptions[13]);


        listDataChild.put(listDataHeader.get(0), day1); // Header, Child data
        listDataChild.put(listDataHeader.get(1), day2);
        listDataChild.put(listDataHeader.get(2), day3);

    }

}
