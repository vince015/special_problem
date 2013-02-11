package special.problem.prototype;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

public class RequestActivity extends SherlockActivity {
	
	private XMPPApplication app;
	
	private ActionBar actionBar;
	private ListView listViewRequest;
	private RequestListAdapter adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        
        app = (XMPPApplication)getApplication();
        
        listViewRequest = (ListView) findViewById(R.id.listViewRequest);
        adapter = new RequestListAdapter(this, app.getRequests(), app);
        listViewRequest.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_request, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
 
        switch(item.getItemId()){
            // back to contacts screen 
        	case R.id.contacts_menu:
            	Intent contacts = new Intent(getApplicationContext(), ContactsActivity.class);
    			startActivity(contacts);
    			finish();
            	break;
            	
            case R.id.logout_menu:
            // disconnect
            	app.logout();
            	finish();
            	break;
        }
        return true;
   }
}
