package com.maman.football.picks.client;

/*
http://nfl-tgamann.rhcloud.com/nflwebapp/
Hello, Tommy!

http://nfl-tgamann.rhcloud.com/nflwebapp/nfl
My Picks
localhost greeting Web App greeting

http://nfl-tgamann.rhcloud.com/nflwebapp/weeks
[1,2]

http://nfl-tgamann.rhcloud.com/nflwebapp/schedule?week=1
[{"week":1,"hometeam":"49ers","visitingteam":"Bears","winner":"Bears"},{...}]

http://nfl-tgamann.rhcloud.com/nflwebapp/teams
["49ers","Bears",...]
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.maman.football.picks.client.utils.Globals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// If you are referring your localhost on your system from the Android emulator then use
// http://10.0.2.2:8080/. Because Android emulator runs inside a Virtual Machine (QEMU)
// and 127.0.0.1 or localhost will be emulator's own loopback address.
// e.g. url ="http://10.0.2.2:8080/schedule?week=1";

public class MainActivity  extends Activity {
    private final List<Game> mGameObjectList = new ArrayList<>();
    private TextView mTextView;
    private Button mPicksButton;
    private Button mRefreshButton;
    private String mWeek = "";

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTextView = (TextView)findViewById(R.id.textIO);
        mPicksButton = (Button) findViewById(R.id.picksBtn);
        mRefreshButton = (Button) findViewById(R.id.refreshBtn);

        final Spinner weekSelector = (Spinner)findViewById(R.id.weekSpinner);
        weekSelector.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTextView.setText("");
                // onItemSelected() gets invoked twice when this Activity is resumed by either
                // waking up the app or hitting the back-button from the Picks Activity. When this
                // happens, the first call will have a null View argument. We'll treat the second
                // call like a refresh and go ahead and execute the GET request.
                if (/*view != null &&*/ position > 0) { // first item is a header - not a selection.
                    mWeek = Integer.toString(position);
                    DoGetRequest("http://nfl-tgamann.rhcloud.com/nflwebapp/schedule?week=" + mWeek);
                }
                else {
                    setButtonVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void DoGetRequest(String url) {
		ConnectivityManager connMgr =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(url);
		} else {
			mTextView.setText("Sorry... network connection not available.");
		}
	}
	
    // DownloadWebpageTask is an AsyncTask that runs outside the main UI thread.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
       @Override
       protected String doInBackground(String... urls) {
           // doInBackground() is the first thing invoked when DownloadWebpageTask().execute().
           // is called. Its parameters come from the execute() call: params[0] is the url.
           // a URL string, creates an HttpUrlConnection, and sends a GET request.
           // The returned InputStream is converted into a string, which is displayed in the UI
           // by the AsyncTask's onPostExecute method.
           try {
               URL url = new URL(urls[0]);
               // Establish an HttpURLConnection, and send a GET request.
               HttpURLConnection conn = (HttpURLConnection) url.openConnection();
               conn.setReadTimeout(10000);    // 10 sec
               conn.setConnectTimeout(15000); // 15 sec
               conn.setRequestMethod("GET");
               conn.setDoInput(true);
               // Starts the query
               conn.connect();
               // Get the response code from the server and verify that it's good.
               int response = conn.getResponseCode();
               if (response == HttpURLConnection.HTTP_OK) {
                   // Retrieve what the Web App renders through an input stream.
                   InputStream is = conn.getInputStream();
                   // Convert the InputStream into a string
                   BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                   String content = reader.readLine();
                   is.close();

                   // return value gets sent as a parameter to onPostExecute().
                   return content;
               }
           } catch (IOException e) {
               return "Unable to retrieve web page. URL may be invalid.";
           }
           return "Schedule not available for selected week";
       }
       // onPostExecute displays the results of the AsyncTask.
       @Override
       protected void onPostExecute(String result) {
           // [{"week":1,"hometeam":"49ers","visitingteam":"Bears","winner":"Bears"},{...}]
           try {
               JSONArray jsonArray = new JSONArray(result);
               if (jsonArray.length() < 1) {
                   mTextView.setText("Sorry... selected week not yet available.");
                   setButtonVisibility(View.INVISIBLE);
                   return;
               }
               mGameObjectList.clear();
               int week = 0;
               for (int i = 0; i < jsonArray.length(); i++) {
                   JSONObject jsonObject = jsonArray.getJSONObject(i);
                   week = jsonObject.getInt("week");
                   String home = jsonObject.getString("hometeam");
                   String visitor = jsonObject.getString("visitingteam");
                   String winner = jsonObject.getString("winner");
                   mGameObjectList.add(new Game(week, home, visitor, winner));
               }
               StringBuilder stringBuilder = new StringBuilder();
               for (Game game : mGameObjectList) {
                   stringBuilder.append(game.visitingteam);
                   if (week == 21) { // Super Bowl week.
                       stringBuilder.append(" vs ");
                   }
                   else {
                       stringBuilder.append(" at ");
                   }
                   stringBuilder.append(game.hometeam);
                   stringBuilder.append("\n");
               }
               stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("\n"));
               mTextView.setText(stringBuilder);
               setButtonVisibility(View.VISIBLE);
           }
           catch (JSONException e) {
               mTextView.setText("Sorry... unable to retrieve data from Web site.");
           }
       }
    }

    public void onClickRefreshBtn(View v) {
        DoGetRequest("http://nfl-tgamann.rhcloud.com/nflwebapp/schedule?week=" + mWeek);
    }

    public void onClickPicksBtn(View v) {
        ArrayList<String> gameList = new ArrayList<>();
        for (Game game : mGameObjectList) {
            // Add teams to a list of matchups such that the winning team is listed first;
            // that way, when we rotate the footballs to the winning side, it's the same
            // rotation for all footballs.
            StringBuilder sb = new StringBuilder(game.winner);
            sb.append(",");
            if (game.winner.contains(game.hometeam)) {
                sb.append(game.visitingteam);
            } else {
                sb.append(game.hometeam);
            }
            sb.append(" Away"); // the last image will be on the side of the football facing "away"
            gameList.add(sb.toString());
        }

        Intent intent = new Intent(this, PicksActivity.class);
        intent.putStringArrayListExtra(Globals.GAME_LIST_KEY, gameList);
        startActivity(intent);
    }

    private void setButtonVisibility(int visibility) {
        mPicksButton.setVisibility(visibility);
        mRefreshButton.setVisibility(visibility);
    }

}
