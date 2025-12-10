export interface SecurityReport {
  id: string;
  scanId: string;
  title: string;
  format: 'HTML';
  generatedAt: string;
  findingCount: number;
  downloadUrl: string;
}

export interface ReportPreview {
  report: SecurityReport;
  htmlContent: string;
}
