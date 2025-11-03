export type FindingSeverity = 'INFO' | 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type FindingStatus = 'OPEN' | 'FIXED' | 'IGNORED' | 'FALSE_POSITIVE';
export type FindingCategory =
  | 'HARDCODED_SECRET'
  | 'VULNERABLE_DEPENDENCY'
  | 'RISKY_CODE_PATTERN'
  | 'MISSING_SECURITY_BEST_PRACTICE'
  | 'OWASP_TOP_TEN'
  | 'CONFIGURATION_RISK';

export interface Finding {
  id: string;
  scanId: string;
  scanName: string;
  title: string;
  description: string;
  severity: FindingSeverity;
  category: FindingCategory;
  status: FindingStatus;
  owaspCategory: string | null;
  filePath: string | null;
  lineNumber: number | null;
  evidence: string | null;
  recommendation: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface FindingFilters {
  severity?: FindingSeverity | '';
  category?: FindingCategory | '';
  owaspCategory?: string;
  status?: FindingStatus | '';
}
