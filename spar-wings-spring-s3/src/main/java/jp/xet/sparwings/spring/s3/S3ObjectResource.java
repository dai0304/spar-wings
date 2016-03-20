/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.xet.sparwings.spring.s3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;
import com.amazonaws.util.BinaryUtils;

import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.ExecutorServiceAdapter;

/**
 * {@link Resource} implementation for Amazon {@link S3Object}.
 *
 * @since #version#
 * @author daisuke
 */
@ToString
@RequiredArgsConstructor
public class S3ObjectResource extends AbstractResource implements WritableResource {
	
	private static final MessageDigest MD5;
	static {
		try {
			MD5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new Error(e);
		}
	}
	
	@NonNull
	private final AmazonS3 amazonS3;
	
	@NonNull
	private final String bucketName;
	
	@NonNull
	private final String key;
	
	@NonNull
	private final Optional<String> versionId;
	
	@NonNull
	private final TaskExecutor taskExecutor;
	
	private volatile ObjectMetadata objectMetadata;
	
	
	/**
	 * Construct S3 resource instance with client, bucket name, key and {@link TaskExecutor}.
	 * 
	 * @param amazonS3 The Amazon S3 client
	 * @param bucketName The bucket name of S3
	 * @param key The key of S3 object
	 * @param taskExecutor The {@link TaskExecutor}
	 * @since #version#
	 */
	public S3ObjectResource(AmazonS3 amazonS3, String bucketName, String key, TaskExecutor taskExecutor) {
		this(amazonS3, bucketName, key, Optional.empty(), taskExecutor);
	}
	
	/**
	 * Construct S3 resource instance with client, bucket name, key, nullable String version ID and {@link TaskExecutor}.
	 * 
	 * @param amazonS3 The Amazon S3 client
	 * @param bucketName The bucket name of S3
	 * @param key The key of S3 object
	 * @param versionId The version ID (nullable)
	 * @param taskExecutor The {@link TaskExecutor}
	 * @since #version#
	 */
	public S3ObjectResource(AmazonS3 amazonS3, String bucketName, String key, String versionId,
			TaskExecutor taskExecutor) {
		this(amazonS3, bucketName, key, Optional.ofNullable(versionId), taskExecutor);
	}
	
	/**
	 * Construct S3 resource instance with client, bucket name, key and non-null optional version ID.
	 * 
	 * @param amazonS3 The Amazon S3 client
	 * @param bucketName The bucket name of S3
	 * @param key The key of S3 object
	 * @param versionId The version ID
	 * @since #version#
	 */
	public S3ObjectResource(AmazonS3 amazonS3, String bucketName, String key, Optional<String> versionId) {
		this(amazonS3, bucketName, key, versionId, new SyncTaskExecutor());
	}
	
	/**
	 * Construct S3 resource instance with client, bucket name, key and nullable String version ID (nullable).
	 * 
	 * @param amazonS3 The Amazon S3 client
	 * @param bucketName The bucket name of S3
	 * @param key The key of S3 object
	 * @param versionId The version ID (nullable)
	 * @since #version#
	 */
	public S3ObjectResource(AmazonS3 amazonS3, String bucketName, String key, String versionId) {
		this(amazonS3, bucketName, key, Optional.ofNullable(versionId), new SyncTaskExecutor());
	}
	
	/**
	 * Construct S3 resource instance with client, bucket name and key.
	 * 
	 * @param amazonS3 The Amazon S3 client
	 * @param bucketName The bucket name of S3
	 * @param key The key of S3 object
	 * @since #version#
	 */
	public S3ObjectResource(AmazonS3 amazonS3, String bucketName, String key) {
		this(amazonS3, bucketName, key, Optional.empty(), new SyncTaskExecutor());
	}
	
	@Override
	public boolean exists() {
		return getObjectMetadata().isPresent();
	}
	
	@Override
	public URL getURL() throws IOException {
		if (amazonS3 instanceof AmazonS3Client) {
			Region region = ((AmazonS3Client) amazonS3).getRegion().toAWSRegion();
			String path = String.format("/%s/%s", bucketName, key);
			return new URL("https", region.getServiceEndpoint(AmazonS3Client.S3_SERVICE_NAME), path);
		} else {
			return super.getURL();
		}
	}
	
