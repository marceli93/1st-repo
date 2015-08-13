package pl.cba.martindesign.simplemp3;

import android.content.Intent;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class Player extends AppCompatActivity implements View.OnClickListener {


    static MediaPlayer mediaPlayer; //tak nazwałem swoj obiekt mediaplayer
    ArrayList<File> mySongs;
    int position;
    Uri uri;
   //TextView fileNam;

    //
    Thread updateSeekBar;


    SeekBar progSeekBar;
    Button playButton,prevButton,nextButton,backwardButton,forwardButton ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // przycisk play stopuje
        ImageButton playButton = (ImageButton) findViewById(R.id.playButton);
        ImageButton forwardButton = (ImageButton) findViewById(R.id.forwardButton);
        ImageButton backwardButton = (ImageButton) findViewById(R.id.backwardButton);
        ImageButton nextButton = (ImageButton) findViewById(R.id.nextButton);
        ImageButton prevButton = (ImageButton) findViewById(R.id.prevButton);
        //fileNam = (TextView) findViewById(R.id.fileNameTextView);

        playButton.setOnClickListener(this);
        forwardButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        backwardButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        progSeekBar = (SeekBar) findViewById(R.id.progressSeekBar);
        updateSeekBar = new Thread() {
            @Override
            public void run() {
                int totalTime = mediaPlayer.getDuration();
                int currentPosition = 0;
                progSeekBar.setMax(totalTime);  //maksuje seek bar
                while (currentPosition < totalTime) {
                    try {
                        sleep(100);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        progSeekBar.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        if (mediaPlayer != null) { //powoduje ze przy wyjsciu do listy i wybraniu innego mp3 piosenki nei nakłądają sie. poprzednia zatrzymuje sie
            mediaPlayer.stop();
            mediaPlayer.release();
        }

// intent wywołujący nową aktywnosc PLAYER
        // ogólnie działa to tak że z listy wybieram sobie plik klikam zaczyna grac i otwiera player czyli tą aktywność

        Intent i = getIntent();
        Bundle b = i.getExtras();
        mySongs = (ArrayList) b.getParcelableArrayList("songlist");
        position = b.getInt("pos", 0);


        Uri uri = Uri.parse(mySongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        progSeekBar.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();
                //gra jak klikne




        //updejtuje seekbar po zmianie piosenki  i przewija piosenki palcem
        progSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
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


 //FUNKCJONALNOSC PRZYCISKOW
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.playButton:
                if(mediaPlayer.isPlaying()) {

                    mediaPlayer.pause();
                }
                else {
                    mediaPlayer.start();

                }
                break;
            case R.id.forwardButton:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+5000);
                break;
            case R.id.backwardButton:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-5000);
                break;
            case R.id.nextButton:
                mediaPlayer.stop();
                mediaPlayer.reset();  //.release() powodowało błąd po naciśnięciu next
                position = (position+1)%mySongs.size();

                uri = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();

                break;
            case R.id.prevButton:
                mediaPlayer.stop();
                mediaPlayer.reset();
                position = (position-1<0) ? mySongs.size()-1: position-1;
               /* if(position-1 < 0) {
                    position = mySongs.size()-1;

                }
                else {
                    position = position-1;
                }
                */

                uri = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                progSeekBar.setMax(mediaPlayer.getDuration());


                break;



        }
    }
}
