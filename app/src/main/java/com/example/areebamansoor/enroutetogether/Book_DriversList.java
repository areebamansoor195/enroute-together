package com.example.areebamansoor.enroutetogether;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Book_DriversList extends AppCompatActivity {
    int[] IMAGES = {R.drawable.icon5, R.drawable.icon1, R.drawable.icon6, R.drawable.icon2, R.drawable.icon3, R.drawable.icon4};

    String[] NAMES = {"Areeba Mansoor", "Hafsa Baig", "Junaid khan", "Sidra ijaz", "Bilal Khan", "Taha Skeikh"};
    String[] DESCRIPTIONS = {"North nazimabad to JUW", "Bufferzone to JUW", "Sakhi Hassan to JUW", "Shafiq Morh to JUW", "Hydri to JUW","Gulshan Iqbal to JUW"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_driverslist);

        ListView listView=(ListView)findViewById(R.id.listview);
        CustomAdapter customAdapter=new CustomAdapter();
        listView.setAdapter(customAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Book_DriversList.this, Book_DriverDetails.class);
                startActivity(intent);
            }
        });
    }

    class CustomAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return IMAGES.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup){
            view = getLayoutInflater().inflate(R.layout.activity_book_customdrivers,null);
            ImageView imageView=(ImageView)view.findViewById(R.id.imageView);
            TextView textView_name=(TextView)view.findViewById(R.id.textView_name);
            TextView textView_description=(TextView)view.findViewById(R.id.textView_description);

            imageView.setImageResource(IMAGES[i]);
            textView_name.setText(NAMES[i]);
            textView_description.setText(DESCRIPTIONS[i]);

            return view;
        }
    }

}
