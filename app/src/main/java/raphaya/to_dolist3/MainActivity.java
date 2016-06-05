package raphaya.to_dolist3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> todoItems;
    ArrayAdapter<String> aa;
    EditText mavariableEditText;
    ListView mavariableListView;
    private Toolbar menu;
    private Button clear;
    private Integer id = 0;
    private NotesDbAdapter database;
    private Cursor notes;
    private Context activity = this;



    public void initList(){
        notes = database.fetchAllNotes();
        if (notes != null && notes.getCount() > 0){
            int columnIndex=notes.getColumnIndex(database.KEY_BODY);
            while(notes.moveToNext()) {
                todoItems.add(notes.getString(columnIndex));
            }
        }
        aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todoItems);
        mavariableListView.setAdapter(aa);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new NotesDbAdapter(this);
        database.open();
        this.setContentView(R.layout.activity_main);

        mavariableEditText = (EditText) findViewById(R.id.champAjout);
        mavariableListView = (ListView) findViewById(R.id.listData);

        todoItems = new ArrayList<String>();
        aa = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, todoItems);

        mavariableListView.setAdapter(aa);
        initList();
        registerForContextMenu(mavariableListView);

        mavariableEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                    if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                        database.createNote(mavariableEditText.getText().toString());
                        todoItems.add(0, mavariableEditText.getText().toString());
                        aa.notifyDataSetChanged();
                        mavariableEditText.setText("");
                        return true;
                    }
                return false;
            }

        });

        mavariableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                database.deleteNote((long)position);
                todoItems.remove(position);
                aa.notifyDataSetChanged();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void btnClick(View view) {
        database.createNote(mavariableEditText.getText().toString());
        todoItems.add(0, mavariableEditText.getText().toString());
        aa.notifyDataSetChanged();
        mavariableEditText.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_1:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Are you sure ?");
                builder.setMessage("Delete all the task ?");
                        // Add the buttons
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        notes = database.fetchAllNotes();
                        for (int i=0; i <= notes.getColumnCount();i++){
                           database.deleteNote(i);
                        }
                      /*  todoItems.clear();
                        todoItems = new ArrayList<String>();
                        aa = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, todoItems);
                        mavariableListView.setAdapter(aa);
                        aa.notifyDataSetChanged();*/
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                // Create the AlertDialog
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        id = v.getId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle("Search on...");
        String[] menuItems = getResources().getStringArray(R.array.menu);
        for (int i = 0; i<menuItems.length; i++) {
            menu.add(Menu.NONE, i, i, menuItems[i]);
        }
    }

    public boolean onContextItemSelected(MenuItem item){

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String selectedTask = (String) mavariableListView.getItemAtPosition(info.position);

        if(item.getItemId()==0){
            Uri uri = Uri.parse("http://www.google.com/#q="+selectedTask);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        else if(item.getItemId()==1){
            Uri gmmIntentUri = Uri.parse("geo:0,0?q="+selectedTask);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }else{
            return false;
        }
        return true;
    }

}
