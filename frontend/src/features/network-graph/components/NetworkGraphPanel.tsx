"use client";

import { useRef, useCallback, useEffect, useState } from "react";
import ForceGraph2D from "react-force-graph-2d";
import type { NetworkGraphData, GraphNode } from "@/types/domain";
import { RISK_COLORS } from "@/lib/constants";

interface NetworkGraphPanelProps {
  data: NetworkGraphData | null;
}

export default function NetworkGraphPanel({ data }: NetworkGraphPanelProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const graphWrapperRef = useRef<HTMLDivElement>(null);
  const graphRef = useRef<any>(null);

  const [dimensions, setDimensions] = useState<{ width: number; height: number }>({
    width: 600,
    height: 400,
  });

  // Track container dimensions dynamically with ResizeObserver
  useEffect(() => {
    if (!graphWrapperRef.current) return;

    const updateDimensions = () => {
      if (!graphWrapperRef.current) return;
      const w = graphWrapperRef.current.clientWidth || 600;
      const h = graphWrapperRef.current.clientHeight || 400;
      setDimensions({ width: w, height: h });
    };

    const observer = new ResizeObserver(() => {
      updateDimensions();
    });

    observer.observe(graphWrapperRef.current);
    updateDimensions();

    return () => observer.disconnect();
  }, []);

  // Dynamically adjust physics forces and zoom level whenever dimensions or data changes
  useEffect(() => {
    if (!graphRef.current || !data || data.nodes.length === 0) return;

    // Dynamic node charge and link distance based on canvas size
    const chargeStrength = Math.min(-250, -dimensions.width * 0.45);
    const linkDistance = Math.min(140, Math.max(60, dimensions.width * 0.14));

    graphRef.current.d3Force("charge")?.strength(chargeStrength);
    graphRef.current.d3Force("link")?.distance(linkDistance);
    graphRef.current.d3ReheatSimulation();

    // Smoothly re-fit graph to fill updated canvas dimensions
    const timer = setTimeout(() => {
      graphRef.current?.zoomToFit(300, 70);
    }, 200);

    return () => clearTimeout(timer);
  }, [dimensions, data]);

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
          <span>Criminal Network</span>
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
        <span>Criminal Network</span>
        <span className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--color-primary)] bg-[var(--color-primary)]/10 px-2.5 py-1 rounded-md shrink-0 whitespace-nowrap border border-[var(--color-primary)]/20">
          {data.nodes.length} nodes · {data.links.length} links
        </span>
      </div>

      {/* Legend */}
      <div className="flex gap-4 px-5 py-2.5 border-b border-[var(--color-border)]/50 bg-white/50 shrink-0">
        <LegendItem color="#A85448" label="High Risk" />
        <LegendItem color="#C18C5D" label="Crime / Medium" />
        <LegendItem color="#5D7052" label="Low Risk" />
        <LegendItem color="#949484" label="Location" />
      </div>

      {/* Dynamic Graph Container */}
      <div className="flex-1 relative min-h-0 overflow-hidden" ref={graphWrapperRef}>
        <ForceGraph2D
          ref={graphRef}
          width={dimensions.width}
          height={dimensions.height}
          graphData={{
            nodes: data.nodes.map((n) => ({ ...n })),
            links: data.links.map((l) => ({ ...l })),
          }}
          nodeColor={nodeColor}
          nodeLabel={nodeLabel}
          nodeRelSize={7}
          linkColor={() => "rgba(93, 112, 82, 0.25)"}
          linkWidth={(link: any) => Math.max(1.5, (link.strength || 1) / 2.5)}
          linkDirectionalParticles={2}
          linkDirectionalParticleSpeed={0.005}
          linkDirectionalParticleColor={() => "rgba(193, 140, 93, 0.6)"}
          backgroundColor="transparent"
          nodeCanvasObjectMode={() => "after"}
          nodeCanvasObject={(node: any, ctx: CanvasRenderingContext2D, globalScale: number) => {
            const label = node.name || "";
            const fontSize = Math.max(11 / globalScale, 3.5);
            ctx.font = `bold ${fontSize}px Nunito, sans-serif`;
            ctx.textAlign = "center";
            ctx.textBaseline = "top";
            ctx.fillStyle = "rgba(44, 44, 36, 0.9)";
            ctx.fillText(label, node.x!, node.y! + 9);
          }}
          cooldownTicks={100}
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
