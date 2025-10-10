import { ProjectSourceType } from './project';

export interface ProjectRequest {
  name: string;
  description: string | null;
  sourceType: ProjectSourceType;
  technology: string | null;
  githubUrl: string | null;
}