	@Override
	public File getFile() throws IOException {
		throw new UnsupportedOperationException("Amazon S3 resource can not be resolved to java.io.File objects.Use " +
				"getInputStream() to retrieve the contents of the object!");
	}
	
	@Override
	public long contentLength() throws IOException {
		return getRequiredObjectMetadata().getContentLength();
	}
	
	@Override
	public long lastModified() throws IOException {
		return getRequiredObjectMetadata().getLastModified().getTime();
	}
	
	@Override
	public S3ObjectResource createRelative(String relativePath) throws IOException {
		StringBuilder sb = new StringBuilder(key);
		if (key.endsWith("/") == false) {
			sb.append("/");
		}
		sb.append(relativePath);
		return new S3ObjectResource(amazonS3, bucketName, sb.toString(), taskExecutor);
	}
	
	@Override
	public String getFilename() throws IllegalStateException {
		int lastIndexOfPathSeparator = key.lastIndexOf('/');
		if (lastIndexOfPathSeparator == -1) {
			return key;
		} else {
			return key.substring(lastIndexOfPathSeparator + 1);
		}
	}
	
	@Override
	public String getDescription() {
		StringBuilder builder = new StringBuilder("Amazon s3 resource [")
			.append("bucket='").append(bucketName).append("'")
			.append(", key='").append(key).append("'");
		versionId.ifPresent(vid -> {
			builder.append(", versionId='").append(vid).append("'");
		});
		builder.append("]");
		return builder.toString();
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
		versionId.ifPresent(getObjectRequest::setVersionId);
		return amazonS3.getObject(getObjectRequest).getObjectContent();
	}
	
	@Override
	public boolean isWritable() {
		return true;
	}
	
	@Override
	public OutputStream getOutputStream() throws IOException {
		return new SimpleStorageOutputStream();
	}
	
	private ObjectMetadata getRequiredObjectMetadata() throws FileNotFoundException {
		return getObjectMetadata().orElseThrow(() -> new FileNotFoundException(getDescription() + " does not found!"));
	}
	
	private synchronized Optional<ObjectMetadata> getObjectMetadata() {
		if (objectMetadata == null) {
			try {
				GetObjectMetadataRequest metadataRequest = new GetObjectMetadataRequest(bucketName, key);
				versionId.ifPresent(metadataRequest::setVersionId);
				objectMetadata = amazonS3.getObjectMetadata(metadataRequest);
			} catch (AmazonS3Exception e) {
				// Catch 404 (object not found) and 301 (bucket not found, moved permanently)
				if (e.getStatusCode() == 404 || e.getStatusCode() == 301) {
					objectMetadata = null;
				} else {
					throw e;
				}
			}
		}
		return Optional.ofNullable(objectMetadata);
	}
	
	
	private class SimpleStorageOutputStream extends OutputStream {
		
		// The minimum size for a multi part is 5 MB, hence the buffer size of 5 MB
		private static final int BUFFER_SIZE = 1024 * 1024 * 5;
		
		private ByteArrayOutputStream currentOutputStream = new ByteArrayOutputStream(BUFFER_SIZE);
		
		private final Object monitor = new Object();
		
		private final CompletionService<UploadPartResult> completionService;
		
		private int partNumberCounter = 1;
		
		private InitiateMultipartUploadResult initiateMultiPartUploadResult;
		
		
		SimpleStorageOutputStream() {
			completionService = new ExecutorCompletionService<>(new ExecutorServiceAdapter(taskExecutor));
		}
		
		@Override
		public void write(int b) throws IOException {
			synchronized (monitor) {
				if (currentOutputStream.size() == BUFFER_SIZE) {
					InitiateMultipartUploadResult imur = initiateMultipartUploadIfRequired();
					completionService.submit(new UploadPartResultCallable(
							amazonS3,
							currentOutputStream.toByteArray(),
							currentOutputStream.size(),
							bucketName,
							key,
							imur.getUploadId(),
							partNumberCounter++,
							false));
					currentOutputStream.reset();
				}
				currentOutputStream.write(b);
			}
		}
		
