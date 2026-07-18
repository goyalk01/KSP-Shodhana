"use client";

import type { TimelineEvent } from "@/types/domain";
import { formatDate } from "@/lib/utils";
import { EVENT_TYPE_ICONS } from "@/features/timeline/types";

interface TimelinePanelProps {
  data: TimelineEvent[] | null;
}

export default function TimelinePanel({ data }: TimelinePanelProps) {
  if (!data || data.length === 0) {
    return (
      <div className="panel flex h-full flex-col">
        <div className="panel-header">
          <span>📅 Investigation Timeline</span>
        </div>
        <div className="flex flex-1 items-center justify-center text-sm text-[var(--color-text-dim)]">
          No timeline data available
        </div>
      </div>
    );
  }

  // Sort by date descending
  const sorted = [...data].sort(
    (a, b) => new Date(b.eventDate).getTime() - new Date(a.eventDate).getTime()
  );

  return (
    <div className="panel flex h-full flex-col">
      <div className="panel-header">
        <span>📅 Investigation Timeline</span>
        <span className="text-[10px] font-normal normal-case tracking-normal text-[var(--color-text-dim)]">
          {data.length} events
        </span>
      </div>

      <div className="flex-1 overflow-y-auto p-4">
        <div className="relative">
          {/* Vertical line */}
          <div className="absolute left-[15px] top-2 bottom-2 w-px bg-gradient-to-b from-[var(--color-primary)] via-[var(--color-accent)] to-[var(--color-border)]" />

          {/* Events */}
          <div className="space-y-4">
            {sorted.map((event, idx) => {
              const icon = EVENT_TYPE_ICONS[event.eventType] || EVENT_TYPE_ICONS.Default;
              return (
                <div
                  key={event.id}
                  className="relative flex gap-4 pl-10 animate-fade-in"
                  style={{ animationDelay: `${idx * 0.08}s` }}
                >
                  {/* Dot */}
                  <div className="absolute left-[10px] top-1 h-3 w-3 rounded-full border-2 border-[var(--color-primary)] bg-[var(--color-background)]" />

                  {/* Content */}
                  <div className="flex-1 rounded-lg border border-[var(--color-border)] bg-[var(--color-surface)] p-3 transition-colors hover:bg-[var(--color-surface-hover)]">
                    <div className="flex items-start justify-between gap-2">
                      <div className="flex items-center gap-2">
                        <span className="text-sm">{icon}</span>
                        <h4 className="text-xs font-semibold text-[var(--color-text)]">
                          {event.title}
                        </h4>
                      </div>
                      <span className="shrink-0 rounded bg-[var(--color-primary)]/10 px-2 py-0.5 text-[10px] font-medium text-[var(--color-primary)]">
                        {event.eventType}
                      </span>
                    </div>
                    <p className="mt-1.5 text-[11px] text-[var(--color-text-muted)] leading-relaxed">
                      {event.description}
                    </p>
                    <p className="mt-2 text-[10px] text-[var(--color-text-dim)]">
                      {formatDate(event.eventDate)}
                    </p>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
}
