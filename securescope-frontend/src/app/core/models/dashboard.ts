export interface DashboardMetric {
  label: string;
  value: number;
}

export interface RecentScan {
  id: string;
  scanName: string;
  status: 'PENDING' | 'RUNNING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  securityScore: number;
  riskLevel: 'SAFE' | 'LOW' | 'MODERATE' | 'HIGH' | 'CRITICAL';
  totalFindings: number;
  completedAt: string | null;
}

export interface DashboardSummary {
  totalProjects: number;
  totalScans: number;
  averageSecurityScore: number;
  criticalFindings: number;
  highFindings: number;
  recentScans: RecentScan[];
}

export interface ScoreTrendPoint {
  scanId: string;
  scanName: string;
  securityScore: number;
  completedAt: string | null;
}
