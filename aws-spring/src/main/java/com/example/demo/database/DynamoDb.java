package com.example.demo.database;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.example.demo.repository.RatingSystem;
import com.example.demo.repository.RatingSystemRepository;

@Repository
public class DynamoDb implements RatingSystemRepository<RatingSystem,String>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDb.class);

	@Autowired
	private DynamoDBMapper mapper;

	public void insertIntoDynamoDB(RatingSystem ratingsystem) {
		mapper.save(ratingsystem);
	}

	public RatingSystem getOneCustomerDetails(String customerId) {
		return mapper.load(RatingSystem.class, customerId);
	}

	public void updateCustomerDetails(RatingSystem ratingsystem) {
		try {
			mapper.save(ratingsystem, buildDynamoDBSaveExpression(ratingsystem));
		} catch (ConditionalCheckFailedException exception) {
			LOGGER.error("invalid data - " + exception.getMessage());
		}
	}

	public void deleteCustomerDetails(	RatingSystem ratingsystem) {
		mapper.delete(ratingsystem);
	}

	public DynamoDBSaveExpression buildDynamoDBSaveExpression(RatingSystem ratingsystem) {
		DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();
		Map<String, ExpectedAttributeValue> expected = new HashMap<>();
		expected.put("customerId", new ExpectedAttributeValue(new AttributeValue(ratingsystem.getcustomerId()))
				.withComparisonOperator(ComparisonOperator.EQ));
		saveExpression.setExpected(expected);
		return saveExpression;
	}
	

}

