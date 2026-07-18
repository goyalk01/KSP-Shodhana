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
    if (graphNode.type === "crime") return "#f59e0b";
    if (graphNode.type === "location") return "#3b82f6";
    return RISK_COLORS[graphNode.riskLevel as keyof typeof RISK_COLORS] || "#6366f1";
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
      <div className="panel flex h-full flex-col">
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
    <div className="panel flex h-full flex-col" ref={containerRef}>
      <div className="panel-header">
        <span>🕸️ Criminal Network</span>
        <span className="text-[10px] font-normal normal-case tracking-normal text-[var(--color-text-dim)]">
          {data.nodes.length} nodes · {data.links.length} links
        </span>
      </div>

      {/* Legend */}
      <div className="flex gap-4 px-4 py-2 border-b border-[var(--color-border-subtle)]">
        <LegendItem color="#ef4444" label="High Risk" />
        <LegendItem color="#f59e0b" label="Crime / Medium" />
        <LegendItem color="#22c55e" label="Low Risk" />
        <LegendItem color="#3b82f6" label="Location" />
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
          linkColor={() => "rgba(99, 102, 241, 0.3)"}
          linkWidth={(link: any) => Math.max(1, (link.strength || 1) / 3)}
          linkDirectionalParticles={2}
          linkDirectionalParticleSpeed={0.005}
          linkDirectionalParticleColor={() => "rgba(99, 102, 241, 0.6)"}
          backgroundColor="transparent"
          nodeCanvasObjectMode={() => "after"}
          nodeCanvasObject={(node: any, ctx: CanvasRenderingContext2D, globalScale: number) => {
            const label = node.name || "";
            const fontSize = Math.max(10 / globalScale, 3);
            ctx.font = `${fontSize}px Inter, sans-serif`;
            ctx.textAlign = "center";
            ctx.textBaseline = "top";
            ctx.fillStyle = "rgba(226, 232, 240, 0.8)";
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
      <div className="h-2.5 w-2.5 rounded-full" style={{ backgroundColor: color }} />
      <span className="text-[10px] text-[var(--color-text-dim)]">{label}</span>
    </div>
  );
}
