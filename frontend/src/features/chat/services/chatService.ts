/**
 * Chat service  handles communication with the AI query endpoint.
 */

import apiClient from "@/lib/api-client";
import type { ApiResponse, AiQueryRequest, WorkspacePayload } from "@/types/api";

/**
 * Send a natural language query to the AI pipeline.
 */
export async function sendQuery(request: AiQueryRequest): Promise<WorkspacePayload> {
  const response = await apiClient.post<ApiResponse<WorkspacePayload>>(
    "/api/v1/ai/query",
    request
  );

  if (!response.data.success || !response.data.data) {
    throw new Error(response.data.error?.message || "Query failed");
  }

  return response.data.data;
}
