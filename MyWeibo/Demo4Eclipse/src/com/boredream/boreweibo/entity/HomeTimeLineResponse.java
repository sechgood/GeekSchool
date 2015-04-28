package com.boredream.boreweibo.entity;

import java.util.ArrayList;

public class HomeTimeLineResponse {

	private ArrayList<Status> statuses;
	private ArrayList<Ad> ad;
	private int total_number;

	public ArrayList<Status> getStatuses() {
		return statuses;
	}

	public void setStatuses(ArrayList<Status> statuses) {
		this.statuses = statuses;
	}

	public ArrayList<Ad> getAd() {
		return ad;
	}

	public void setAd(ArrayList<Ad> ad) {
		this.ad = ad;
	}

	public int getTotal_number() {
		return total_number;
	}

	public void setTotal_number(int total_number) {
		this.total_number = total_number;
	}

}
