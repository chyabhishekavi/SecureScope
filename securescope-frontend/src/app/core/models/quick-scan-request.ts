export interface QuickScanRequest {
  snippetName: string;
  language: string;
  fileName: string;
  codeContent: string;
  projectId?: string | null;
}
