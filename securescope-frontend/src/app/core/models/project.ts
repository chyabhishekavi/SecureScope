import { ProjectScanSummary } from './project-scan-summary';

export type ProjectSourceType = 'QUICK_CODE' | 'ZIP_UPLOAD' | 'GITHUB_REPOSITORY';

export interface Project {
  id: string;
  name: string;
  description: string | null;
  sourceType: ProjectSourceType;
  technology: string | null;
  githubUrl: string | null;
  createdAt: string;
  updatedAt: string;
  scanHistory: ProjectScanSummary[];
}
