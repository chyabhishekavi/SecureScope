export interface ZipUploadResponse {
  uploadId: string;
  projectId: string;
  fileName: string;
  fileSizeBytes: number;
  extractedFileCount: number;
  skippedEntryCount: number;
  uploadedAt: string;
}
