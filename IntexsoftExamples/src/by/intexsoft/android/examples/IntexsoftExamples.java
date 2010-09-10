package by.intexsoft.android.examples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import by.intexsoft.android.examples.memory.MemoryUsage;


public class IntexsoftExamples extends ListActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setListAdapter(new SimpleAdapter(this, prepareData(),
                android.R.layout.simple_list_item_1, new String[] { "title" },
                new int[] { android.R.id.text1 }));
        getListView().setTextFilterEnabled(true);
    }

    private List<Map<String, Object>> prepareData() {
        List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();
        addItem(myData, R.string.memory_example,MemoryUsage.class);
        return myData;
    }
    
    private void addItem(List<Map<String, Object>> data, int text, Class<?> classname) {
    	Intent intent=new Intent(this,classname);
        Map<String, Object> temp = new HashMap<String, Object>();
        temp.put("title", getText(text).toString());
        temp.put("intent", intent);
        data.add(temp);
    }

    @SuppressWarnings("unchecked")
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	Map map = (Map) l.getItemAtPosition(position);
        Intent intent = (Intent) map.get("intent");
        startActivity(intent);
    }
}

