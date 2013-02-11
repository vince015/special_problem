package special.problem.prototype;

import org.jivesoftware.smack.XMPPConnection;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConnectionActivity extends Activity implements OnClickListener {
	private static final String TAG = "ConnectionActivity";
	
	XMPPApplication app;
	XMPPConnection connection;
	EditText editTextNetID;
    EditText editTextPassword;
    Button buttonConnect;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // check if background service is running
        if(isXMPPServiceRunning()) {
        	// direct to ContactActivity if background service is still active
        	Intent contactsScreen = new Intent(getApplicationContext(), ContactsActivity.class);
			startActivity(contactsScreen);
			
			finish();        	
        } else {
        	// initialize screen and widgets
        	setContentView(R.layout.activity_connection);
            
            editTextNetID = (EditText) findViewById(R.id.editTextNetID);
            editTextNetID.requestFocus();
            
            editTextPassword = (EditText) findViewById(R.id.editTextPassword);
                
            buttonConnect = (Button) findViewById(R.id.buttonConnect);
            buttonConnect.setOnClickListener(this);
        }            
        
		Log.i(TAG, "service; " + isXMPPServiceRunning());
        Log.i(TAG, "onCreated");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_connection, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.buttonConnect) {
			Log.i(TAG, "buttonConnect Clicked");
			
			app = ((XMPPApplication) getApplication());
    		connection = app.setConnection();
			
    		// get user's input
			String netID = editTextNetID.getText().toString();
			String password = editTextPassword.getText().toString();
			
			// connect to server then login
			if(connection != null) {
				Log.i(TAG, "connection established.");
				if(app.login(netID, password)) {
					Log.i(TAG, "authentication successful.");
					
					// set and start ContactsActivity
					Intent contactsScreen = new Intent(getApplicationContext(), ContactsActivity.class);
					startActivity(contactsScreen);
					
					// finish this activity
					finish();
				} else {
					// inform user that login was unsuccessful
					Toast.makeText(getBaseContext(), "Cannot connect as " + netID, 
							Toast.LENGTH_SHORT).show();
				}
			} else {
				// inform user that the application was unable to connect to server
				Toast.makeText(getBaseContext(), "Cannot connect to server", 
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	// checks if background service is active
	private boolean isXMPPServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (XMPPService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
}