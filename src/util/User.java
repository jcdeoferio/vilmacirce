package util;

import java.sql.Date;

public class User implements Comparable<User> {
	public String username;
	public int score;
	public long time;

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
		return username + " " + score + " " + time;
	}

	@Override
	public int compareTo(User u) {
		if (u.score - score == 0)
			return (int)(time - u.time);
		return u.score - score;
	}
}
