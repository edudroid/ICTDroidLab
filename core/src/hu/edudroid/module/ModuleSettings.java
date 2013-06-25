package hu.edudroid.module;

import hu.edudroid.ict.R;
import hu.edudroid.ict.RegisterActivity;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ModuleSettings extends Activity implements OnSeekBarChangeListener, OnCheckedChangeListener{

	
	private static final long MAXIMUM_CACHE_SIZE = 10485760L;  //10 MB
    private static final long MINIMUM_CACHE_SIZE = 102400L;  //100 KB
	
	private RadioGroup mGroup;
	private SeekBar mSeekBar;
	private TextView mProgressText;
	private SharedPreferences mPrefs;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_module_settings);
		mGroup = (RadioGroup)findViewById(R.id.radiog_network_type);
		mSeekBar = (SeekBar)findViewById(R.id.seek_file_size);
		mProgressText = (TextView) findViewById(R.id.txt_progress);
		mPrefs = getSharedPreferences(RegisterActivity.PREFS_NAME, Context.MODE_PRIVATE);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		setupElement();
	}
	
	
	public void setupElement() {
		mSeekBar.setMax((int)(MAXIMUM_CACHE_SIZE - MINIMUM_CACHE_SIZE));
		Log.e("MaxCachceSize", ModuleBase.getMaximumCacheSize(mPrefs) + "");
		mSeekBar.setOnSeekBarChangeListener(this);
		mSeekBar.setProgress((int)(ModuleBase.getMaximumCacheSize(mPrefs) - MINIMUM_CACHE_SIZE));
		
		if (ModuleBase.isOnlyWifiUpload(mPrefs))
			mGroup.check(R.id.radio0);
		else
			mGroup.check(R.id.radio1);
		mGroup.setOnCheckedChangeListener(this);
	}


	public void onProgressChanged(	SeekBar seekBar,
									int progress,
									boolean fromUser){
		Log.e("Progress", progress + " ");
		ModuleBase.setMaximumCacheSize(mPrefs, progress + MINIMUM_CACHE_SIZE);
		mProgressText.setText((progress + MINIMUM_CACHE_SIZE)/1024 + " KB");
	}


	public void onStartTrackingTouch(SeekBar seekBar){
		
	}


	public void onStopTrackingTouch(SeekBar seekBar){

	}

	public void onCheckedChanged(RadioGroup group, int checkedId){
		switch (checkedId){
			case R.id.radio0:
				ModuleBase.setUploadWithoutWifi(mPrefs, true);
				break;

			case R.id.radio1:
				ModuleBase.setUploadWithoutWifi(mPrefs, false);
				break;
		}
		
	}

}
