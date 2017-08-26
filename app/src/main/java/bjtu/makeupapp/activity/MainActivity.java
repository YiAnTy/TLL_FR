package bjtu.makeupapp.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bjtu.makeupapp.R;
import bjtu.makeupapp.adapter.StyleAdapter;
import bjtu.makeupapp.model.StyleItem;

public class MainActivity extends AppCompatActivity {

    private List<StyleItem>styleItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initStyle();

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        StyleAdapter styleAdapter = new StyleAdapter(styleItems);
        recyclerView.setAdapter(styleAdapter);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
    }
    private void initStyle(){
       for(int i = 0;i<2;i++){
            StyleItem s1 = new StyleItem(R.drawable.style_one,"烟熏妆");
            styleItems.add(s1);
            StyleItem s2 = new StyleItem(R.drawable.style_two,"橘系妆容");
            styleItems.add(s2);
            StyleItem s3 = new StyleItem(R.drawable.style_three,"御姐妆");
            styleItems.add(s3);
        }
    }
}
