"use client";

import { useWorkspaceStore } from "@/stores/workspaceStore";

interface SuggestionChipsProps {
  suggestions: string[];
}

export default function SuggestionChips({ suggestions }: SuggestionChipsProps) {
  const { sendQuery, isQuerying } = useWorkspaceStore();

  return (
    <div className="border-t border-[var(--color-border)]/50 px-6 py-3 bg-white/10">
      <p className="mb-1.5 text-[9px] font-bold uppercase tracking-widest text-[var(--color-primary)]">
        Suggested Follow-Ups
      </p>
      <div className="flex flex-wrap gap-1.5">
        {suggestions.slice(0, 3).map((suggestion, idx) => (
          <button
            key={idx}
            onClick={() => !isQuerying && sendQuery(suggestion)}
            disabled={isQuerying}
            className="rounded-full border border-[var(--color-border)] bg-white px-3.5 py-1 text-[10px] font-bold text-[var(--color-text-muted)] shadow-soft transition-all duration-300 hover:scale-105 hover:border-[var(--color-primary)]/50 hover:text-[var(--color-primary)] hover:bg-[var(--color-primary)]/5 disabled:opacity-50 active:scale-95 cursor-pointer"
          >
            {suggestion}
          </button>
        ))}
      </div>
    </div>
  );
}
