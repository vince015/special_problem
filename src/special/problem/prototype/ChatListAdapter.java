package special.problem.prototype;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatListAdapter extends BaseAdapter {
	private ArrayList<String> messages;
	private LayoutInflater mInflater;
	private final Context context;
	
	public ChatListAdapter(Context context, ArrayList<String> messages) {
		this.context = context;
		this.messages = messages;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return messages.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return messages.get(position);
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
        View v = mInflater.inflate(R.layout.chat_row, parent, false );
        String msg[] = getItem(position).toString().split(":");
        
        TextView msgFrom = (TextView)v.findViewById(R.id.msg_from);
        msgFrom.setText(msg[0]);
        
        TextView msgBody = (TextView)v.findViewById(R.id.msg_body);
        msgBody.setText(msg[1]);
                
        return v;
	}
}
