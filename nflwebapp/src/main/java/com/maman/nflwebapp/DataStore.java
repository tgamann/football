package com.maman.nflwebapp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class DataStore {
	private static DataStore instance = null;
	private static JdbcTemplate mJdbcTemplate = null;
	
	protected DataStore() { /* Exists only to defeat instantiation */ }

	public static DataStore getInstance(String host) {
		if(instance == null) {
			instance = new DataStore();
	         
	        // simple DS for test (not for production!)
			SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
			dataSource.setDriverClass(com.mysql.jdbc.Driver.class);

			String port = null;
			String username = null;
			String password = null;
	         
			if (host.contains("localhost")) {
				port = "3306";
				username = "root";
				password = "root";	        	 
			}
			else {
				host = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
				port = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
				username = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
				password = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");	        	 
			}
			dataSource.setUsername(username);
			dataSource.setPassword(password);
			// Apache Listens for request to deliver web pages to a browser on port 8080; config file for apache is httpd.conf.
			// MySQL Listens for request to query databases on port 3306; config file for mysql is my.ini where you will see 3306.
			String url = String.format("jdbc:mysql://%s:%s/nfl", host, port);
			dataSource.setUrl(url);

			mJdbcTemplate = new JdbcTemplate(dataSource);
		}
		return instance;
	}

	public List<Integer> getWeeks() {
		List<Integer> weeks = new ArrayList<Integer>();
		String sql = "SELECT week FROM games GROUP BY week"; 
		SqlRowSet row = mJdbcTemplate.queryForRowSet(sql);
	    while (row.next()) {
	    	weeks.add(row.getInt("week"));
	    }
		return weeks;
	}
	
	public List<Game> getSchedule(int week) {
		List<Game> schedule = new ArrayList<Game>();
		String sql = "SELECT hometeam, visitingteam, winner FROM games WHERE week=" 
				+ Integer.toString(week);
		SqlRowSet row = mJdbcTemplate.queryForRowSet(sql);
	    while (row.next()) {
	    	Game game = new Game(week,
	    			row.getString("hometeam"),
	    			row.getString("visitingteam"),
	    			row.getString("winner"));
	    	schedule.add(game);
	    }
		return schedule;
	}
	
	public List<String> getTeams() {
		String sql = "SELECT nickname FROM teams";
		List<String>teams = mJdbcTemplate.queryForList(sql, String.class);
		return teams;
	}
	
//  for (String team : teams) {
//	sql = "UPDATE teams SET W=" + wins.get(team) 
//			+ ", L=" + losses.get(team) + ", T=" + ties.get(team) 
//			+ " WHERE nickname='"+team+"'";
//	int rowsUpdated = mJdbcTemplate.update(sql);

}
