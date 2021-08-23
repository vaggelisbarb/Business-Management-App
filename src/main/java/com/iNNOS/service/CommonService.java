package com.iNNOS.service;

import java.util.List;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.iNNOS.constants.CommonConstants;


public class CommonService {

	public CommonService() {}
	
	public void getObj(AmazonS3 s3client, String fileNameToDownload, String bucket, String title)  {

        try {
        	String bucketFilePath = title + "/" + fileNameToDownload;
    		System.out.printf("[INFO] Downloading file from S3 Bucket \n"
    				+ "[INFO] FILE_NAME = %s\n"
    				+ "[INFO] BUCKET_FILE_PATH = %s\n", fileNameToDownload, bucketFilePath);
        	S3Object s3object = s3client.getObject(bucket, bucketFilePath);
        	S3ObjectInputStream inputStream = s3object.getObjectContent();
        	
        	String fullPathToSave = CommonConstants.LOCAL_DOWNLOAD_PATH+fileNameToDownload;
        	FileUtils.copyInputStreamToFile(inputStream, new File(fullPathToSave));
        
        	System.out.println("[INFO] Execution completed\n");
        }catch(Exception e) {
        	e.printStackTrace();
        }
    }

	
	/**
	 * @param fileToUpload File object that it will be uploaded to the cloud
	 * @param s3Client 
	 */
	public void putObject(File fileToUpload, AmazonS3 s3Client, String bucket, String folderName) {
		String fileFullPath = folderName + CommonConstants.SUFFIX + fileToUpload.getName();
		System.out.printf("[INFO] Uploading file into S3 Bucket \n"
				+ "[INFO] FILE_PATH = %s\n"
				+ "[INFO] BUCKET_FOLDER_NAME = %s\n", fileToUpload.getAbsolutePath(), fileFullPath);
		
		s3Client.putObject(new PutObjectRequest(bucket, fileFullPath, fileToUpload)
				.withCannedAcl(CannedAccessControlList.PublicRead));
	
		System.out.println("[INFO] Execution completed\n");
		
	}
	
	
	public void deleteObject(AmazonS3 s3client, String fileName, String bucket) {
		System.out.printf("[INFO] Deleting file from S3 Bucket \n"
				+ "[INFO] FILE_NAME = %s\n"
				+ "[INFO] BUCKET_FOLDER_NAME = %s\n", fileName, CommonConstants.FOLDER_NAME+CommonConstants.SUFFIX+fileName);
	
		s3client.deleteObject(bucket, CommonConstants.FOLDER_NAME+CommonConstants.SUFFIX+fileName);
		System.out.println("[INFO] Execution completed\n");

	}
	
	
	/**
	 * @param bucketName Amazon S3 bucket's name
	 * @param folderName Name of the bucket's folder that will be created
	 * @param client
	 * @param SUFFIX
	 */
	public static void createFolder(String bucketName, String folderName, AmazonS3 client,String SUFFIX) {
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);

		// create empty content+
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName + SUFFIX, emptyContent,
			metadata);

		// send request to S3 to create folder
		client.putObject(putObjectRequest);
	}

	/**
	 * This method first deletes all the files in given folder and then the folder
	 * itself
	 * @param bucketName Amazon S3 bucket's name
	 * @param folderName Bucket's folder that will be deleted
	 * @param client
	 */
	public static void deleteFolder(String bucketName, String folderName, AmazonS3 client) {
		List fileList = client.listObjects(bucketName, folderName).getObjectSummaries();
		for (Object object : fileList) {
			S3ObjectSummary file = (S3ObjectSummary) object;
			client.deleteObject(bucketName, file.getKey());
		}
		client.deleteObject(bucketName, folderName);
	}

    // handle results

}
