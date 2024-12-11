    package com.example.aircraftwar2024.activity;

    import androidx.appcompat.app.AppCompatActivity;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;

    import com.example.aircraftwar2024.R;

    public class OfflineActivity extends AppCompatActivity implements View.OnClickListener{
        public int GameType;
        public boolean musicFlag;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_offline);
            Button btn_easy = (Button) findViewById(R.id.button_easy);
            Button btn_mob = (Button) findViewById(R.id.button_mob);
            Button btn_hard = (Button) findViewById(R.id.button_hard);

            setMusicFlag(getIntent().getBooleanExtra("musicFlag", false));

            btn_easy.setOnClickListener(this);
            btn_mob.setOnClickListener(this);
            btn_hard.setOnClickListener(this);
        }
        private void setMusicFlag(boolean musicFlag){
            this.musicFlag = musicFlag;
        }
        private void setGameType(int gameType){
            this.GameType = gameType;
        }
        public void onClick(View v){
            if (v.getId() == R.id.button_easy){
                Intent intent = new Intent(OfflineActivity.this, GameActivity.class);
                setGameType(1);
                intent.putExtra("gameType", GameType);
                intent.putExtra("musicFlag",musicFlag);
                startActivity(intent);

            }
            else if (v.getId() == R.id.button_mob){
                Intent intent = new Intent(OfflineActivity.this, GameActivity.class);
                setGameType(2);
                intent.putExtra("gameType", GameType);
                intent.putExtra("musicFlag",musicFlag);
                startActivity(intent);
            }
            else if (v.getId() == R.id.button_hard){
                Intent intent = new Intent(OfflineActivity.this, GameActivity.class);
                setGameType(3);
                intent.putExtra("gameType", GameType);
                intent.putExtra("musicFlag",musicFlag);
                startActivity(intent);
            }
        }
    }