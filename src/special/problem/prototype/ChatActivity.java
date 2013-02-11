package special.problem.prototype;

import java.util.ArrayList;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends SherlockActivity implements OnClickListener {
	private static final String TAG = "ChatActivity";
	
	private XMPPApplication app;
	private ActionBar actionBar;
	private String receipient;
	private Button buttonSend;
	private EditText editTextMsgBody;
	private EditText editTextReceipient;
	private ArrayList<String> messages;	
	
	private ListView listViewMsg;
	private ChatListAdapter adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    
        Intent i = getIntent();
        receipient = i.getStringExtra("target");
            
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        
        app = (XMPPApplication)getApplication();
        
        editTextMsgBody = (EditText) findViewById(R.id.editTextMsgBody);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(this);
        
        messages = new ArrayList<String>();
        listViewMsg = (ListView) findViewById(R.id.listViewMsg);
        adapter = new ChatListAdapter(this, messages);
        listViewMsg.setAdapter(adapter);
        
        editTextReceipient = (EditText) findViewById(R.id.editTextReceipient);
        if(receipient != null)
        		editTextReceipient.setText(receipient);
        if(i.getStringExtra("msg") != null)
        	messages.add(receipient + ":" + i.getStringExtra("msg"));
        setMsgReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
 
        switch(item.getItemId()){
            // back to contacts screen 
        	case R.id.contacts_menu:
            	app.setCurrentContact(null);
        		Intent contacts = new Intent(getApplicationContext(), ContactsActivity.class);
    			startActivity(contacts);
    			finish();
            	break;
            	
            case R.id.logout_menu:
            // disconnect
            	messages.clear();
            	app.logout();
            	finish();
            	break;
        }
        return true;
   }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.buttonSend) {
			String body = editTextMsgBody.getText().toString();
			String to = editTextReceipient.getText().toString();
			
			if(!body.equals("")) {				
				Message msg = new Message(receipient, Message.Type.chat);
		        msg.setTo(to);
				msg.setBody(body);
				
		        messages.add("You:"+body);
	    		app.setConnection().sendPacket(msg);
	    		
	    		editTextMsgBody.setText("");
	    		adapter.notifyDataSetChanged();
			}
		}
	}
	
	public void setMsgReceiver() {
		PacketFilter msgFilter = new MessageTypeFilter(Message.Type.chat);
		app.setConnection().addPacketListener(new PacketListener() {
			@Override
			public void processPacket(Packet packet) {
				// TODO Auto-generated method stub
				Message msg = (Message) packet;
									
				String from[] = msg.getFrom().split("/");
				Log.i(TAG, "from: " + from[0] + " receipient: " + receipient);
				if(msg.getBody() != null && 
						(from[0].equals(receipient) || 
						msg.getFrom().equals(editTextReceipient.getText()))) {
					messages.add(from[0] + ":" + msg.getBody());
					adapter.notifyDataSetChanged();
				}
			}
		}, msgFilter);
	}
}