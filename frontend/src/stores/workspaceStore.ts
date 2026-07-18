/**
 * Zustand store for the investigation workspace.
 * Manages chat messages, active visualizations, and data payloads.
 */

import { create } from "zustand";
import type {
  NetworkGraphData,
  HeatmapData,
  TimelineEvent,
  Evidence,
  VisualizationType,
} from "@/types/domain";
import type { ChatMessage, WorkspacePayload } from "@/types/api";
import { generateId } from "@/lib/utils";
import apiClient from "@/lib/api-client";

interface WorkspaceState {
  // Chat
  messages: ChatMessage[];
  isQuerying: boolean;

  // Active visualizations (driven by AI response)
  activeVisualizations: VisualizationType[];

  // Data payloads (populated by AI query responses)
  networkData: NetworkGraphData | null;
  heatmapData: HeatmapData | null;
  timelineData: TimelineEvent[] | null;
  evidenceData: Evidence[] | null;

  // Suggested follow-ups
  suggestedFollowups: string[];

  // Actions
  sendQuery: (text: string) => Promise<void>;
  clearWorkspace: () => void;
  setActiveVisualizations: (vis: VisualizationType[]) => void;
}

export const useWorkspaceStore = create<WorkspaceState>((set, get) => ({
  // Initial state
  messages: [],
  isQuerying: false,
  activeVisualizations: [],
  networkData: null,
  heatmapData: null,
  timelineData: null,
  evidenceData: null,
  suggestedFollowups: [
    "Show crimes in Bengaluru last month",
    "Find criminals linked to theft cases",
    "Show crime hotspots in Karnataka",
  ],

  // Send a natural language query to the AI pipeline
  sendQuery: async (text: string) => {
    const { messages } = get();

    // Add user message
    const userMessage: ChatMessage = {
      id: generateId(),
      role: "user",
      content: text,
      timestamp: new Date(),
    };

    // Add loading placeholder
    const loadingMessage: ChatMessage = {
      id: generateId(),
      role: "assistant",
      content: "Analyzing your query...",
      timestamp: new Date(),
      isLoading: true,
    };

    set({
      messages: [...messages, userMessage, loadingMessage],
      isQuerying: true,
    });

    try {
      // Build conversation history (last 5 messages)
      const history = messages
        .filter((m) => m.role === "user" || m.role === "assistant")
        .slice(-5)
        .map((m) => ({ role: m.role as "user" | "assistant", content: m.content }));

      const response = await apiClient.post("/api/v1/ai/query", {
        text,
        conversationHistory: history,
      });

      const payload: WorkspacePayload = response.data.data;

      // Replace loading message with AI response
      const aiMessage: ChatMessage = {
        id: generateId(),
        role: "assistant",
        content: payload.message,
        timestamp: new Date(),
        payload,
      };

      set((state) => ({
        messages: state.messages.map((m) =>
          m.id === loadingMessage.id ? aiMessage : m
        ),
        isQuerying: false,
        activeVisualizations: payload.visualizations || [],
        networkData: payload.networkGraph,
        heatmapData: payload.heatmap,
        timelineData: payload.timeline,
        evidenceData: payload.evidence,
        suggestedFollowups: payload.suggestedFollowups || [],
      }));
    } catch (error) {
      // Replace loading with error message
      const errorMessage: ChatMessage = {
        id: generateId(),
        role: "system",
        content: "Sorry, something went wrong processing your query. Please try again.",
        timestamp: new Date(),
      };

      set((state) => ({
        messages: state.messages.map((m) =>
          m.id === loadingMessage.id ? errorMessage : m
        ),
        isQuerying: false,
      }));

      console.error("Query failed:", error);
    }
  },

  // Clear workspace data and chat
  clearWorkspace: () =>
    set({
      messages: [],
      activeVisualizations: [],
      networkData: null,
      heatmapData: null,
      timelineData: null,
      evidenceData: null,
      suggestedFollowups: [
        "Show crimes in Bengaluru last month",
        "Find criminals linked to theft cases",
        "Show crime hotspots in Karnataka",
      ],
    }),

  // Manually toggle visualizations
  setActiveVisualizations: (vis: VisualizationType[]) =>
    set({ activeVisualizations: vis }),
}));
