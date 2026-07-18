/**
 * API-related TypeScript types.
 * Matches the backend's ApiResponse envelope and WorkspacePayload.
 */

import type {
  NetworkGraphData,
  HeatmapData,
  TimelineEvent,
  Evidence,
  VisualizationType,
} from "./domain";

// ===== API Response Envelope =====

export interface ApiResponse<T> {
  success: boolean;
  data: T | null;
  error: ApiError | null;
  meta: ApiMeta;
}

export interface ApiError {
  code: string;
  message: string;
  details?: unknown;
}

export interface ApiMeta {
  timestamp: string;
  requestId: string;
  page?: number;
  pageSize?: number;
  totalRecords?: number;
}

// ===== AI Query =====

export interface AiQueryRequest {
  text: string;
  sessionId?: string;
  conversationHistory?: ConversationMessage[];
}

export interface ConversationMessage {
  role: "user" | "assistant";
  content: string;
}

// ===== Workspace Payload (the "super response") =====

export interface WorkspacePayload {
  message: string;
  visualizations: VisualizationType[];
  networkGraph: NetworkGraphData | null;
  heatmap: HeatmapData | null;
  timeline: TimelineEvent[] | null;
  evidence: Evidence[] | null;
  suggestedFollowups: string[];
}

// ===== Chat Message =====

export interface ChatMessage {
  id: string;
  role: "user" | "assistant" | "system";
  content: string;
  timestamp: Date;
  payload?: WorkspacePayload;
  isLoading?: boolean;
}
