/**
 * Application-wide constants.
 */

/** Default map center: Bengaluru, Karnataka */
export const MAP_CENTER = {
  lat: 12.9716,
  lng: 77.5946,
} as const;

export const MAP_DEFAULT_ZOOM = 10;

/** Maximum number of conversation messages to send as context */
export const MAX_CONVERSATION_CONTEXT = 5;

/** Maximum query length */
export const MAX_QUERY_LENGTH = 2000;

/** Application name */
export const APP_NAME = "KSP Shodhana";
export const APP_TAGLINE = "Ask. Analyze. Act.";

/** Risk level colors for consistent theming */
export const RISK_COLORS = {
  High: "#ef4444",    // red-500
  Medium: "#f59e0b",  // amber-500
  Low: "#22c55e",     // green-500
} as const;

/** Crime severity colors */
export const SEVERITY_COLORS = {
  Critical: "#dc2626", // red-600
  High: "#ef4444",     // red-500
  Medium: "#f59e0b",   // amber-500
  Low: "#3b82f6",      // blue-500
} as const;
