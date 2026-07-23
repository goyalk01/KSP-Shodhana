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

  // Navigation / Tabs
  activeTab: string;
  setActiveTab: (tab: string) => void;

  // Settings state
  geminiModel: string;
  geminiApiKey: string;
  defaultDistrict: string;
  localFallbackActive: boolean;
  settingsLoading: boolean;
  settingsSuccessMessage: string;

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
  fetchSettings: () => Promise<void>;
  updateSettings: (settingsPayload: {
    geminiModel: string;
    geminiApiKey: string;
    defaultDistrict: string;
    localFallbackActive: boolean;
  }) => Promise<void>;
}

export const useWorkspaceStore = create<WorkspaceState>((set, get) => ({
  // Initial state
  messages: [],
  isQuerying: false,
  activeTab: "dashboard",
  setActiveTab: (tab: string) => set({ activeTab: tab }),

  // Settings
  geminiModel: "gemini-3.5-flash-lite",
  geminiApiKey: process.env.NEXT_PUBLIC_GEMINI_API_KEY || "",
  defaultDistrict: "Bengaluru Urban",
  localFallbackActive: false,
  settingsLoading: false,
  settingsSuccessMessage: "",

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

  // Fetch settings from API
  fetchSettings: async () => {
    set({ settingsLoading: true, settingsSuccessMessage: "" });
    try {
      const response = await apiClient.get("/api/v1/settings");
      const data = response.data.data;
      set({
        geminiModel: data.gemini_model || "gemini-3.5-flash-lite",
        geminiApiKey: data.gemini_api_key || "",
        defaultDistrict: data.default_district || "Bengaluru Urban",
        localFallbackActive: data.local_fallback_active || false,
        settingsLoading: false,
      });
    } catch (error) {
      console.error("Failed to fetch settings:", error);
      set({ settingsLoading: false });
    }
  },

  // Save settings via API
  updateSettings: async (settingsPayload) => {
    set({ settingsLoading: true, settingsSuccessMessage: "" });
    try {
      await apiClient.post("/api/v1/settings", {
        gemini_model: settingsPayload.geminiModel,
        gemini_api_key: settingsPayload.geminiApiKey,
        default_district: settingsPayload.defaultDistrict,
        local_fallback_active: settingsPayload.localFallbackActive,
      });
      set({
        geminiModel: settingsPayload.geminiModel,
        geminiApiKey: settingsPayload.geminiApiKey,
        defaultDistrict: settingsPayload.defaultDistrict,
        localFallbackActive: settingsPayload.localFallbackActive,
        settingsLoading: false,
        settingsSuccessMessage: "Settings saved successfully!",
      });
      // Clear success message after 3 seconds
      setTimeout(() => set({ settingsSuccessMessage: "" }), 3000);
    } catch (error) {
      console.error("Failed to update settings:", error);
      set({ settingsLoading: false });
    }
  },
}));
