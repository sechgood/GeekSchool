package com.boredream.boreweibo.entity.response;

import java.util.List;

import com.boredream.boreweibo.entity.Comments;

public class CommentResponse {
	private List<Comments> comments;
	private int total_number;

	public List<Comments> getComments() {
		return comments;
	}

	public void setComments(List<Comments> comments) {
		this.comments = comments;
	}

	public int getTotal_number() {
		return total_number;
	}

	public void setTotal_number(int total_number) {
		this.total_number = total_number;
	}

}