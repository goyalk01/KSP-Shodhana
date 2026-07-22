"use client";

import { useWorkspaceStore } from "@/stores/workspaceStore";

interface SuggestionChipsProps {
  suggestions: string[];
}

export default function SuggestionChips({ suggestions }: SuggestionChipsProps) {
  const { sendQuery, isQuerying } = useWorkspaceStore();

  return (
    <div className="border-t border-[var(--color-border)]/50 px-4 sm:px-6 py-2.5 bg-white/10">
      <p className="mb-1.5 text-[9px] font-bold uppercase tracking-widest text-[var(--color-primary)]">
        Suggested Follow-Ups
      </p>
      <div className="flex gap-1.5 overflow-x-auto pb-1 no-scrollbar max-w-full">
        {suggestions.slice(0, 3).map((suggestion, idx) => (
          <button
            key={idx}
            onClick={() => !isQuerying && sendQuery(suggestion)}
            disabled={isQuerying}
            aria-label={`Suggested query: ${suggestion}`}
            title={`Suggested query: ${suggestion}`}
            className="shrink-0 rounded-full border border-[var(--color-border)] bg-white px-3 py-1 text-[10px] font-bold text-[var(--color-text-muted)] shadow-soft transition-all duration-300 hover:scale-[1.02] hover:border-[var(--color-primary)]/50 hover:text-[var(--color-primary)] hover:bg-[var(--color-primary)]/5 disabled:opacity-50 active:scale-95 cursor-pointer focus-visible:ring-2 focus-visible:ring-[var(--color-primary)] whitespace-nowrap"
          >
            {suggestion}
          </button>
        ))}
      </div>
    </div>
  );
}
