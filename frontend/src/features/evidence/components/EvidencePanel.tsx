"use client";

import type { Evidence } from "@/types/domain";
import { formatConfidence } from "@/lib/utils";

interface EvidencePanelProps {
  data: Evidence[] | null;
}

export default function EvidencePanel({ data }: EvidencePanelProps) {
  if (!data || data.length === 0) {
    return (
      <div className="flex h-full flex-col rounded-2xl border border-[var(--color-border)]/50 bg-white shadow-sm overflow-hidden">
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
    <div className="flex h-full flex-col rounded-2xl border border-[var(--color-border)]/50 bg-white shadow-sm overflow-hidden">
      <div className="panel-header">
        <span>📊 Explainable Evidence</span>
        <span className="text-[10px] font-bold uppercase tracking-wider text-[var(--color-primary)] bg-[var(--color-primary)]/10 px-2 py-0.5 rounded-md">
          {data.length} items
        </span>
      </div>

      <div className="flex-1 overflow-y-auto p-4 space-y-3">
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
      ? "bg-[var(--color-primary)]"
      : item.confidence >= 0.5
        ? "bg-[var(--color-secondary)]"
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
      className="rounded-xl border border-[var(--color-border)]/50 bg-[var(--color-surface)] p-4 transition-all duration-200 hover:shadow-md hover:border-[var(--color-primary)]/30 animate-fade-in"
      style={{ animationDelay: `${index * 0.1}s` }}
    >
      {/* Header: Type + Confidence */}
      <div className="flex items-center justify-between mb-2">
        <span className="text-[9px] font-extrabold uppercase tracking-widest text-[var(--color-primary)]">
          {typeLabel}
        </span>
        <div className="flex items-center gap-2">
          <span className={`text-[11px] font-bold ${confidenceClass}`}>
            {formatConfidence(item.confidence)}
          </span>
        </div>
      </div>

      {/* Claim */}
      <p className="text-xs font-bold leading-relaxed text-[var(--color-text)] mb-3">
        {item.claim}
      </p>

      {/* Confidence Bar */}
      <div
        className="h-1.5 w-full rounded-full bg-[var(--color-border)]/30 mb-3"
        role="progressbar"
        aria-label={`Confidence level: ${Math.round(item.confidence * 100)} percent`}
        aria-valuenow={Math.round(item.confidence * 100)}
        aria-valuemin={0}
        aria-valuemax={100}
      >
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
              className="rounded-md bg-[var(--color-primary)]/10 px-2 py-0.5 text-[9px] font-extrabold uppercase tracking-wider text-[var(--color-primary)]"
            >
              {source}
            </span>
          ))}
        </div>
      )}
    </div>
  );
}
