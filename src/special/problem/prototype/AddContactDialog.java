package special.problem.prototype;

import org.jivesoftware.smack.packet.Presence;

import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddContactDialog extends Dialog implements OnClickListener {
	private XMPPApplication app;
	
	private EditText editTextAddNetID;
	private EditText editTextAddName;
	private Button buttonAdd;
	
	public AddContactDialog(ContactsActivity contactsActivity) {
		super(contactsActivity);
		// TODO Auto-generated constructor stub
		app = contactsActivity.getApp();				
	}
	
	protected void onStart() {
		super.onStart();
        setContentView(R.layout.dialog_add_contact);
        getWindow().setFlags(4, 4);
        setTitle("Add a Contact");
        
        editTextAddNetID = (EditText) findViewById(R.id.editTextAddNetID);
        editTextAddName = (EditText) findViewById(R.id.editTextAddName);
        
        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String to = editTextAddNetID.getText().toString();
		String name = editTextAddName.getText().toString();		
		
		app.addPendingSubscription(to, name);
		Presence subscribe = new Presence(Presence.Type.subscribe);
		subscribe.setTo(to);
		
		app.setConnection().sendPacket(subscribe);
		dismiss();
	}
	
}
