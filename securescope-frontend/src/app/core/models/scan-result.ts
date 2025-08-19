import { FindingResult } from './finding-result';

export interface ScanResult {
  scanId: string;
  securityScore: number;
  riskLevel: 'SAFE' | 'LOW' | 'MODERATE' | 'HIGH' | 'CRITICAL';
  totalFindings: number;
  findings: FindingResult[];
}
