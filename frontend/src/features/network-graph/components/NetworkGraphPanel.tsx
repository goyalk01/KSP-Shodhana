"use client";

import { useRef, useCallback, useEffect } from "react";
import ForceGraph2D from "react-force-graph-2d";
import type { NetworkGraphData, GraphNode } from "@/types/domain";
import { RISK_COLORS } from "@/lib/constants";

interface NetworkGraphPanelProps {
  data: NetworkGraphData | null;
}

export default function NetworkGraphPanel({ data }: NetworkGraphPanelProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const graphRef = useRef<any>(null);

  // Auto-fit graph on data change
  useEffect(() => {
    if (graphRef.current && data && data.nodes.length > 0) {
      setTimeout(() => graphRef.current?.zoomToFit(400, 60), 300);
    }
  }, [data]);

  const nodeColor = useCallback((node: any) => {
    const graphNode = node as GraphNode;
    if (graphNode.type === "crime") return "#C18C5D"; // Terracotta
    if (graphNode.type === "location") return "#949484"; // Slate Bark
    return RISK_COLORS[graphNode.riskLevel as keyof typeof RISK_COLORS] || "#5D7052";
  }, []);

  const nodeLabel = useCallback((node: any) => {
    const graphNode = node as GraphNode;
    const parts = [graphNode.name];
    if (graphNode.status) parts.push(`(${graphNode.status})`);
    if (graphNode.riskLevel) parts.push(`[${graphNode.riskLevel} Risk]`);
    return parts.join(" ");
  }, []);

  if (!data || data.nodes.length === 0) {
    return (
      <div className="flex h-full flex-col rounded-2xl border border-[var(--color-border)]/50 bg-white shadow-sm overflow-hidden">
        <div className="panel-header">
          <span>🕸️ Criminal Network</span>
        </div>
        <div className="flex flex-1 items-center justify-center text-sm text-[var(--color-text-dim)]">
          No network data available
        </div>
      </div>
    );
  }

  return (
    <div className="flex h-full flex-col rounded-2xl border border-[var(--color-border)]/50 bg-white shadow-sm overflow-hidden" ref={containerRef}>
      <div className="panel-header">
        <span>🕸️ Criminal Network</span>
        <span className="text-[10px] font-bold uppercase tracking-wider text-[var(--color-primary)] bg-[var(--color-primary)]/10 px-2 py-0.5 rounded-md">
          {data.nodes.length} nodes · {data.links.length} links
        </span>
      </div>

      {/* Legend */}
      <div className="flex gap-4 px-5 py-2.5 border-b border-[var(--color-border)]/50 bg-white/50">
        <LegendItem color="#A85448" label="High Risk" />
        <LegendItem color="#C18C5D" label="Crime / Medium" />
        <LegendItem color="#5D7052" label="Low Risk" />
        <LegendItem color="#949484" label="Location" />
      </div>

      {/* Graph */}
      <div className="flex-1 relative">
        <ForceGraph2D
          ref={graphRef}
          graphData={{
            nodes: data.nodes.map((n) => ({ ...n })),
            links: data.links.map((l) => ({ ...l })),
          }}
          nodeColor={nodeColor}
          nodeLabel={nodeLabel}
          nodeRelSize={6}
          linkColor={() => "rgba(93, 112, 82, 0.2)"}
          linkWidth={(link: any) => Math.max(1, (link.strength || 1) / 3)}
          linkDirectionalParticles={2}
          linkDirectionalParticleSpeed={0.005}
          linkDirectionalParticleColor={() => "rgba(193, 140, 93, 0.5)"}
          backgroundColor="transparent"
          nodeCanvasObjectMode={() => "after"}
          nodeCanvasObject={(node: any, ctx: CanvasRenderingContext2D, globalScale: number) => {
            const label = node.name || "";
            const fontSize = Math.max(10 / globalScale, 3);
            ctx.font = `${fontSize}px Nunito, sans-serif`;
            ctx.textAlign = "center";
            ctx.textBaseline = "top";
            ctx.fillStyle = "rgba(44, 44, 36, 0.85)";
            ctx.fillText(label, node.x!, node.y! + 8);
          }}
          cooldownTicks={100}
          width={containerRef.current?.clientWidth || 600}
          height={(containerRef.current?.clientHeight || 400) - 80}
        />
      </div>
    </div>
  );
}

function LegendItem({ color, label }: { color: string; label: string }) {
  return (
    <div className="flex items-center gap-1.5">
      <div className="h-2.5 w-2.5 rounded-full shadow-sm" style={{ backgroundColor: color }} />
      <span className="text-[10px] font-bold text-[var(--color-text-muted)]">{label}</span>
    </div>
  );
}
