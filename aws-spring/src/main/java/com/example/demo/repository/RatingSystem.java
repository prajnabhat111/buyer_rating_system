package com.example.demo.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName ="Rating")
public class RatingSystem {
	
	private String customerId;
	private double rating;
	private double amountBought;
	private double amountCancelled;
	private double amountReturned;
	private double fine;
	private double amountShipped;
	private double reward;
	private boolean returnPolicy;
	private boolean blackListed;

	public boolean isReturnPolicy() {
		return returnPolicy;
	}

	public void setReturnPolicy(boolean returnPolicy) {
		this.returnPolicy = returnPolicy;
	}

	public boolean isBlackListed() {
		return blackListed;
	}

	public void setBlackListed(boolean blackListed) {
		this.blackListed = blackListed;
	}

	@DynamoDBHashKey(attributeName ="customerId")
	public String getcustomerId() {
		return customerId;
	}

	public void setcustomerId(String customerId) {
		this.customerId = customerId;
	}

	@DynamoDBAttribute
	public double getrating() {
		return rating;
	}

	public void setrating(double rating) {
		this.rating =rating;
	}
	
	@DynamoDBAttribute
	public double getamountBought() {
		return amountBought;
	}

	public void setamountBought(double amountBought) {
		this.amountBought =amountBought;
	}
	@DynamoDBAttribute
	public double getamountCancelled() {
		return amountCancelled;
	}

	public void setamountCancelled(double amountCancelled) {
		this.amountCancelled =amountCancelled;
	}
	@DynamoDBAttribute
	public double getamountReturned() {
		return amountReturned;
	}

	public void setamountReturned(double amountReturned) {
		this.amountReturned=amountReturned;
	}
	@DynamoDBAttribute
	public double getfine() {
		return fine;
	}

	public void setfine(double fine) {
		this.fine =fine;
	}

	public double getAmountShipped() {
		return amountShipped;
	}

	public void setAmountShipped(double amountShipped) {
		this.amountShipped = amountShipped;
	}

	public double getReward() {
		return reward;
	}

	public void setReward(double reward) {
		this.reward = reward;
	}	

}
