import { FindingResult } from './finding-result';

export interface ScanResult {
  scanId: string;
  status: 'PENDING' | 'RUNNING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  securityScore: number;
  riskLevel: 'SAFE' | 'LOW' | 'MODERATE' | 'HIGH' | 'CRITICAL';
  totalFindings: number;
  findings: FindingResult[];
}
