"use client";

import dynamic from "next/dynamic";
import { useWorkspaceStore } from "@/stores/workspaceStore";
import EvidencePanel from "@/features/evidence/components/EvidencePanel";
import TimelinePanel from "@/features/timeline/components/TimelinePanel";

// Dynamic imports for heavy visualization libraries (no SSR)
const NetworkGraphPanel = dynamic(
  () => import("@/features/network-graph/components/NetworkGraphPanel"),
  { ssr: false, loading: () => <PanelSkeleton label="Network Graph" /> }
);

const HeatmapPanel = dynamic(
  () => import("@/features/heatmap/components/HeatmapPanel"),
  { ssr: false, loading: () => <PanelSkeleton label="Crime Heatmap" /> }
);

function PanelSkeleton({ label }: { label: string }) {
  return (
    <div className="flex flex-col rounded-2xl border border-[var(--color-border)]/50 bg-white shadow-sm overflow-hidden h-full">
      <div className="panel-header">{label}</div>
      <div className="flex-1 shimmer m-3 rounded-xl" />
    </div>
  );
}

export default function VisualizationGrid() {
  const { activeVisualizations, networkData, heatmapData, timelineData, evidenceData } =
    useWorkspaceStore();

  // Determine which panels to show based on AI response
  const showNetwork = activeVisualizations.includes("network_graph");
  const showHeatmap = activeVisualizations.includes("heatmap");
  const showTimeline = activeVisualizations.includes("timeline");
  const showEvidence = activeVisualizations.includes("evidence");

  // Count active panels for layout
  const activePanels = [showNetwork, showHeatmap, showTimeline, showEvidence].filter(Boolean).length;

  // Dynamic grid layout based on number of active panels
  const gridClass =
    activePanels === 1
      ? "grid-cols-1 grid-rows-1"
      : activePanels === 2
        ? "grid-cols-1 lg:grid-cols-2 grid-rows-1"
        : activePanels === 3
          ? "grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 grid-rows-1"
          : "grid-cols-1 lg:grid-cols-2 grid-rows-2";

  return (
    <div className={`grid flex-1 h-full gap-3 overflow-auto p-3 ${gridClass} auto-rows-fr`}>
      {showNetwork && (
        <div className="animate-fade-in h-full">
          <NetworkGraphPanel data={networkData} />
        </div>
      )}
      {showHeatmap && (
        <div className="animate-fade-in h-full" style={{ animationDelay: "0.1s" }}>
          <HeatmapPanel data={heatmapData} />
        </div>
      )}
      {showTimeline && (
        <div className="animate-fade-in h-full" style={{ animationDelay: "0.2s" }}>
          <TimelinePanel data={timelineData} />
        </div>
      )}
      {showEvidence && (
        <div className="animate-fade-in h-full" style={{ animationDelay: "0.3s" }}>
          <EvidencePanel data={evidenceData} />
        </div>
      )}
    </div>
  );
}
