package special.problem.prototype;

import java.util.ArrayList;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ContactsActivity extends SherlockActivity implements OnClickListener {
	private static final String TAG = "ContactsActivity";
	private ArrayList<Contact> contacts;
	private XMPPApplication app;
	private ActionBar actionBar;
	
	private Button buttonAddEmpty;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        
        app = ((XMPPApplication) getApplication());
        setContacts();
		
        buttonAddEmpty = (Button) findViewById(R.id.buttonAddEmpty);
        buttonAddEmpty.setOnClickListener(this);
        
        Log.i(TAG, "onCreated");        
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_contacts, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.buttonAddEmpty) {
			AddContactDialog addDialog = new AddContactDialog(this);
			addDialog.show();
		}
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
 
        switch(item.getItemId()){
            case R.id.chat_menu:
            	Intent chat = new Intent(getApplicationContext(), ChatActivity.class);
    			startActivity(chat);
     
    			contacts.clear();
    			finish();
            	break;
 
            case R.id.call_menu:
            	Log.i(TAG, "call_menu");
            	Toast.makeText(getBaseContext(), 
                		"You selected call", Toast.LENGTH_SHORT).show();
                break;
                
            case R.id.logout_menu:
            	Log.i(TAG, "logout_menu");
            	app.logout();
            	finish();
            	break;
        }
        return true;
    }
    
    public void setContacts() {
    	contacts = app.getContacts();
    	
    	final ListView listViewContacts = (ListView) findViewById(R.id.listViewContacts);
    	listViewContacts.setAdapter(new ContactListAdapter(this, contacts));
    	listViewContacts.setEmptyView(findViewById(R.id.emptyView));
    	listViewContacts.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> a, View v, int position, long id) {
             Object o = listViewContacts.getItemAtPosition(position);
             
             Contact fullObject = (Contact)o;
             
             String target = fullObject.getNetID();             
             Intent chat = new Intent(getApplicationContext(), ChatActivity.class);
 			 chat.putExtra("target", target);
             app.setCurrentContact(target);
 			 startActivity(chat);
  
 			 contacts.clear();
 			 finish();
             //Toast.makeText(getBaseContext(), fullObject.getNetID() + " selected", 
						//Toast.LENGTH_SHORT).show();
    		}
    	});
    }
    
    public XMPPApplication getApp() {
		return app;
    }
}
