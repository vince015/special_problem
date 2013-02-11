package special.problem.prototype;

import java.util.Collection;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class XMPPService extends Service {
	private static final String TAG = "XMPPService";
	
	private XMPPApplication app;
	private XMPPConnection connection;
	private Roster roster;
	private PacketFilter presenceHandler;
	private PacketFilter messageHandler;
	 
	public XMPPService() {
		super();
		Log.i(TAG, "XMPPService constructed");
	}
	
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onCreate() {
    	super.onCreate();
    		
    	app = (XMPPApplication)getApplication();
    	connection = app.setConnection();
    	if(connection != null) {
    		// set connection handlers and listeners
    		roster = app.getRoster();
    		setPresenceHandler();
        	setRosterListener();
        	addMessageHandler();
    	}
    	
    	Log.i(TAG, "onCreated");
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	super.onStartCommand(intent, flags, startId);
    	Log.i(TAG, "onStartCommand");
    	
    	// get the connection
    	app.setConnection(connection);
    	
    	return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	Log.i(TAG, "onDestroyed");
    }
    
    // notification for a contact request
    @SuppressWarnings("deprecation")
	public void onSubscribe(String from) {
    	int icon = R.drawable.add_menu;
    	CharSequence text = "Contact Notification";
    	long when = System.currentTimeMillis();
    	
    	CharSequence info = from + " adds you to his/her contacts";
    	
    	NotificationManager notifier = (NotificationManager)XMPPService.this.
    	getSystemService(Context.NOTIFICATION_SERVICE);
    	
    	Notification notification = new Notification(icon, text, when);
    	
    	Intent pending = new Intent(this, RequestActivity.class);    	
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, pending, 0);
    	
    	notification.setLatestEventInfo(this, "Contact Request", info, contentIntent);
    
    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.DEFAULT_SOUND;
        
        notifier.notify(0x007, notification);
    }
    
    // notification for a received message
    @SuppressWarnings("deprecation")
	public void onMessage(String from, String body) {
    	int icon = R.drawable.chat_menu;
    	CharSequence text = "Message Received";
    	long when = System.currentTimeMillis();
    	
    	CharSequence info = from + " sends you a message.";
    	
    	NotificationManager notifier = (NotificationManager)XMPPService.this.
    	getSystemService(Context.NOTIFICATION_SERVICE);
    	
    	Notification notification = new Notification(icon, text, when);
    	
    	Intent pending = new Intent(this, ChatActivity.class);
    	pending.putExtra("target", from);
    	pending.putExtra("msg", body);
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, pending, 0);
    	
    	notification.setLatestEventInfo(this, "Message Received", info, contentIntent);
    
    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.DEFAULT_SOUND;
        
        notifier.notify(0x007, notification);
    }
    
    public void setPresenceHandler() {
    	presenceHandler = new PacketTypeFilter(Presence.class);
    	connection.addPacketListener(new PacketListener() {
    		
			@Override
			public void processPacket(Packet packet) {
				// TODO Auto-generated method stub
				Presence subscription = (Presence) packet;
				Presence.Type type = subscription.getType();
				String from = subscription.getFrom();
				
				if(type.equals(Presence.Type.subscribe)) {
					RosterEntry e = roster.getEntry(from);
					
					if(app.isPendingSubscription(from)){
						connection.sendPacket(new Presence(Presence.Type.subscribed));
					} else {
						// show notification						
						Log.i(TAG, "Subscription from " + from + "\nType: " + e.getType());
						app.addRequest(from);
						onSubscribe(subscription.getFrom());
					}
				} else if (type.equals(Presence.Type.subscribed)) {
					if(app.isPendingSubscription(from)){
						try {
							roster.createEntry(from, app.getName(from), null);
							app.removePendingSubscription(from);
						} catch (XMPPException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
    	}, presenceHandler);
    }
    
    
    public void setRosterListener() {
    	if(roster != null) {
	    	roster.addRosterListener(new RosterListener() {
				@Override
				public void entriesAdded(Collection<String> names) {
					// TODO Auto-generated method stub
					for(String s : names) {
			    		Log.i(TAG, s);
						RosterEntry r = roster.getEntry(s);
						if (r != null) {
							Presence p = roster.getPresence(s);
							app.addContact(r, p);
						}					
			    	}
				}
	
				@Override
				public void entriesDeleted(Collection<String> arg0) {
					// TODO Auto-generated method stub
					
				}
	
				@Override
				public void entriesUpdated(Collection<String> arg0) {
					// TODO Auto-generated method stub
					
				}
	
				@Override
				public void presenceChanged(Presence p) {
					// TODO Auto-generated method stub
					String from = p.getFrom();
					boolean cont = app.getContacts().contains(from);
					Log.i(TAG, from + " is now " + p.getType()+ " cont: " + cont);
					
				}
	    		
	    	});
    	} else Log.i(TAG, "roster is null! OH WHY?!");
    }
    
    public void addMessageHandler () {
		//add a packet listener to handle messages
	    messageHandler = new MessageTypeFilter(Message.Type.chat);
	    connection.addPacketListener(new PacketListener() {
	        public void processPacket(Packet packet) {
	            Message message = (Message) packet;
	            
	            //get sender's bare JID
	            String from = message.getFrom();
	            if(!from.equals(app.getCurrentContact()))
	            	onMessage(from, message.getBody());	            
	        }
	    },  messageHandler);
	}
}