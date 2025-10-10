export interface ProjectScanSummary {
  id: string;
  scanName: string;
  status: 'PENDING' | 'RUNNING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  securityScore: number;
  riskLevel: 'SAFE' | 'LOW' | 'MODERATE' | 'HIGH' | 'CRITICAL';
  totalFindings: number;
  startedAt: string | null;
  completedAt: string | null;
}
