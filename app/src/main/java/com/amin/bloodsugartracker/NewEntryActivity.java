package com.amin.bloodsugartracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class NewEntryActivity extends AppCompatActivity
{
	private ImageView discardEntryButton;
	private Button saveEntryButton;

	private DatePicker entryDate;
	private int entryYear;
	private int entryMonth;
	private final String months[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	private int entryDay;
	private String date;

	private TimePicker entryTime;
	private String time;
	private int entryHour;
	private int entryMinute;
	private String ampmIndicator;

	//private CheckBox entryFasting;
	private RadioButton measurementCondition;
	private String statusDesc = "NONE";

	private EditText entryGlucoseLevel;

	private File filename;
	private FileOutputStream fos;

	private String dateCreatedString;
	private Date dateCreated;
	private long dateCreatedFinal = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bloodsugar_entry);

		entryDate = findViewById(R.id.date_entry);
		entryDate.setMaxDate(new Date().getTime());

		entryTime = findViewById(R.id.time_entry);
		//entryFasting = findViewById(R.id.fasting_confirmation_entry);
		entryGlucoseLevel = findViewById(R.id.blood_glucose_level_entry);

		entryYear = entryDate.getYear();
		entryMonth = entryDate.getMonth();
		entryDay = entryDate.getDayOfMonth();

		entryDate.init(entryYear, entryMonth, entryDay, new DatePicker.OnDateChangedListener()
		{
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
			{
				entryYear = year;
				entryMonth = monthOfYear;
				entryDay = dayOfMonth;
				date = entryDay + " " + months[entryMonth] + ", " + entryYear;
			}
		});

		date = entryDay + " " + months[entryMonth] + ", " + entryYear;

		entryHour = entryTime.getHour();
		entryMinute = entryTime.getMinute();

		entryTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener()
		{
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
			{
				entryHour = hourOfDay;
				entryMinute = minute;
				if(entryHour > 12)
				{
					ampmIndicator = "PM";
					entryHour -= 12;
					if(entryHour < 0)
					{
						entryHour = -(entryHour);
					}
				}
				else
				{
					ampmIndicator = "AM";
				}

				if(entryMinute < 10)
				{
					time = entryHour + ":0" + entryMinute + " " + ampmIndicator;
				}
				else
				{
					time = entryHour + ":" + entryMinute + " " + ampmIndicator;
				}
			}
		});

		if(entryHour > 12)
		{
			ampmIndicator = "PM";
			entryHour -= 12;
			if(entryHour < 0)
			{
				entryHour = -(entryHour);
			}
		}
		else
		{
			ampmIndicator = "AM";
		}

		if(entryMinute < 10)
		{
			time = entryHour + ":0" + entryMinute + " " + ampmIndicator;
		}
		else
		{
			time = entryHour + ":" + entryMinute + " " + ampmIndicator;
		}

//		dateCreatedString = entryYear + "/" + entryMonth + "/" + entryDay + " " + entryTime.getHour() + ":" + entryMinute;
//		SimpleDateFormat s = new SimpleDateFormat("yyyy/MM/dd HH:mm");
//		try
//		{
//			dateCreated = s.parse(dateCreatedString);
//			dateCreatedFinal = dateCreated.getTime();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}


//		entryFasting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//		{
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//			{
//				if(isChecked)
//				{
//					fasting = "YES";
//				}
//				else
//				{
//					fasting = "NO";
//				}
//			}
//		});

		discardEntryButton = findViewById(R.id.discard_entry);
		discardEntryButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onBackPressed();
			}
		});

		saveEntryButton = findViewById(R.id.save_entry);
		saveEntryButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(!(entryGlucoseLevel.getText().toString().equals("")) && !(statusDesc.equals("NONE")))
				{
					dateCreatedString = entryYear + "/" + entryMonth + "/" + entryDay + " " + entryTime.getHour() + ":" + entryMinute;
					SimpleDateFormat s = new SimpleDateFormat("yyyy/MM/dd HH:mm");
					try
					{
						dateCreated = s.parse(dateCreatedString);
						dateCreatedFinal = dateCreated.getTime();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					new AlertDialog.Builder(NewEntryActivity.this)
							.setTitle("Confirm Entry")
							.setMessage("Entry Date: " + date + "\nEntry Time: " + time + "\nCondition: " + statusDesc + "\nBlood Glucose Level: " + Double.parseDouble(entryGlucoseLevel.getText().toString()) + " mmol/L")
							.setNegativeButton(android.R.string.no, null)
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									filename = new File(getFilesDir(), UUID.randomUUID().toString());
									try
									{
										fos = openFileOutput(filename.getName(), Context.MODE_PRIVATE);
										fos.write((date + "#" + time + "#" + statusDesc + "#" + Double.parseDouble(entryGlucoseLevel.getText().toString())).getBytes());
										fos.close();
										filename.setLastModified(dateCreatedFinal);
										Toast.makeText(NewEntryActivity.this, "Entry has been created.", Toast.LENGTH_SHORT).show();
										startActivity(new Intent(NewEntryActivity.this, HomeActivity.class));
										finish();
									}
									catch(Exception e)
									{
										e.printStackTrace();
									}
								}
							})
							.create().show();
				}
				else
				{
					new AlertDialog.Builder(NewEntryActivity.this)
							.setTitle("Missing Information")
							.setMessage("Please review entry and fill in any missing fields.")
							.setPositiveButton("OK", null)
							.create().show();
				}
			}
		});
	}

	public void onSelected(View v)
	{
		boolean selected = ((RadioButton)v).isChecked();
		switch(v.getId())
		{
			case(R.id.in_fasting):
				if(selected)
				{
					statusDesc = "FASTING";
				}
				break;
//			case(R.id.in_before):
//				if(selected)
//				{
//					statusDesc = "BEFORE MEAL";
//				}
//				break;
//			case(R.id.in_after):
//				if(selected)
//				{
//					statusDesc = "AFTER MEAL";
//				}
//				break;
			case(R.id.in_twosafter):
				if(selected)
				{
					statusDesc = "2 HOURS AFTER MEAL";
				}
				break;
		}
	}

	@Override
	public void onBackPressed()
	{
		new AlertDialog.Builder(NewEntryActivity.this)
				.setTitle("Discard Entry").setMessage("Do you wish to discard this entry?")
				.setNegativeButton(android.R.string.no, null)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface arg00, int arg01)
					{
						NewEntryActivity.super.onBackPressed();
					}
				}).create().show();
	}
}