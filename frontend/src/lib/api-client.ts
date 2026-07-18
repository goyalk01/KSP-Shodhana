/**
 * Axios-based API client for communicating with the Spring Boot backend.
 * All requests go through the Next.js BFF proxy (/api/proxy/...).
 */

import axios, { type AxiosInstance, type AxiosError } from "axios";
import type { ApiResponse } from "@/types/api";

/** Configured Axios instance for all API calls */
const apiClient: AxiosInstance = axios.create({
  baseURL: "/api/proxy",
  timeout: 35000, // 35s to allow for AI processing
  headers: {
    "Content-Type": "application/json",
  },
});

// Response interceptor for consistent error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiResponse<unknown>>) => {
    const apiError = error.response?.data?.error;
    if (apiError) {
      console.error(`[API Error] ${apiError.code}: ${apiError.message}`);
    } else {
      console.error("[API Error] Network or unexpected error:", error.message);
    }
    return Promise.reject(error);
  }
);

export default apiClient;
