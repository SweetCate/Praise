package health.water.com.praise;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {



    private PraiseView mPraiseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPraiseView = findViewById(R.id.ttt);
        mPraiseView.setNum(20);

    }

    public void add(View view){
        mPraiseView.setNum(mPraiseView.getCurrentNum()+1);
    }

    public void reduce(View v){
        mPraiseView.setNum(mPraiseView.getCurrentNum()-1);
    }
    public void praise(View v){
        mPraiseView.setPraised(!mPraiseView.getPraised());
    }
}
