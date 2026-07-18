"use client";

import type { Evidence } from "@/types/domain";
import { formatConfidence } from "@/lib/utils";

interface EvidencePanelProps {
  data: Evidence[] | null;
}

export default function EvidencePanel({ data }: EvidencePanelProps) {
  if (!data || data.length === 0) {
    return (
      <div className="panel flex h-full flex-col">
        <div className="panel-header">
          <span>📊 Evidence Panel</span>
        </div>
        <div className="flex flex-1 items-center justify-center text-sm text-[var(--color-text-dim)]">
          No evidence data available
        </div>
      </div>
    );
  }

  return (
    <div className="panel flex h-full flex-col">
      <div className="panel-header">
        <span>📊 Explainable Evidence</span>
        <span className="text-[10px] font-normal normal-case tracking-normal text-[var(--color-text-dim)]">
          {data.length} items
        </span>
      </div>

      <div className="flex-1 overflow-y-auto p-3 space-y-2">
        {data.map((item, idx) => (
          <EvidenceCard key={item.id} item={item} index={idx} />
        ))}
      </div>
    </div>
  );
}

function EvidenceCard({ item, index }: { item: Evidence; index: number }) {
  const confidenceClass =
    item.confidence >= 0.7
      ? "confidence-high"
      : item.confidence >= 0.5
        ? "confidence-medium"
        : "confidence-low";

  const confidenceBarColor =
    item.confidence >= 0.7
      ? "bg-[var(--color-success)]"
      : item.confidence >= 0.5
        ? "bg-[var(--color-warning)]"
        : "bg-[var(--color-danger)]";

  const typeLabel =
    {
      criminal_link: "🔗 Criminal Link",
      pattern: "📈 Pattern",
      location: "📍 Location",
      modus_operandi: "🎯 Modus Operandi",
      temporal: "⏰ Temporal",
      system: "⚙️ System",
    }[item.type] || "📌 General";

  return (
    <div
      className="rounded-lg border border-[var(--color-border)] bg-[var(--color-surface)] p-3 transition-all hover:bg-[var(--color-surface-hover)] hover:border-[var(--color-primary)]/30 animate-fade-in"
      style={{ animationDelay: `${index * 0.1}s` }}
    >
      {/* Header: Type + Confidence */}
      <div className="flex items-center justify-between mb-2">
        <span className="text-[10px] font-medium uppercase tracking-wider text-[var(--color-text-dim)]">
          {typeLabel}
        </span>
        <div className="flex items-center gap-2">
          <span className={`text-xs font-semibold ${confidenceClass}`}>
            {formatConfidence(item.confidence)}
          </span>
        </div>
      </div>

      {/* Claim */}
      <p className="text-xs text-[var(--color-text)] leading-relaxed mb-2">
        {item.claim}
      </p>

      {/* Confidence Bar */}
      <div className="h-1 w-full rounded-full bg-[var(--color-border)] mb-2">
        <div
          className={`h-full rounded-full transition-all duration-500 ${confidenceBarColor}`}
          style={{ width: `${item.confidence * 100}%` }}
        />
      </div>

      {/* Sources */}
      {item.sources.length > 0 && (
        <div className="flex flex-wrap gap-1">
          {item.sources.map((source, sIdx) => (
            <span
              key={sIdx}
              className="rounded bg-[var(--color-primary)]/10 px-1.5 py-0.5 text-[10px] text-[var(--color-primary)] font-medium"
            >
              {source}
            </span>
          ))}
        </div>
      )}
    </div>
  );
}
