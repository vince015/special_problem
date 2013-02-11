package special.problem.prototype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

public class XMPPApplication extends Application {
	private static final String TAG = "XMPPApplication";
	private String SERVER_HOST = "172.16.14.10"; // chat.intranet.uplb.edu.ph
	//private String SERVER_HOST = "10.0.2.2"; // localhost
	private boolean ip= false;
	//private String SERVER_HOST = "192.168.11.98"; // ip address
	private int SERVER_PORT = 5222;
	
	private Intent service;
	private ConnectionConfiguration config;
    private XMPPConnection connection;
	private Roster roster;
    
	private HashMap<String, String> pendingSubscriptions = new HashMap<String, String>();
	private ArrayList<String> requests = new ArrayList<String>();
    private Collection<RosterEntry> entries;
	private ArrayList<Contact> contacts = new ArrayList<Contact>();
    private String currentContact;    
    
	@Override
	public void onCreate() {		
		Log.i(TAG, "onCreated");
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i(TAG, "onTerminated");
	}
	
	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
	}
	
	// sets up XMPPConnection
	public synchronized XMPPConnection setConnection() {
		if(connection == null) {	    	
	    	config = new ConnectionConfiguration(SERVER_HOST, SERVER_PORT);
	    	//config = new ConnectionConfiguration(SERVER_HOST, SERVER_PORT, SERVICE_NAME);
	    	
	    	// for authentication stuffs
	    	config.setSASLAuthenticationEnabled(true); 
			config.setSecurityMode(SecurityMode.disabled);
			config.setDebuggerEnabled(true);
			connection = new XMPPConnection(config);
			
			try {
				SASLAuthentication.supportSASLMechanism("PLAIN", 0);
				// connect to server
				connection.connect();
			} catch (XMPPException e) {
				// catch block if server connection is unsuccessful
				e.printStackTrace();
				connection = null;
			}
		}
		Log.i(TAG, "connection: " + connection);
		return connection;
	}
	
	public boolean login(String netID, String password) {
		if(connection.isConnected()) {
			try {
				if(ip) {
					connection.login("dwade", "dwyane", "resource");
				} else {
				// login using user's input
				connection.login("amzrobles", "izaeyogeix", "resource");
				}
				
				//retrieve roster 
				roster = connection.getRoster();
				roster.setSubscriptionMode(SubscriptionMode.manual);
				entries = roster.getEntries();
				
				// start background service
				service = new Intent(this, XMPPService.class);
				startService(service);				
			} catch (XMPPException e) {
				// catch block for unsuccessful login
				e.printStackTrace();
			}
		}
		return connection.isAuthenticated();
	}
	
	public void logout() {
		// stop background service
		stopService(service);
		
		// terminate connection
		connection.disconnect();
		connection = null;
	}
	
	public Roster getRoster() {
		return roster;
	}
	
	public void setRoster(Roster roster) {
		this.roster = roster;
	}
	
	// stores roster to ArrayList of contacts 
	public ArrayList<Contact> getContacts() {    	
		contacts.clear();
    	if(!entries.isEmpty()) {
			for(RosterEntry r : entries) {
	    		Presence p = roster.getPresence(r.getUser());
	    		Contact c = new Contact(r);
	    		c.setPresence(p);
	    		
	    		contacts.add(c);
	    	}
		}
    	return contacts;
	}
	
	// adds a contact to array list
	public void addContact(RosterEntry r, Presence p) {
		Contact c = new Contact(r);
		c.setPresence(p);
		contacts.add(c);
	}
	
	// adds request to array list
	public void addRequest(String requester) {
		requests.add(requester);
	}
	
	// deletes request in the array list
	public void removeRequest(String requester) {
		requests.remove(requester);
	}
	
	public ArrayList<String> getRequests() {
		return requests;
	}
	
	// set the contact whom the user is talking to
	public void setCurrentContact(String currentContact) {
		this.currentContact = currentContact;
	}
	
	public String getCurrentContact() {
		return currentContact;
	}
	
	public void addPendingSubscription(String netID, String name) {
		pendingSubscriptions.put(netID, name);
	}
	
	public void removePendingSubscription(String netID) {
		pendingSubscriptions.remove(netID);		
	}
	
	public String getName(String netID) {
		return pendingSubscriptions.get(netID);
	}
	
	public boolean isPendingSubscription(String netID) {
		return pendingSubscriptions.containsKey(netID);
	}
	
}