		@Override
		public void close() throws IOException {
			synchronized (monitor) {
				if (isMultiPartUpload()) {
					finishMultiPartUpload();
				} else {
					finishSimpleUpload();
				}
			}
		}
		
		private boolean isMultiPartUpload() {
			return initiateMultiPartUploadResult != null;
		}
		
		private void finishSimpleUpload() {
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(currentOutputStream.size());
			
			byte[] content = currentOutputStream.toByteArray();
			String md5Digest = BinaryUtils.toBase64(MD5.digest(content));
			objectMetadata.setContentMD5(md5Digest);
			
			amazonS3.putObject(bucketName, key, new ByteArrayInputStream(content), objectMetadata);
			
			//Release the memory early
			currentOutputStream = null;
		}
		
		private void finishMultiPartUpload() throws IOException {
			completionService.submit(new UploadPartResultCallable(
					amazonS3,
					currentOutputStream.toByteArray(),
					currentOutputStream.size(),
					bucketName,
					key,
					initiateMultiPartUploadResult.getUploadId(),
					partNumberCounter,
					true));
			try {
				List<PartETag> partETags = getMultiPartsUploadResults();
				amazonS3.completeMultipartUpload(new CompleteMultipartUploadRequest(
						initiateMultiPartUploadResult.getBucketName(),
						initiateMultiPartUploadResult.getKey(),
						initiateMultiPartUploadResult.getUploadId(), partETags));
			} catch (ExecutionException e) {
				abortMultiPartUpload();
				throw new IOException("Multi part upload failed ", e.getCause());
			} catch (InterruptedException e) {
				abortMultiPartUpload();
				Thread.currentThread().interrupt();
			} finally {
				currentOutputStream = null;
			}
		}
		
		private InitiateMultipartUploadResult initiateMultipartUploadIfRequired() {
			if (initiateMultiPartUploadResult == null) {
				initiateMultiPartUploadResult = amazonS3.initiateMultipartUpload(
					new InitiateMultipartUploadRequest(bucketName, key));
			}
			return initiateMultiPartUploadResult;
		}
		
		private void abortMultiPartUpload() {
			if (isMultiPartUpload()) {
				amazonS3.abortMultipartUpload(new AbortMultipartUploadRequest(
						initiateMultiPartUploadResult.getBucketName(),
						initiateMultiPartUploadResult.getKey(),
						initiateMultiPartUploadResult.getUploadId()));
			}
		}
		
		private List<PartETag> getMultiPartsUploadResults() throws ExecutionException, InterruptedException {
			List<PartETag> result = new ArrayList<>(partNumberCounter);
			for (int i = 0; i < partNumberCounter; i++) {
				Future<UploadPartResult> uploadPartResultFuture = completionService.take();
				result.add(uploadPartResultFuture.get().getPartETag());
			}
			return result;
		}
		
		
		private class UploadPartResultCallable implements Callable<UploadPartResult> {
			
			private final AmazonS3 amazonS3;
			
			private byte[] content;
			
			private final int contentLength;
			
			private final int partNumber;
			
			private final boolean last;
			
			private final String bucketName;
			
			private final String key;
			
			private final String uploadId;
			
			
			private UploadPartResultCallable(AmazonS3 amazonS3, byte[] content, int contentLength, String bucketName,
					String key, String uploadId, int partNumber, boolean last) {
				this.amazonS3 = amazonS3;
				this.content = content;
				this.contentLength = contentLength;
				this.partNumber = partNumber;
				this.last = last;
				this.bucketName = bucketName;
				this.key = key;
				this.uploadId = uploadId;
			}
			
			@Override
			public UploadPartResult call() throws Exception {
				try {
					return amazonS3.uploadPart(new UploadPartRequest()
						.withBucketName(bucketName)
						.withKey(key)
						.withUploadId(uploadId)
						.withInputStream(new ByteArrayInputStream(content))
						.withPartNumber(partNumber)
						.withLastPart(last)
						.withPartSize(contentLength));
				} finally {
					// Release the memory, as the callable may still live inside the CompletionService which would cause
					// an exhaustive memory usage
					content = null;
				}
			}
		}
	}
}
