"use client";

import { useState, useEffect, useRef } from "react";
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

  // Collect active panel JSX nodes
  const activePanels: JSX.Element[] = [];
  if (showNetwork) activePanels.push(<NetworkGraphPanel key="network" data={networkData} />);
  if (showHeatmap) activePanels.push(<HeatmapPanel key="heatmap" data={heatmapData} />);
  if (showTimeline) activePanels.push(<TimelinePanel key="timeline" data={timelineData} />);
  if (showEvidence) activePanels.push(<EvidencePanel key="evidence" data={evidenceData} />);

  // Split pane ratio for 2 side-by-side visualization panels (percentage 20% to 80%, default 50%)
  const [splitRatio, setSplitRatio] = useState(50);
  const [isDraggingViz, setIsDraggingViz] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  const handleMouseDownViz = (e: React.MouseEvent) => {
    e.preventDefault();
    setIsDraggingViz(true);
  };

  useEffect(() => {
    const handleMouseMove = (e: MouseEvent) => {
      if (!isDraggingViz || !containerRef.current) return;
      const rect = containerRef.current.getBoundingClientRect();
      const relativeX = e.clientX - rect.left;
      const newRatio = (relativeX / rect.width) * 100;
      if (newRatio >= 20 && newRatio <= 80) {
        setSplitRatio(newRatio);
      }
    };

    const handleMouseUp = () => {
      setIsDraggingViz(false);
    };

    if (isDraggingViz) {
      window.addEventListener("mousemove", handleMouseMove);
      window.addEventListener("mouseup", handleMouseUp);
    }

    return () => {
      window.removeEventListener("mousemove", handleMouseMove);
      window.removeEventListener("mouseup", handleMouseUp);
    };
  }, [isDraggingViz]);

  if (activePanels.length === 0) return null;

  // Single panel layout
  if (activePanels.length === 1) {
    return (
      <div className="flex flex-1 h-full w-full p-3 overflow-hidden">
        <div className="w-full h-full min-w-0 animate-fade-in">{activePanels[0]}</div>
      </div>
    );
  }

  // Two panels side-by-side with resizable drag handle
  if (activePanels.length === 2) {
    return (
      <div
        ref={containerRef}
        className="flex flex-1 h-full w-full overflow-hidden p-3 select-none"
      >
        {/* Panel 1 */}
        <div
          className="flex flex-col h-full min-w-0 shrink-0 animate-fade-in"
          style={{ width: `${splitRatio}%` }}
        >
          {activePanels[0]}
        </div>

        {/* Resizable Slider Drag Handle */}
        <div
          onMouseDown={handleMouseDownViz}
          title="Click and drag left or right to resize visualization box area"
          className={`group relative flex w-3 items-center justify-center cursor-col-resize select-none shrink-0 transition-colors ${
            isDraggingViz
              ? "bg-[var(--color-primary)]/20"
              : "hover:bg-[var(--color-primary)]/10"
          }`}
        >
          {/* Visual Drag Knob */}
          <div
            className={`h-9 w-1.5 rounded-full transition-all duration-150 ${
              isDraggingViz
                ? "bg-[var(--color-primary)] scale-y-125"
                : "bg-[var(--color-border)] group-hover:bg-[var(--color-primary)]"
            }`}
          />
        </div>

        {/* Panel 2 */}
        <div className="flex flex-col flex-1 h-full min-w-0 overflow-hidden animate-fade-in">
          {activePanels[1]}
        </div>
      </div>
    );
  }

  // 3+ panels layout (grid fallback with dynamic columns)
  return (
    <div className="grid flex-1 h-full gap-3 overflow-auto p-3 grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 auto-rows-fr">
      {activePanels.map((panel, idx) => (
        <div key={idx} className="animate-fade-in h-full min-w-0">
          {panel}
        </div>
      ))}
    </div>
  );
}
