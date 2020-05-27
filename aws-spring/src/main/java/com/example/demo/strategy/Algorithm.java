package com.example.demo.strategy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.example.demo.database.DynamoDb;
import com.example.demo.repository.Order;
import com.example.demo.repository.RatingSystem;
import com.example.demo.repository.SQSMessage;
import com.example.demo.repository.Type;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class Algorithm implements Strategy{
	
	Logger logger = LoggerFactory.getLogger(Algorithm.class);
	
	@Autowired
	private DynamoDb dynamoDb;
	
	public double logic(String from_sqs, RatingSystem from_Ddb) {
		
		double const_val = 0.00;
		double price=0.00,x;
		int quantity = 0;
		String type_name=null;
	  
		
		double amountBought = from_Ddb.getamountBought();
		double amountCancelled = from_Ddb.getamountCancelled();
		double amountReturned = from_Ddb.getamountReturned();
		double old_rating = from_Ddb.getrating();
		double amountShipped = from_Ddb.getAmountShipped();
		double reward = from_Ddb.getReward();
		double fine = from_Ddb.getfine();
		boolean returnPolicy = from_Ddb.isReturnPolicy();
		boolean blackListed = from_Ddb.isBlackListed();
		double c;
		
		ObjectMapper mapper = new ObjectMapper();
		SQSMessage sqsMessage;
		try {
			sqsMessage = mapper.readValue(from_sqs, SQSMessage.class);
			for (Order Order : sqsMessage.getOrder()) {
				for(Type type : Order.getType()) {
	        	 type_name = type.getType_name();
	        	 price = type.getPrice();
	        	 quantity = type.getQuantity();
	        	}
	        }
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		
		logger.info("type = "+type_name);
		
		
		double amount = price*quantity;
		
		
		if(type_name.equals("placed")) {
			
			logger.info("order is placed");
			return old_rating;
			
		}
		
		else if(type_name.equals("shipped")) {
			amountBought = amountBought+amount;
			
			x=0.7;
			
			if(amountShipped>=amountCancelled+amountReturned) {
				c = Math.exp(-(amountCancelled+amountReturned+1)/(amountShipped+1));
			}
			else {
				c = Math.exp(-(amountShipped+1)/(amountCancelled+amountReturned+1));
			}
			
			logger.info("amountShipped="+ amountShipped);
			const_val= ((old_rating/2)*c)+ (Math.log(amount)/10);
			amountShipped= amountShipped+amount;
			logger.info("amount = "+amount);
			
			reward += (2 * amount)/100;
			
			from_Ddb.setAmountShipped(amountShipped);
			from_Ddb.setReward(reward);
			
		
		}
		else if(type_name.equals("cancelled")) {
			amountBought = amountBought+amount;
			
			
			x=0.85;
			
			if(amountCancelled>=amountShipped+amountReturned) {
				c = Math.exp(-(amountShipped+amountReturned+1)/(amountCancelled+1));
				
			}
			else {
				c = Math.exp(-(amountCancelled+1)/(amountShipped+amountReturned+1));
			}
			
			logger.info("amountCancelled="+ amountCancelled);
			const_val= (-(c/10) - ((Math.log(amount)/10))) ;
			amountCancelled = amountCancelled +amount;

			logger.info("amount = "+amount);
			
			fine += (2 * amount)/100;
			from_Ddb.setamountCancelled(amountCancelled);
			from_Ddb.setfine(fine);
		}
		else {
			amountBought = amountBought+amount;
			
			
			x=0.8;
			
			if(amountReturned>=amountCancelled+amountShipped) {
				c = Math.exp(-(amountCancelled+amountShipped+1)/(amountReturned+1));
				
			}
			else {
				c = Math.exp(-(amountReturned+1)/(amountCancelled+amountShipped+1));
				
			}
			
			logger.info("amountReturned="+ amountReturned);
			const_val= -(c/10) - ((Math.log(amount)/10));
			amountReturned = amountReturned +amount;

			logger.info("amount = "+amount);

			
			
			fine += (2 * amount)/100;
			from_Ddb.setamountReturned(amountReturned);
			from_Ddb.setfine(fine);
		}
		
		
		from_Ddb.setamountBought(amountBought);
		
		logger.info("x = "+x);
		double new_rating = (x*old_rating)+ (1 - Math.exp((-amountShipped)/(amountShipped+amountCancelled+amountReturned))) + (const_val);                                   
		

	   logger.info("old_rating = "+ old_rating);
	   
	   
	   if(new_rating<0 )
		   new_rating = 0;
	   else if(new_rating>10)
		   new_rating = 10;
	   
	   
	   if(new_rating<old_rating && new_rating<=2 ) {
		   from_Ddb.setReturnPolicy(false);
	   }
	   else {
		   from_Ddb.setReturnPolicy(true);
	   }
	   
	   if((amountReturned+amountCancelled) > (amountShipped)) {
		   from_Ddb.setBlackListed(true);
	   }
	   else {
		   from_Ddb.setBlackListed(false);
		   
	   }
	   
	   from_Ddb.setrating(new_rating);
	   dynamoDb.updateCustomerDetails(from_Ddb);
	   
	   return new_rating;
	   
	}
	
}


