package tycho.your_own;

/**
 * Created by Tycho on 15-12-2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Tycho on 18-11-2016.
 */

public class SecondCustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> titles = new ArrayList<String>();
    private ArrayList<String> authors = new ArrayList<String>();
    private ArrayList<String> descriptions = new ArrayList<String>();
    private Context context;

    public SecondCustomAdapter(ArrayList<String> titles, ArrayList<String> authors, ArrayList<String> descriptions, Context context) {
        this.titles = titles;
        this.authors = authors;
        this.descriptions = descriptions;
        this.context = context;
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Object getItem(int pos) {
        return titles.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos+1;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.book_details, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView) view.findViewById(R.id.title);
        listItemText.setText(titles.get(position));

        TextView AuthorlistItemText = (TextView) view.findViewById(R.id.author);
        AuthorlistItemText.setText(authors.get(position));

        TextView DescriptionslistItemText = (TextView) view.findViewById(R.id.description);
        DescriptionslistItemText.setText(descriptions.get(position));

        return view;
    }

}
