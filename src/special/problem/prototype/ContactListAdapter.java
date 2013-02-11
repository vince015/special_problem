package special.problem.prototype;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactListAdapter extends BaseAdapter {
	private static ArrayList<Contact> contactList;
	private LayoutInflater mInflater;
	
	public ContactListAdapter(Context context, ArrayList<Contact> contacts) {
		contactList= contacts;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return contactList.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return contactList.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder holder;
		
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.contact_row, null);
			
			holder = new Holder();
			holder.contactName = (TextView) convertView.findViewById(R.id.contactName);
			holder.contactNetID = (TextView) convertView.findViewById(R.id.contactNetID);
			holder.presenceMode = (ImageView) convertView.findViewById(R.id.presence_mode);
			
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.contactName.setText(contactList.get(position).getName());
		holder.contactNetID.setText(contactList.get(position).getNetID());
		
		// set image for presence mode
		String p = contactList.get(position).getPresence();
		if(p.equals("unavailable")) {
			holder.presenceMode.setImageResource(R.drawable.unavailable_mode);
		} else {
			holder.presenceMode.setImageResource(R.drawable.available_mode);
		} 
		return convertView;
	} 
	 
	static class Holder {
		TextView contactName;
		TextView contactNetID;
		ImageView presenceMode;
	}
}
