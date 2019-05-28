package com.kv.callrecorder.Utility;


import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

public class Utilities {

	public static String getAudioDuration(Context context, long length, String path) {
		String time = null;
		String time_dur = "0 sec";

		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		if (length > 10) {
			try {
				mmr.setDataSource(context, Uri.parse(path));
				time = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mmr.release();
			}

			if (time != null) {
				long timeInmillisec = Long.parseLong(time);
				long duration = timeInmillisec / 1000;
				long hours = duration / 3600;
				long minutes = (duration - hours * 3600) / 60;
				long seconds = duration - (hours * 3600 + minutes * 60);

				if (seconds > 0) {
					time_dur = String.valueOf(seconds) + " sec ";
				}
				if (minutes > 0) {
					time_dur = String.valueOf(minutes) + " min " + time_dur;
				}
				if (hours > 0) {
					time_dur = String.valueOf(hours) + " hr " + time_dur;
				}
			}
		}
		return time_dur;
	}

	/**
	 * Function to convert milliseconds time to
	 * Timer Format
	 * Hours:Minutes:Seconds
	 * */
	public String milliSecondsToTimer(long milliseconds){
		String finalTimerString = "";
		String secondsString = "";
		
		// Convert total duration into time
		   int hours = (int)( milliseconds / (1000*60*60));
		   int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
		   int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
		   // Add hours if there
		   if(hours > 0){
			   finalTimerString = hours + ":";
		   }
		   
		   // Prepending 0 to seconds if it is one digit
		   if(seconds < 10){ 
			   secondsString = "0" + seconds;
		   }else{
			   secondsString = "" + seconds;}
		   
		   finalTimerString = finalTimerString + minutes + ":" + secondsString;
		
		// return timer string
		return finalTimerString;
	}
	
	/**
	 * Function to get Progress percentage
	 * @param currentDuration
	 * @param totalDuration
	 * */
	public int getProgressPercentage(long currentDuration, long totalDuration){
		Double percentage;
		
		long currentSeconds = (int) (currentDuration / 1000);
		long totalSeconds = (int) (totalDuration / 1000);
		
		// calculating percentage
		percentage =(((double)currentSeconds)/totalSeconds)*100;
		
		// return percentage
		return percentage.intValue();
	}

	/**
	 * Function to change progress to timer
	 * @param progress - 
	 * @param totalDuration
	 * returns current duration in milliseconds
	 * */
	public int progressToTimer(int progress, int totalDuration) {
		int currentDuration = 0;
		totalDuration = (int) (totalDuration / 1000);
		currentDuration = (int) ((((double)progress) / 100) * totalDuration);
		
		// return current duration in milliseconds
		return currentDuration * 1000;
	}
}
