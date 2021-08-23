package com.iNNOS.service;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.iNNOS.constants.CommonConstants;

public class CloudEntryPoint {
	
	private ClientConfiguration opts;
	private AmazonS3 s3Client;
	private CommonService service;
	private static CloudEntryPoint cloudentrypoint;
	
	private CloudEntryPoint() {
		service = new CommonService();
		opts = new ClientConfiguration(); // Only needed for "old" Cellar (V1)
	    opts.setSignerOverride("S3SignerType"); // Force the use of V2 signer
	    EndpointConfiguration endpointConfiguration = new EndpointConfiguration(CommonConstants.CELLAR_ADDON_HOST, null);
	    
	    AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(
	        new BasicAWSCredentials(CommonConstants.CELLAR_ADDON_KEY_ID, CommonConstants.CELLAR_ADDON_KEY_SECRET));
	    
	    this.s3Client = AmazonS3ClientBuilder.standard()
	        .withCredentials(credentialsProvider)
	        .withClientConfiguration(opts)
	        .withEndpointConfiguration(endpointConfiguration)
	        .withPathStyleAccessEnabled(Boolean.TRUE)
	        .build();	 
	}
	
	public static CloudEntryPoint getCloudEntryPointInstance() {
		if (cloudentrypoint == null)
			cloudentrypoint = new CloudEntryPoint();
		return cloudentrypoint;
	}
	
	public AmazonS3 getS3Client() {
		return s3Client;
	}
	
	public CommonService getCommonService() {
		return service;
	}
	
}
