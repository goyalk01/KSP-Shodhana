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
      <div className="flex h-full flex-col rounded-2xl border border-[var(--color-border)]/50 bg-white shadow-sm overflow-hidden">
        <div className="panel-header">
          <span>📅 Investigation Timeline</span>
        </div>
        <div className="flex flex-1 items-center justify-center text-sm text-[var(--color-text-dim)]">
          No timeline data available
        </div>
      </div>
    );
  }

  // Helper to extract date and type safely regardless of backend property naming
  const getEventDate = (e: any) => e.eventDate || e.date || "";
  const getEventType = (e: any) => e.eventType || e.type || "Event";

  // Sort by date descending
  const sorted = [...data].sort((a, b) => {
    const timeA = new Date(getEventDate(a).replace(" ", "T")).getTime() || 0;
    const timeB = new Date(getEventDate(b).replace(" ", "T")).getTime() || 0;
    return timeB - timeA;
  });

  return (
    <div className="flex h-full flex-col rounded-2xl border border-[var(--color-border)]/50 bg-white shadow-sm overflow-hidden">
      <div className="panel-header">
        <span>📅 Investigation Timeline</span>
        <span className="text-[10px] font-bold uppercase tracking-wider text-[var(--color-primary)] bg-[var(--color-primary)]/10 px-2 py-0.5 rounded-md">
          {data.length} events
        </span>
      </div>

      <div className="flex-1 overflow-y-auto p-5">
        <div className="relative">
          {/* Vertical dashed line */}
          <div className="absolute left-[15px] top-2 bottom-2 w-0.5 border-l-2 border-dashed border-[var(--color-border)]" />
 
          {/* Events */}
          <div className="space-y-4">
            {sorted.map((event, idx) => {
              const eventDate = getEventDate(event);
              const eventType = getEventType(event);
              const icon = EVENT_TYPE_ICONS[eventType] || EVENT_TYPE_ICONS.Default;
              const formattedDateStr = formatDate(eventDate);
              return (
                <div
                  key={event.id || idx}
                  className="relative flex gap-4 pl-10 animate-fade-in"
                  style={{ animationDelay: `${idx * 0.08}s` }}
                >
                  {/* Dot */}
                  <div className="absolute left-[9px] top-2.5 h-3.5 w-3.5 rounded-full border-2 border-[var(--color-primary)] bg-white shadow-soft" />
 
                  {/* Content Card */}
                  <div 
                    tabIndex={0}
                    aria-label={`Timeline event: ${event.title}, ${formattedDateStr}`}
                    className="flex-1 rounded-xl border border-[var(--color-border)]/50 bg-[var(--color-surface)] p-4 shadow-sm hover:shadow-md hover:border-[var(--color-primary)]/30 focus-visible:ring-2 focus-visible:ring-[var(--color-primary)]"
                  >
                    <div className="flex items-start justify-between gap-2 mb-2">
                      <div className="flex items-center gap-2">
                        <span className="text-sm">{icon}</span>
                        <h4 className="text-xs font-bold text-[var(--color-text)] leading-tight">
                          {event.title}
                        </h4>
                      </div>
                      <span className="shrink-0 rounded-md bg-[var(--color-primary)]/10 px-2 py-0.5 text-[9px] font-extrabold tracking-wider uppercase text-[var(--color-primary)]">
                        {eventType}
                      </span>
                    </div>
                    <p className="text-[11px] font-bold text-[var(--color-text-muted)] leading-relaxed">
                      {event.description}
                    </p>
                    {formattedDateStr && (
                      <p className="mt-2 text-[9px] font-bold text-[var(--color-text-dim)] tracking-wide">
                        {formattedDateStr}
                      </p>
                    )}
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
