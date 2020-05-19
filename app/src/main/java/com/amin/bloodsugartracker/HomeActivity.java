package com.amin.bloodsugartracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

// app icon shape src = http://clipart-library.com/clipart/BcaonGRc8.htm

public class HomeActivity extends AppCompatActivity
{
	private ImageView addEntryButton;

	private File f;
	private File ff[];
	private ArrayList<File> file_list;

	private FileInputStream fis;

	private RecyclerView rv;
	private RecyclerView.Adapter rva;
	private RecyclerView.LayoutManager rvlm;

	private String entryDetail = "";
	private String entryDetailProcessed[];

	private Double glucoselvl = 0.0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_list);

		addEntryButton = findViewById(R.id.add_entry);
		addEntryButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent newEntryIntent = new Intent(HomeActivity.this, NewEntryActivity.class);
				startActivity(newEntryIntent);
			}
		});

		file_list = new ArrayList<>();

		f = new File(getFilesDir().toString());
		ff = f.listFiles();

		Arrays.sort(ff, new Comparator<File>()
		{
			public int compare(File a, File b)
			{
				if(a.lastModified() < b.lastModified())
					return 1;
				else if(a.lastModified() > b.lastModified())
					return -1;
				else
					return 0;
			}
		});

		if(ff != null)
		{
			for(int i = 0; i < ff.length; ++i)
			{
				file_list.add(ff[i]);
			}
		}

		rv = (RecyclerView) findViewById(R.id.entry_list);
		rvlm = new LinearLayoutManager(this);
		rv.setLayoutManager(rvlm);

		rva = new la(file_list);
		rv.addItemDecoration(new DividerItemDecoration(HomeActivity.this, DividerItemDecoration.VERTICAL));
		rv.setAdapter(rva);
	}

	private class vh extends RecyclerView.ViewHolder
	{
		private TextView datetime;
		private TextView glucoselevel;
		private TextView status;
		private Button entrydeletebutton;

		public vh(LayoutInflater inf, ViewGroup parent)
		{
			super(inf.inflate(R.layout.list_item, parent, false));
			glucoselevel = itemView.findViewById(R.id.blood_glucose_level);
			status = itemView.findViewById(R.id.status_description);
			datetime = itemView.findViewById(R.id.date_time);
			entrydeletebutton = itemView.findViewById(R.id.delete_entry_button);
			entrydeletebutton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					new AlertDialog.Builder(HomeActivity.this)
							.setTitle("Confirm Deletion")
							.setMessage("Do you want to delete this entry?")
							.setNegativeButton(android.R.string.no, null)
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface arg00, int arg01)
								{
									int pos = getLayoutPosition();
									File fname = file_list.get(pos);

									fname.delete();
									Toast.makeText(HomeActivity.this, "Entry deleted.", Toast.LENGTH_SHORT).show();
									recreate();
								}
							}).create().show();
				}
			});
		}
	}

	public class la extends RecyclerView.Adapter<vh>
	{
		private ArrayList<File> items;

		public la(ArrayList<File> _items)
		{
			items = _items;
		}
		@Override
		public vh onCreateViewHolder(ViewGroup parent, int viewType)
		{
			LayoutInflater inf = LayoutInflater.from(parent.getContext());
			return new vh(inf, parent);
		}
		@Override
		public void onBindViewHolder(vh holder, int position)
		{
			try
			{
				fis = new FileInputStream(items.get(position).getAbsolutePath());
				int r = fis.read();
				StringBuilder builder = new StringBuilder();
				while(r != -1)
				{
					builder.append((char) r);
					r = fis.read();
				}
				fis.close();
				entryDetail = builder.toString();
				entryDetailProcessed = entryDetail.split("#");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			glucoselvl = Double.parseDouble(entryDetailProcessed[3]);

			holder.glucoselevel.setText("Blood Glucose Level: " + entryDetailProcessed[3] + " mmol/L");
			if(entryDetailProcessed[2].equals("FASTING"))
			{
				if(glucoselvl <= 7.0 && glucoselvl >= 4.0)
				{
					holder.glucoselevel.setTextColor(getResources().getColor(R.color.safe, getTheme()));
				}
				else
				{
					holder.glucoselevel.setTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));
				}
			}
//			else if(entryDetailProcessed[2].equals("BEFORE MEAL"))
//			{
//				if(glucoselvl <= 7.0 && glucoselvl >= 4.0)
//				{
//					holder.glucoselevel.setTextColor(getResources().getColor(R.color.safe, getTheme()));
//				}
//				else
//				{
//					holder.glucoselevel.setTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));
//				}
//			}
//			else if(entryDetailProcessed[2].equals("AFTER MEAL"))
//			{
//
//			}
			else if(entryDetailProcessed[2].equals("2 HOURS AFTER MEAL"))
			{
				if(glucoselvl < 8.5 && glucoselvl >= 4.0)
				{
					holder.glucoselevel.setTextColor(getResources().getColor(R.color.safe, getTheme()));
				}
				else
				{
					holder.glucoselevel.setTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));
				}
			}
			holder.status.setText("Condition: " + entryDetailProcessed[2]);
			holder.datetime.setText("Time: " + entryDetailProcessed[0] + " @ " + entryDetailProcessed[1]);
		}
		@Override
		public int getItemCount()
		{
			return items.size();
		}
	}

	public void onRestart()
	{
		super.onRestart();
		recreate();
	}
}