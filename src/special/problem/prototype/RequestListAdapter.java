package special.problem.prototype;

import java.util.ArrayList;

import org.jivesoftware.smack.packet.Presence;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RequestListAdapter extends BaseAdapter {
	private ArrayList<String> requestList;
	private LayoutInflater mInflater;
	private final Context context;
	
	private XMPPApplication app;
	
	public RequestListAdapter(Context context, ArrayList<String> requests, XMPPApplication app) {
		this.context = context;
		this.app = app;
		requestList= requests;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return requestList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return requestList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	/*
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}
	*/
	public View getView(int position, View convertView, ViewGroup parent) {
        /*
         * Please note that while this code works it is somewhat inefficient
         * and may result in some jerky scrolling. Please read the article
         * which explains this code at http://blog.stylingandroid.com/archives/623
         * for further explanation and base any production code on the later,
         * more efficient examples.
         */
        context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View v = mInflater.inflate(R.layout.request_row, parent, false );
        
        final String target = (String) getItem(position);
        
        TextView requester = (TextView)v.findViewById(R.id.requester);
        requester.setText(target);
        
        ImageView accept = (ImageView)v.findViewById(R.id.accept_req);  
        accept.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick( View v ) {
            	Presence confirm = new Presence(Presence.Type.subscribed);
        		confirm.setTo(target);
        		app.setConnection().sendPacket(confirm);
            	
        		app.removeRequest(target);
                Toast.makeText( context, "Accept request from " + target, 
                		Toast.LENGTH_SHORT ).show();
            }
        } );
        
        ImageView deny = (ImageView)v.findViewById(R.id.deny_req);  
        deny.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick( View v ) {
            	Presence confirm = new Presence(Presence.Type.unsubscribe);
        		confirm.setTo(target);
        		app.setConnection().sendPacket(confirm);
            	
        		app.removeRequest(target);
                Toast.makeText( context, "Deny request from " + target, 
                		Toast.LENGTH_SHORT ).show();
            }
        } );
        
        return v;
	}
}
