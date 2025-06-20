package br.com.mythe.droid.gelib.events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.jredfoot.sophielib.view.DateTimePicker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.mythe.droid.gelib.R;

public class EditEvent extends Activity {
	private static final int trackingStringID = R.string.toolbarTracking;
	private static final int notTrackingStringID = R.string.toolbarNotTracking;
	private static final int previousEventTextID = R.string.previousActivityText;
	private static final int previousEventDefaultID = R.string.previousActivityDefault;
	
	private EventDbAdapter mDbHelper;
	private EventEntry currentEvent;
	private EventEntry previousEvent;
	
	private ArrayList<String> autoCompleteActivities=new ArrayList<String>();
	private ArrayList<String> autoCompleteLocations=new ArrayList<String>();

	private ArrayAdapter<String> adapterActivities;
	private ArrayAdapter<String> adapterLocations;
	private AutoCompleteTextView editTextEventName;
	private AutoCompleteTextView editTextEventLocation;
	private Button previousActivityBar;
	private Button setStartDateButton;
	private Button setStopDateButton;
	private TextView textViewStartTime;
	private TextView textViewIsTracking;


	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.event_main);
		
		//ViewStub v =(ViewStub) findViewById(R.id.content_view);
		//v.setLayoutResource(R.layout.edit_event);
		//v.inflate();
		
		setContentView(R.layout.edit_event);

		mDbHelper = new EventDbAdapter(this);
		mDbHelper.open();
		
		//initializeToolbar();
		initializeEditTexts();
		initializeAutoComplete();
		
		textViewStartTime = (TextView) findViewById(R.id.startTime);
		previousActivityBar = (Button) findViewById(R.id.previous_activity_bar);
		initializeActivityButtons();
	}
	
	/**
	 * Initializes the NextActivity and StopTracking buttons.
	 */
	private void initializeActivityButtons() {
		setStartDateButton=(Button)findViewById(R.id.setStartDate);
		setStopDateButton=(Button)findViewById(R.id.setEndDate);
		
		setStartDateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showStartDateTimeDialog();
			}
		});
		
		setStopDateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showEndDateTimeDialog();
			}
		});
	}
	
	/**
	 * Initializes the AutoCompleteTextViews and intializes references to related views.
	 */
	private void initializeEditTexts() {
		editTextEventName = (AutoCompleteTextView) findViewById(R.id.editEventName);
		editTextEventName.setInputType(0);
		editTextEventLocation = (AutoCompleteTextView) findViewById(R.id.editLocation);
		editTextEventLocation.setInputType(0);
		
		adapterActivities = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, autoCompleteActivities);
		adapterLocations = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, autoCompleteLocations);

		editTextEventName.setAdapter(adapterActivities);
		editTextEventLocation.setAdapter(adapterLocations);
		
		editTextEventName.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (currentEvent == null) {
					currentEvent = new EventEntry();
					updateUI();
				}
				currentEvent.mName = editTextEventName.getText().toString();
				return false;
			}
		});
		editTextEventLocation.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (currentEvent == null) {
					currentEvent = new EventEntry();
					updateUI();
				}
				currentEvent.mLocation = editTextEventLocation.getText().toString();
				return false;
			}
		});
	}
	
	/**
	 * Initializes the toolbar onClickListeners and intializes references to toolbar views.
	 */
	private void initializeToolbar() {		
		textViewIsTracking = (TextView) findViewById(R.id.toolbar_center);
		((ImageView) findViewById(R.id.toolbar_left_option)).setImageResource(R.drawable.list);

		findViewById(R.id.toolbar_right_option).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent settingsIntent = new Intent(EditEvent.this, Settings.class);
				settingsIntent.putExtra(getString(R.string.isTracking), isTracking());
				startActivity(settingsIntent);
			}
		});

		findViewById(R.id.toolbar_left_option).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent listIntent = new Intent(EditEvent.this, ListEvents.class);
				listIntent.putExtra(getString(R.string.isTracking), isTracking());
				startActivity(listIntent);
			}
		});
	}

	/**
	 * Updates the UI using the currentEvent and previousEvent.
	 */
	private void updateUI() {
		//updateTrackingUI();
		fillViewWithEventInfo();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		List<EventEntry> events = getLatestEvents(2);
		if (events.size() != 0) {
			EventEntry event = events.remove(0);
			if (event.mEndTime != 0) {
				// We aren't tracking
				previousEvent = event;
			} else {
				// We are tracking
				currentEvent = event;
				previousEvent = events.size() != 0 ? events.remove(0) : null;
			}
		}
		initializeAutoComplete();
		updateUI();
	}
	
	/**
	 * Fills the text entries and views with the correct info based on the
	 * current/previous events.
	 */
	private void fillViewWithEventInfo() {
		if (currentEvent != null) {
			editTextEventName.setText(currentEvent.mName != null ? currentEvent.mName : "");
			editTextEventLocation.setText(currentEvent.mLocation != null ? currentEvent.mLocation : "");
			// TODO make this not in ListEvents
			textViewStartTime.setText(ListEvents.getDateString(currentEvent.mStartTime));
		} else {
			editTextEventName.setText("");
			editTextEventLocation.setText("");
			textViewStartTime.setText("");
		}
		previousActivityBar.setText(getPreviousEventString());
	}

	/**
	 * @return The text that the previous event bar should have, based on the previousEvent.
	 */
	private String getPreviousEventString() {
		String previousActivityLabel = getString(previousEventTextID);
		String previousEventString = previousEvent != null ? previousEvent.mName : getString(previousEventDefaultID); 
		return previousActivityLabel + " " + previousEventString;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		updateAutoComplete();
		if (!updateDatabase(currentEvent))
			throw new RuntimeException("Ahh it didn't update!"); // TODO remove
	}
	
	/**
	 * Changes the appearance of this activity to reflect the fact that this activity is now tracking
	 */
	private void updateTrackingUI(){
		boolean isTracking = isTracking();
		textViewIsTracking.setText(isTracking ? trackingStringID : notTrackingStringID);
		setStartDateButton.setEnabled(isTracking);
		setStopDateButton.setEnabled(isTracking);	
	}
	
	

	/**
	 * Creates a database entry for the EventEntry that is in progress
	 * Updates the EventEntry with its corresponding rowID for the database
	 * @param entry- the entry of the activity thats in progress
	 * @return false if an error occurred, otherwise true. 
	 */
	private boolean updateDatabase(EventEntry event) {
		if (event == null)
			return true;
		else if(event.mDbRowID==-1) {
			event.mDbRowID = mDbHelper.createEvent(event.mName, event.mLocation, event.mStartTime, event.mEndTime);
			return event.mDbRowID != -1;
		} else {
			return mDbHelper.updateEvent(event.mDbRowID, event.mName, 
					event.mLocation, event.mStartTime,event.mEndTime);
		}
	}
	
	/**
	 * Queries the database for the events in sorted order.
	 * @param numEvents The number of events to return.
	 * @return  A list of EventEntry objects.
	 */
	private List<EventEntry> getLatestEvents(int numEvents){
		List<EventEntry> eventList=new LinkedList<EventEntry>();
		Cursor sortedEvents=mDbHelper.fetchSortedEvents();
		if (sortedEvents.getCount() > 0) {
		     while (sortedEvents.moveToNext() && numEvents > 0) {
		    	 long dbRowID =		getLong(sortedEvents, EventDbAdapter.KEY_ROWID);
		 		 String name =		getString(sortedEvents, EventDbAdapter.KEY_NAME);
		 		 String location =	getString(sortedEvents, EventDbAdapter.KEY_LOCATION);
		 		 long startTime =	getLong(sortedEvents, EventDbAdapter.KEY_START_TIME);
		 		 long endTime =		getLong(sortedEvents, EventDbAdapter.KEY_END_TIME);
		 		 eventList.add(new EventEntry(dbRowID, name, location, startTime, endTime));
		    	 numEvents--;
		     }
	        }
		return eventList;
	}
	
	/**
	 * @param cursor A cursor at a particular row.
	 * @param columnName The name of the column in the DB.
	 * @return The Long at the column with the given name, or null
	 * 		   it doesn't exist.
	 */
	private long getLong(Cursor cursor, String columnName) {
		return cursor.getLong(cursor.getColumnIndex(columnName));
	}
	
	/**
	 * 
	 * @param cursor A cursor at a particular row.
	 * @param columnName The name of the column in the DB.
	 * @return The String at the colum with the given name.
	 */
	private String getString(Cursor cursor, String columnName) {
		return cursor.getString(cursor.getColumnIndex(columnName));
	}
	
	private void updateAutoComplete(){
		String activity=editTextEventName.getText().toString();
		String location=editTextEventLocation.getText().toString();
		if(!mDbHelper.getEvents().contains(activity))
			adapterActivities.add(activity);
		if(!mDbHelper.getLocations().contains(location))
			adapterLocations.add(location);
	}
	
	/**
	 * @return Whether or not an activity is currently being tracked.
	 */
	private boolean isTracking() {
		return currentEvent != null;
	}

	private void initializeAutoComplete(){

		adapterActivities.clear();
		adapterLocations.clear();
		for(String event: mDbHelper.getEvents()){
			
			adapterActivities.add(event);
		}
		for(String location: mDbHelper.getLocations()){
			adapterLocations.add(location);
		}

	}
	
	/**
	 * A local, in-memory version of a Event database entry. This is pushed and pulled
	 * from the database when necessary.  
	 * 
	 * @author AlexD
	 *
	 */
	private class EventEntry {
		long mDbRowID=-1;
		String mName="";
		String mLocation="";
		long mStartTime;
		long mEndTime;
		
		public EventEntry() {
			mStartTime = System.currentTimeMillis();
		}
		
		public EventEntry(long dbRowID, String name, String location, long startTime, long endTime){
			 this.mDbRowID = dbRowID;
			 this.mName=name;
			 this.mLocation=location;
			 this.mStartTime=startTime;
			 this.mEndTime=endTime;
		}
	}
	
	
	
	private void showStartDateTimeDialog() {
        // Create the dialog
        final Dialog mDateTimeDialog = new Dialog(this);
        // Inflate the root layout
        final RelativeLayout mDateTimeDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.date_time_dialog, null);
        // Grab widget instance
        final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView.findViewById(R.id.DateTimePicker);
        // Check is system is set to use 24h time (this doesn't seem to work as expected though)
        final String timeS = android.provider.Settings.System.getString(getContentResolver(), android.provider.Settings.System.TIME_12_24);
        final boolean is24h = !(timeS == null || timeS.equals("12"));
        
        // Update demo TextViews when the "OK" button is clicked 
        ((Button) mDateTimeDialogView.findViewById(R.id.SetDateTime)).setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                        mDateTimePicker.clearFocus();
                        // TODO Auto-generated method stub
                        String dia = "00" + mDateTimePicker.get(Calendar.DAY_OF_MONTH);                     
                        String mes = "00" + (mDateTimePicker.get(Calendar.MONTH)+1);
                        ((TextView) findViewById(R.id.startDate)).setText( dia.substring(dia.length() -2 , dia.length()) + "/" + mes.substring(mes.length() -2 , mes.length())  + "/" +  mDateTimePicker.get(Calendar.YEAR) );
                        if (mDateTimePicker.is24HourView()) {
                                ((TextView) findViewById(R.id.startTime)).setText(mDateTimePicker.get(Calendar.HOUR_OF_DAY) + ":" + mDateTimePicker.get(Calendar.MINUTE));
                        } else {
                                ((TextView) findViewById(R.id.startTime)).setText(mDateTimePicker.get(Calendar.HOUR) + ":" + mDateTimePicker.get(Calendar.MINUTE) + " "
                                                + (mDateTimePicker.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM"));
                        }
                        mDateTimeDialog.dismiss();
                }
        });

        // Cancel the dialog when the "Cancel" button is clicked
        ((Button) mDateTimeDialogView.findViewById(R.id.CancelDialog)).setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                        // TODO Auto-generated method stub
                        mDateTimeDialog.cancel();
                }
        });

        // Reset Date and Time pickers when the "Reset" button is clicked
        ((Button) mDateTimeDialogView.findViewById(R.id.ResetDateTime)).setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                        // TODO Auto-generated method stub
                        mDateTimePicker.reset();
                }
        });
        
        // Setup TimePicker
        mDateTimePicker.setIs24HourView(is24h);
        // No title on the dialog window
        mDateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Set the dialog content view
        mDateTimeDialog.setContentView(mDateTimeDialogView);
        // Display the dialog
        mDateTimeDialog.show();
	}
	
	
	private void showEndDateTimeDialog() {
        // Create the dialog
        final Dialog mDateTimeDialog = new Dialog(this);
        // Inflate the root layout
        final RelativeLayout mDateTimeDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.date_time_dialog, null);
        // Grab widget instance
        final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView.findViewById(R.id.DateTimePicker);
        // Check is system is set to use 24h time (this doesn't seem to work as expected though)
        final String timeS = android.provider.Settings.System.getString(getContentResolver(), android.provider.Settings.System.TIME_12_24);
        final boolean is24h = !(timeS == null || timeS.equals("12"));
        
        // Update demo TextViews when the "OK" button is clicked 
        ((Button) mDateTimeDialogView.findViewById(R.id.SetDateTime)).setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                        mDateTimePicker.clearFocus();
                        // TODO Auto-generated method stub
                        String dia = "00" + mDateTimePicker.get(Calendar.DAY_OF_MONTH);                     
                        String mes = "00" + (mDateTimePicker.get(Calendar.MONTH)+1);
                        ((TextView) findViewById(R.id.stopDate)).setText( dia.substring(dia.length() -2 , dia.length()) + "/" + mes.substring(mes.length() -2 , mes.length())  + "/" +  mDateTimePicker.get(Calendar.YEAR) );
                        if (mDateTimePicker.is24HourView()) {
                                ((TextView) findViewById(R.id.stopTime)).setText(mDateTimePicker.get(Calendar.HOUR_OF_DAY) + ":" + mDateTimePicker.get(Calendar.MINUTE));
                        } else {
                                ((TextView) findViewById(R.id.stopTime)).setText(mDateTimePicker.get(Calendar.HOUR) + ":" + mDateTimePicker.get(Calendar.MINUTE) + " "
                                                + (mDateTimePicker.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM"));
                        }
                        mDateTimeDialog.dismiss();
                }
        });

        // Cancel the dialog when the "Cancel" button is clicked
        ((Button) mDateTimeDialogView.findViewById(R.id.CancelDialog)).setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                        // TODO Auto-generated method stub
                        mDateTimeDialog.cancel();
                }
        });

        // Reset Date and Time pickers when the "Reset" button is clicked
        ((Button) mDateTimeDialogView.findViewById(R.id.ResetDateTime)).setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                        // TODO Auto-generated method stub
                        mDateTimePicker.reset();
                }
        });
        
        // Setup TimePicker
        mDateTimePicker.setIs24HourView(is24h);
        // No title on the dialog window
        mDateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Set the dialog content view
        mDateTimeDialog.setContentView(mDateTimeDialogView);
        // Display the dialog
        mDateTimeDialog.show();
	}
}