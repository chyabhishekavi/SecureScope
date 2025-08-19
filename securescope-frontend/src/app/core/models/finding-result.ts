export interface FindingResult {
  title: string;
  description: string;
  severity: 'INFO' | 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  category: string;
  owaspCategory: string;
  filePath: string;
  lineNumber: number;
  evidence: string;
  recommendation: string;
}
