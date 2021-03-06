/*
* Copyright (C) 2010 Mamadou Diop.
*
* Contact: Mamadou Diop <diopmamadou(at)doubango.org>
*	
* This file is part of imsdroid Project (http://code.google.com/p/imsdroid)
*
* imsdroid is free software: you can redistribute it and/or modify it under the terms of 
* the GNU General Public License as published by the Free Software Foundation, either version 3 
* of the License, or (at your option) any later version.
*	
* imsdroid is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
* See the GNU General Public License for more details.
*	
* You should have received a copy of the GNU General Public License along 
* with this program; if not, write to the Free Software Foundation, Inc., 
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*
*/

package org.doubango.imsdroid.Screens;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.doubango.imsdroid.IMSDroid;
import org.doubango.imsdroid.R;
import org.doubango.imsdroid.Model.Group;
import org.doubango.imsdroid.Model.HistoryEvent;
import org.doubango.imsdroid.Model.HistoryMsrpEvent;
import org.doubango.imsdroid.Model.HistorySMSEvent;
import org.doubango.imsdroid.Services.IContactService;
import org.doubango.imsdroid.Services.IHistoryService;
import org.doubango.imsdroid.Services.Impl.ServiceManager;
import org.doubango.imsdroid.events.HistoryEventArgs;
import org.doubango.imsdroid.events.IHistoryEventHandler;
import org.doubango.imsdroid.media.MediaType;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class ScreenHistory  extends Screen
implements IHistoryEventHandler 
{

	private final IHistoryService historytService;
	private final IContactService contactService;
	
	private static final int SELECT_CONTENT = 1;
	private String fixmeRemoteParty;
	
	private final Handler handler;
	
	private GridView gridView;
	private ScreenHistoryAdapter adapter;
	
	private final static int MENU_DELETE_EVENT = 1;
	private final static int MENU_DELETE_ALLEVENTS = 2;
	
	private final static int MENU_VOICE_CALL = 10;
	private final static int MENU_VIDEO_CALL = 11;
	//private final static int MENU_SEND_MESSAGE = 12;
	private final static int MENU_SEND_SMS = 13;
	private final static int MENU_SEND_FILE = 14;
	private final static int MENU_START_CHAT = 15;
	private final static int MENU_CONFERENCE = 16;
	private final static int MENU_SHARE_CONTENT = 17;
	
	public ScreenHistory() {
		super(SCREEN_TYPE.HISTORY_T, ScreenHistory.class.getCanonicalName());
		
		// services
		this.historytService = ServiceManager.getHistoryService();
		this.contactService = ServiceManager.getContactService();
		
		this.handler = new Handler();
	}
	
	/* ===================== Activity ======================== */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_history);
        
        // gridView
		this.adapter = new ScreenHistoryAdapter(this.historytService.getEvents());
		this.gridView = (GridView) this.findViewById(R.id.screen_history_gridView);
		this.gridView.setAdapter(this.adapter);
		this.registerForContextMenu(this.gridView);
		this.gridView.setOnItemClickListener(this.gridView_OnItemClickListener);
		
		// add event handler
		this.historytService.addHistoryEventHandler(this);
	}

	@Override
	protected void onResume() {

		if(this.historytService.isLoadingHistory()){
			Toast.makeText(this, "Loading history...", Toast.LENGTH_SHORT).show();
		}
		
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {

		// remove event handler
		this.historytService.removeHistoryEventHandler(this);
		
		super.onDestroy();
	}
	
	@Override
	public boolean createOptionsMenu(Menu menu){
		menu.add(0, ScreenHistory.MENU_DELETE_ALLEVENTS, 0, "Delete All Events").setIcon(R.drawable.delete_list_48);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
			case ScreenHistory.MENU_DELETE_EVENT:
				
				break;
			case ScreenHistory.MENU_DELETE_ALLEVENTS:
				AlertDialog.Builder builder = new AlertDialog.Builder(ScreenHistory.this);
				builder.setMessage("Are you sure you want to delete all events?")
				       .setCancelable(false)
				       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   ScreenHistory.this.historytService.clear();
				           }
				       })
				       .setNegativeButton("No", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                dialog.cancel();
				           }
				       });
				builder.create().show();
				break;
		}
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		menu.add(0, ScreenHistory.MENU_VOICE_CALL, Menu.NONE, "Make Voice Call");
		menu.add(0, ScreenHistory.MENU_VIDEO_CALL, Menu.NONE, "Make Video Call");
		//menu.add(0, ScreenHistory.MENU_SEND_MESSAGE, Menu.NONE, "Send Short Message");
		menu.add(0, ScreenHistory.MENU_SEND_SMS, Menu.NONE, "Send SMS");
		//menu.add(0, ScreenHistory.MENU_SEND_FILE, Menu.NONE, "Send File");
		//menu.add(0, ScreenHistory.MENU_START_CHAT, Menu.NONE, "Start Chat");
		menu.add(0, ScreenHistory.MENU_SHARE_CONTENT, Menu.NONE, "Share Content");
		menu.add(1, ScreenHistory.MENU_DELETE_EVENT, Menu.NONE, "Delete Event");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final HistoryEvent event;
		final int location = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
		if((event = this.adapter.getEvent(location)) == null){ /* should never happen ...but who know? */
			return super.onContextItemSelected(item);
		}
		
		switch(item.getItemId()){
			case ScreenHistory.MENU_VOICE_CALL:
				ScreenAV.makeCall(event.getRemoteParty(), MediaType.Audio);
				return true;
			case ScreenHistory.MENU_VIDEO_CALL:
				ScreenAV.makeCall(event.getRemoteParty(), MediaType.AudioVideo);
				return true;
			case ScreenHistory.MENU_SHARE_CONTENT:
				 this.fixmeRemoteParty = event.getRemoteParty();
				 Intent intent = new Intent();
				 intent.setType("*/*").addCategory(Intent.CATEGORY_OPENABLE).setAction(Intent.ACTION_GET_CONTENT);
				 startActivityForResult(Intent.createChooser(intent, "Select content"), ScreenHistory.SELECT_CONTENT);
				return true;
			case ScreenHistory.MENU_SEND_SMS:
				ScreenSMSCompose.sendSMS(event.getRemoteParty());
				return true;
			case ScreenHistory.MENU_SEND_FILE:
				Toast.makeText(this, "Send File: " + event.getRemoteParty(), Toast.LENGTH_SHORT).show();
				return true;
			case ScreenHistory.MENU_START_CHAT:
				Toast.makeText(this, "Start Chat: " + event.getRemoteParty(), Toast.LENGTH_SHORT).show();
				return true;
			case ScreenHistory.MENU_CONFERENCE:
				Toast.makeText(this, "Start Conference: " + event.getRemoteParty(), Toast.LENGTH_SHORT).show();
				return true;
				
				
			case ScreenHistory.MENU_DELETE_EVENT:
				AlertDialog.Builder builder = new AlertDialog.Builder(ScreenHistory.this);
				builder.setMessage("Are you sure you want to delete to delete this event?")
				       .setCancelable(false)
				       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   ScreenHistory.this.historytService.deleteEvent(location);
				           }
				       })
				       .setNegativeButton("No", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                dialog.cancel();
				           }
				       });
				builder.create().show();
				return true;
				
			default:
				return super.onContextItemSelected(item);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK) {
	        if (requestCode == ScreenHistory.SELECT_CONTENT && this.fixmeRemoteParty != null) {
	            Uri selectedContentUri = data.getData();
	            String selectedContentPath = this.getPath(selectedContentUri);
	            ScreenFileTransferView.ShareContent(this.fixmeRemoteParty, selectedContentPath, true);
	        }
	    }
	}
	
	private OnItemClickListener gridView_OnItemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			final HistoryEvent event = ScreenHistory.this.adapter.getEvent(position);
			if(event != null){
				if(event.getMediaType() == MediaType.SMS && event instanceof HistorySMSEvent){
					ScreenSMSView.showSMS((HistorySMSEvent)event);
				}
				else if(event.getMediaType() == MediaType.FileTransfer && event instanceof HistoryMsrpEvent){
					try{
						HistoryMsrpEvent msrpEvent = (HistoryMsrpEvent)event;
						Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW);
						myIntent.setDataAndType(Uri.fromFile(new File(msrpEvent.getFilePath())), "*/*");
						startActivity(myIntent); 
					}
					catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(IMSDroid.getContext(), String.format("Failed to open the file"), Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	};
	
	/* ===================== IScreen (Screen) ======================== */
	@Override
	public boolean haveMenu(){
		return true;
	}
	
	
	/* ===================== IHistoryEventHandler ======================== */
	@Override
	public boolean onHistoryEvent(Object sender, HistoryEventArgs e) {
		// already on its own thread
		switch(e.getType()){
			default:
				this.handler.post(new Runnable(){
					public void run() {
						ScreenHistory.this.adapter.update(ScreenHistory.this.historytService.getEvents());
					}
				});
				break;
		}
		return true;
	}
	
	
	
	
	
	/* ===================== Adapter ======================== */

	private class ScreenHistoryAdapter extends BaseAdapter {
		private List<HistoryEvent> items;
		private final SimpleDateFormat dateFormat;
		private final SimpleDateFormat durationFormat;
		
		private ScreenHistoryAdapter(List<HistoryEvent> items) {
			this.items = items;
			this.dateFormat = new SimpleDateFormat("yyyy MMM dd hh:mm aaa");
			//this.durationFormat = new SimpleDateFormat("hh:mm:ss");
			this.durationFormat = new SimpleDateFormat("mm:ss");
		}

		private synchronized void update(List<HistoryEvent> items){
			this.items = items;
			this.notifyDataSetChanged();
		}
		
		public int getCount() {
			return this.items == null ? 0 : this.items.size();
		}

		public Object getItem(int position) {
			return (this.items.size() >position ? this.items.get(position) : null);
		}
		
		public HistoryEvent getEvent(int position){
			if(this.getCount() > position){
				return this.items.get(position);
			}
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			HistoryEvent event;

			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.screen_history_item, null);
			}
			if ((event = this.items.get(position)) == null) {
				return view;
			}

			ImageView imageView = (ImageView) view.findViewById(R.id.screen_history_item_imageView);
			TextView tvRemoteUri = (TextView) view.findViewById(R.id.screen_history_item_textView_remote);
			TextView tvInfo = (TextView) view.findViewById(R.id.screen_history_item_textView_info);
			
			final String date = this.dateFormat.format(new Date(event.getStartTime()));
			
			switch(event.getMediaType()){
				case Audio:
				case AudioVideo:
					{
						final String duration = this.durationFormat.format(new Date(event.getEndTime() - event.getStartTime()));
						tvInfo.setText(String.format("%s (%s)", date, duration));
						switch(event.getStatus()){
							case Outgoing:
								imageView.setImageResource(R.drawable.call_outgoing_45);
								break;
							case Incoming:
								imageView.setImageResource(R.drawable.call_incoming_45);
								break;
							case Failed:
							case Missed:
								imageView.setImageResource(R.drawable.call_missed_45);
								break;
						}
						break;
					}
				case SMS:
					{
						tvInfo.setText(String.format("%s", date));
						switch(event.getStatus()){
							case Outgoing:
								imageView.setImageResource(R.drawable.sms_out_45);
								break;
							case Incoming:
								imageView.setImageResource(R.drawable.sms_into_45);
								break;
							case Failed:
							case Missed:
								imageView.setImageResource(R.drawable.sms_error_45);
								break;
						}
						break;
					}
				case FileTransfer:
					{
						boolean isOK = false;
						final HistoryMsrpEvent eventFile = (HistoryMsrpEvent)event;
						final String filePath = eventFile.getFilePath();
						final File file;
						if(filePath != null && (file = new File(filePath)).exists()){
							isOK = true;
							tvInfo.setText(String.format("%s (%s)", date, file.getName()));
						}
						else{
							tvInfo.setText("Error: File does not exist");
						}
						
						switch(event.getStatus()){
							case Outgoing:
								imageView.setImageResource(isOK ? R.drawable.document_up_48 : R.drawable.document_error_48);
								break;
							case Incoming:
								imageView.setImageResource(isOK ? R.drawable.document_down_48 : R.drawable.document_error_48);
								break;
							case Failed:
							case Missed:
								imageView.setImageResource(R.drawable.document_error_48);
								break;
						}
						break;
					}
			}
			
			final String remoteParty = event.getRemoteParty();
			if(remoteParty != null){
				final Group.Contact contact = ScreenHistory.this.contactService.getContact(remoteParty);
				if(contact != null && contact.getDisplayName() != null){
					tvRemoteUri.setText(contact.getDisplayName());
				}
				else{
					tvRemoteUri.setText(event.getRemoteParty());
				}
			}
			

			return view;
		}
	}
}
