package com.example.demo.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.example.demo.database.DynamoDb;
import com.example.demo.repository.RatingSystem;
import com.example.demo.repository.SQSMessage;
import com.example.demo.strategy.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

@EnableSqs
@PropertySource(value = "application.yml")
@Service
public class SQSService {
	
	Logger logger = LoggerFactory.getLogger(SQSService.class);
	
	private String sqsEndPoint="https://sqs.us-east-1.amazonaws.com/237142071300/trial-sqs.fifo";
	
	@Autowired
	private AmazonSQSBufferedAsyncClient amazonsqs;
	
	@Autowired(required=false)
	private Algorithm algorithm;
	
	@Autowired
	private DynamoDb dynamoDb;
	
	@Autowired(required = false)
	private RatingSystem ratingSystem;
	
	private SQSMessage sqsMessage;
	
	
	
	ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(sqsEndPoint)
			  .withWaitTimeSeconds(10)
			  .withVisibilityTimeout(10)
			  .withMaxNumberOfMessages(10);
	
    @Async
	public void receiveMessage(){
		System.out.println("queue ="+ amazonsqs + " remsgreq = " + receiveMessageRequest);

		    final List<Message> messages = amazonsqs.receiveMessage(receiveMessageRequest).getMessages();

		    for (Message messageObject : messages) {
		    	
		    	logger.info("Current thread is "+ Thread.currentThread().getName());
		    	
		        String message = messageObject.getBody();
		        
		        ObjectMapper mapper = new ObjectMapper();
		        
		        try {
		        	sqsMessage = mapper.readValue(message, SQSMessage.class);
				} catch (Exception e) {
					logger.info("Exception occured is "+ e);
				}
		        
		        Long cid = sqsMessage.getCid();
		       
		        logger.info("this is = "+cid);
		         
		         try {
		        	logger.info("inside try");
		        	ratingSystem = dynamoDb.getOneCustomerDetails(cid.toString());
		        	logger.info(ratingSystem.toString());
				} catch (NullPointerException e) {
					ratingSystem = new RatingSystem();
					ratingSystem.setcustomerId(cid.toString());
					ratingSystem.setrating(2);
					ratingSystem.setReturnPolicy(true);
					dynamoDb.insertIntoDynamoDB(ratingSystem);	
				}catch (Exception e) {
					logger.info("exception is "+ e);
				}
    
		         double rate = algorithm.logic(message, ratingSystem);
		
		         
		        logger.info("new_rating = "+ rate);
		        
		        logger.info("End of "+ Thread.currentThread().getName());
		        System.out.println("");
		        
		        
		        
		    
		}
		
	}
	
}

