package com.example.hp.helpmewithmymood;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class process extends Activity  implements TextToSpeech.OnInitListener{
    String[] anger = new String[] {
            //fetch jokes data from files
            };

    String[] fear = new String[] {
            //fetch url data from files
    };

    String[] analytical = new String[] {
            //fetch facts data from files
    };

    Random rn;
    String url="";
    private TextToSpeech tts;
    int n=0;
    float score=0,temp_score=0;
    int in=0,i=0,j=0,p=0;
    String formatedDate,sentence,polo="";
    private Float value[] = new Float[7];
    private float valueval[] = new float[7];
    private String tone[]= new String[7];
    private String tonetn[]= new String[7];
    private String tw[] = new String[200];
    private TextView username_display,tweets;
    private ArrayList<Status> status;
    private String tweet1="",tweet = "",tweet12="",alltweet="";
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process);

        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        status = new ArrayList<>();
        String username = getIntent().getStringExtra("user");
        username_display = findViewById(R.id.user_display);
        username_display.setText("Welcome "+username);
        tweets = findViewById(R.id.tweets);
        if(username.equalsIgnoreCase(""))
        {

        }
        else {
            tts = new TextToSpeech(this, this);
            float x=Float.parseFloat(".95");
            tts.setSpeechRate(x);
            //speakOut();
            init(username);
        }
    }

    @SuppressLint("NewApi")
    public void init(String username) {
        String sys_date = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH).format(new Date());
        try{
            twitter4j.Twitter twitter = new TwitterFactory().getInstance();

             twitter.setOAuthConsumer("{enter your consumer key}","{enter your consumerSecret key}");
            AccessToken accessToken = new AccessToken("{enter your token key}", "{enter your tokenSecret key}");
            twitter.setOAuthAccessToken(accessToken);

            Paging paging = new Paging();

            Date date;
            Collection<Status> status1 = twitter.getUserTimeline(username,paging);
            status.addAll(status1);
            for(Status st: status) {

                Date dateStr = st.getCreatedAt();
                date = dateStr;
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                String d="";
                if((cal.get(Calendar.DATE)<10)){
                    d=d+'0'+cal.get(Calendar.DATE);
                    formatedDate = d+ "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
                }
                else{
                    formatedDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
                }
                if(sys_date.equalsIgnoreCase(formatedDate)) {
                tweet = st.getText();
                tw[p]=tweet;
                p++;
                tweet1 = tweet1 + "\t" + formatedDate + "\n" + tweet + "\n\n";
                alltweet=alltweet+tweet;
                }
            }
            if(alltweet.length()==0){
                tweet1 = tweet1 + "No tweets for today "+"\n\n";
                polo="shownull";
            }
            else {
                 ToneAnalyzerAdapter(sys_date,alltweet);
                tweets.setText(tweet1);
                for(j=0;j<p;j++){
                    ToneAna(tw[j]);
                }
                tweet1=tweet1+"\nThe tweet which has major impact on your today's mood : \n" + sentence;
                tweets.setText(tweet1);
            }
        }
        catch (TwitterException e) {
            Toast.makeText(process.this,"No tweets fetched",Toast.LENGTH_SHORT).show();
        }
    }

    public void ToneAnalyzerAdapter(String sys_date,String twe){
        ToneAnalyzer toneAnalyzer = new ToneAnalyzer("{version date}");
        toneAnalyzer.setUsernameAndPassword("{your username}","{your password}");
        toneAnalyzer.setEndPoint("url");
        ToneOptions toneOptions = new ToneOptions.Builder()
                .text(twe)
                .build();
        ToneAnalysis toneAnalysis = toneAnalyzer.tone(toneOptions).execute();
        String str=toneAnalysis.toString();
        String s="";

        if(str.length()<=44)
        {
            tweet1 = tweet1 + "No Tone observed today"+"\n\n";
            //speakOut("No Tone observed today");
            tweets.setText(tweet1);
            polo="shownull";
        }
        else{
            float value[] = new float[7];
            int i=0,k=0,len=str.length();
            int flag=0;
            while(true)
            {
                if(str.charAt(i)=='"')
                {
                    i++;
                    while(str.charAt(i)!='"')
                    {
                        s=s+str.charAt(i);
                        i++;
                    }
                    i++;
                    if(s.equals("score"))
                    {
                        s="";
                        i++;
                        while(str.charAt(i)!=',')
                        {
                            s=s+str.charAt(i);
                            i++;
                        }
                        value[k]=Float.parseFloat(s);
                        temp_score=value[k];
                        i=i+22;
                        s="";
                        while(str.charAt(i)!='"')
                        {
                            s=s+str.charAt(i);
                            i++;
                        }
                        tone[k]=s;
                        k++;
                        if((str.length()-i)<57)
                        {
                            if(str.charAt(len-1)=='}'&&str.charAt(len-3)=='}')
                            {
                                flag=1;
                                break;
                            }
                        }
                    }
                    else if(s.equals("sentences_tone")||flag==1)
                        break;
                }
                s="";
                i++;
            }
            in=0;
            float max=value[0];
            for(i=0;i<k;i++)
            {
                if(max<value[i])
                {
                    max=value[i];
                    in=i;
                }
            }
            if(k==0)
            {
                tweet1 = tweet1 + "No Tone observed today"+"\n\n";
                //speakOut("No Tone observed today");
                tweets.setText(tweet1);
                polo="shownull";
            }
            else {
                tweet1 = tweet1 + "Your Today's Mood is " + tone[in] + "\n\n";
                //String rt="Your Today's Mood is "+tone[in];
                //speakOut(rt);
                tweets.setText(tweet1);
            }

        }
    }

    public void play_Data(View view){
        Toast.makeText(process.this,""+tone[in],Toast.LENGTH_LONG).show();
        if(polo.equalsIgnoreCase("shownull")){

        }
        else {
            if (tone[in].equalsIgnoreCase("tentative")) {
                Toast.makeText(process.this, "" + tone[in], Toast.LENGTH_LONG).show();
                url = "https://s3.amazonaws.com/twitterlogindemocb7eca63e43341108bd44ca99beb2920/tentative/";
                rn = new Random();
                n = rn.nextInt(100) + 100;
                url = url + n + ".jpg";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } else if (tone[in].equalsIgnoreCase("analytical")) {
                Toast.makeText(process.this, "" + tone[in], Toast.LENGTH_LONG).show();
                rn = new Random();
                n = rn.nextInt(50);
                Toast.makeText(process.this, "" + analytical[n], Toast.LENGTH_LONG).show();
                speakOut(analytical[n]);
            } else if (tone[in].equalsIgnoreCase("anger")) {
                Toast.makeText(process.this, "" + tone[in], Toast.LENGTH_LONG).show();
                rn = new Random();
                n = rn.nextInt(15);
                Toast.makeText(process.this, "" + anger[n], Toast.LENGTH_LONG).show();
                speakOut(anger[n]);
            } else if (tone[in].equalsIgnoreCase("sadness")) {
                Toast.makeText(process.this, "" + tone[in], Toast.LENGTH_LONG).show();
                rn = new Random();
                n = rn.nextInt(2);
                try {
                    if (n == 0) {
                        url = "{cloud storge link}";
                        rn = new Random();
                        n = rn.nextInt(6);
                        url = url + n + ".mp3";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } else {
                        url = "{cloud storge link}";
                        rn = new Random();
                        n = rn.nextInt(100) + 100;
                        url = url + n + ".jpg";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (tone[in].equalsIgnoreCase("confident")) {
                Toast.makeText(process.this, "" + tone[in], Toast.LENGTH_LONG).show();
                Toast.makeText(process.this, "its quite good you are confident", Toast.LENGTH_LONG).show();
                speakOut("its quite good you are confident");
            } else if (tone[in].equalsIgnoreCase("fear")) {
                Toast.makeText(process.this, "" + tone[in], Toast.LENGTH_LONG).show();
                try {
                    rn = new Random();
                    n = rn.nextInt(6);
                    url = fear[n];
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (tone[in].equalsIgnoreCase("joy")) {
                try {
                    Toast.makeText(process.this, "" + tone[in], Toast.LENGTH_LONG).show();
                    url = "https://s3.amazonaws.com/twitterlogindemocb7eca63e43341108bd44ca99beb2920/joy/joy";
                    rn = new Random();
                    n = rn.nextInt(5);
                    url = url + n + ".mp3";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void Back(View view) {
        Intent intent = new Intent(process.this,handleActivity.class);
        startActivity(intent);
        finish();
    }
    public void pre_Day(View view) {
        ArrayList<String> dates = new ArrayList<String>();
        ArrayList<String> tones = new ArrayList<String>();
        tones.add(tone[in]);
        dates.add(formatedDate);
        Toast.makeText(process.this,""+formatedDate+"\t"+tone[in],Toast.LENGTH_LONG).show();
    }

    public void ToneAna(String twe){
        ToneAnalyzer toneAnalyzer = new ToneAnalyzer("{version date}");
        toneAnalyzer.setUsernameAndPassword("{your username}","{your password}");
        toneAnalyzer.setEndPoint("url");
        ToneOptions toneOptions = new ToneOptions.Builder()
                .text(twe)
                .build();

        ToneAnalysis toneAnalysis = toneAnalyzer.tone(toneOptions).execute();
        String strstr=toneAnalysis.toString();
        String ss="";
        int ii=0,kk=0,len=strstr.length();
        if(strstr.length()<=44)
        {

        }
        else
        {
            int flag=0;
            while(true)
            {
                if(strstr.charAt(ii)=='"')
                {
                    ii++;
                    while(strstr.charAt(ii)!='"')
                    {
                        ss=ss+strstr.charAt(ii);
                        ii++;
                    }
                    ii++;
                    if(ss.equals("score"))
                    {
                        ss="";
                        ii++;
                        while(strstr.charAt(ii)!=',')
                        {
                            ss=ss+strstr.charAt(ii);
                            ii++;
                        }
                        valueval[kk]=Float.parseFloat(ss);
                        temp_score=valueval[kk];
                        ii=ii+22;
                        ss="";
                        while(strstr.charAt(ii)!='"')
                        {
                            ss=ss+strstr.charAt(ii);
                            ii++;
                        }
                        tonetn[kk]=ss;
                        kk++;
                        if(temp_score>score){
                            score=temp_score;
                            sentence=twe;
                        }
                        if((strstr.length()-ii)<57)
                        {
                            if(strstr.charAt(len-1)=='}'&&strstr.charAt(len-3)=='}')
                            {
                                flag=1;
                                break;
                            }
                        }
                    }
                    else if(ss.equals("sentences_tone")||flag==1)
                        break;
                }
                ss="";
                ii++;
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut("");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }
    private void speakOut(String sss) {

        String text =sss;
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null,null);
    }
}