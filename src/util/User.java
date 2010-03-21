package util;

import java.sql.Date;

public class User {
	String username;
	int score;
	long time;

	public User(String username, int score, long time) {
		this.username = username;
		this.score = score;
		this.time = time;
	}

	public String timeToHMS() {
		long timeMillis = time / 1000;
		String seconds = Integer.toString((int) (timeMillis % 60));
		String minutes = Integer.toString((int) ((timeMillis % 3600) / 60));
		String hours = Integer.toString((int) (timeMillis / 3600));
		for (int i = 0; i < 2; i++) {
			if (seconds.length() < 2) {
				seconds = "0" + seconds;
			}
			if (minutes.length() < 2) {
				minutes = "0" + minutes;
			}
			if (hours.length() < 2) {
				hours = "0" + hours;
			}
		}
		return hours + ":" + minutes + ":" + seconds;
	}

	public String toString() {
		return username + " " + score + " " + timeToHMS();
	}
}
