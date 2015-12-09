package com.maman.nflwebapp;

public class Game {
	public int week;
	public String hometeam;
	public String visitingteam;
	public String  winner;
	
	public Game(int week, String home, String visitor, String winner) {
		this.week = week;
		this.hometeam = home;
		this.visitingteam = visitor;
		this.winner = winner;
	}
}
