package com.example.demo.strategy;

import com.example.demo.repository.RatingSystem;

public interface Strategy {
	
	public double logic(String from_sqs, RatingSystem from_ddb);

}
