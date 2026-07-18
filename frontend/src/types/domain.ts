/**
 * Global TypeScript types for the KSP Shodhana domain.
 * Shared across all features.
 */

// ===== Crime Domain =====

export interface Crime {
  rowId: number;
  firNumber: string;
  crimeType: string;
  description: string;
  status: CrimeStatus;
  severity: Severity;
  dateReported: string;
  dateOccurred: string;
  district: string;
  station: string;
  latitude: number;
  longitude: number;
  address: string;
  investigatingOfficer: string;
  weaponUsed?: string;
  modusOperandi?: string;
}

export interface Criminal {
  rowId: number;
  name: string;
  alias?: string;
  age: number;
  gender: string;
  idNumber?: string;
  phone?: string;
  address?: string;
  district: string;
  criminalHistory?: string;
  riskLevel: RiskLevel;
  status: CriminalStatus;
  photoUrl?: string;
}

// ===== Enums =====

export type CrimeStatus = "Open" | "Under Investigation" | "Closed";
export type CriminalStatus = "Wanted" | "Arrested" | "On Bail" | "At Large" | "Convicted";
export type Severity = "Critical" | "High" | "Medium" | "Low";
export type RiskLevel = "High" | "Medium" | "Low";
export type VisualizationType = "network_graph" | "heatmap" | "timeline" | "evidence";

// ===== Network Graph =====

export interface GraphNode {
  id: string;
  name: string;
  type: "criminal" | "crime" | "location";
  riskLevel?: RiskLevel;
  status?: string;
}

export interface GraphLink {
  source: string;
  target: string;
  type: string;
  strength: number;
}

export interface NetworkGraphData {
  nodes: GraphNode[];
  links: GraphLink[];
}

// ===== Heatmap =====

export interface HeatmapPoint {
  lat: number;
  lng: number;
  intensity: number;
}

export interface HeatmapData {
  points: HeatmapPoint[];
  center: { lat: number; lng: number };
  zoom: number;
}

// ===== Timeline =====

export interface TimelineEvent {
  id: string;
  eventType: string;
  eventDate: string;
  title: string;
  description: string;
}

// ===== Evidence =====

export interface Evidence {
  id: string;
  claim: string;
  sources: string[];
  confidence: number;
  type: "criminal_link" | "pattern" | "location" | "modus_operandi" | "temporal" | "system";
}
