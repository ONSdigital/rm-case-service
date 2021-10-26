package uk.gov.ons.ctp.response.casesvc.message;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import java.io.File;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;

@Component
public class UploadObjectGCS {
  private static final Logger log = LoggerFactory.getLogger(UploadObjectGCS.class);
  private final Storage storage;
  private AppConfig appConfig;

  public UploadObjectGCS(Storage storage, AppConfig appConfig) {
    this.storage = storage;
    this.appConfig = appConfig;
  }

  public boolean uploadObject(String filename, String bucket, byte[] data) {
    String prefix = appConfig.getGcp().getBucket().getPrefix();
    String bucketFilename;
    if (!prefix.isEmpty()) {
      bucketFilename = prefix + File.separator + filename;
    } else {
      bucketFilename = filename;
    }

    BlobId blobId = BlobId.of(bucket, bucketFilename);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/json").build();
    Boolean isSuccess = false;
    log.info("file_name: " + bucketFilename + " bucket: " + bucket + ", Uploading to GCS bucket");
    try {
      storage.create(blobInfo, data);
      isSuccess = true;
      log.info("file_name: " + bucketFilename + " bucket: " + bucket + ", Upload Successful!");
    } catch (StorageException exception) {
      log.with("bucketFileName", bucketFilename)
          .with("bucket", bucket)
          .with("exception", exception)
          .error("Error uploading the generated file to GCS");
    }
    return isSuccess;
  }
}
