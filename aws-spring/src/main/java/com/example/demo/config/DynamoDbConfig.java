package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

@Configuration
public class DynamoDbConfig {
	


		private String awsRegion= "us-east-1";


		private String awsDynamoDBEndPoint ="dynamodb.us-east-1.amazonaws.com";
		
		private String awsAccessKey = "";
		
		private String awsSecretKey = "";

		@Bean
		public DynamoDBMapper mapper() {
			return new DynamoDBMapper(amazonDynamoDBConfig());
		}

		public AmazonDynamoDB amazonDynamoDBConfig() {
			return AmazonDynamoDBClientBuilder.standard()
					.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsDynamoDBEndPoint, awsRegion))
					.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey)))
					.build();
		}

}
