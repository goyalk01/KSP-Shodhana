"use client";

import { useWorkspaceStore } from "@/stores/workspaceStore";

interface SuggestionChipsProps {
  suggestions: string[];
}

export default function SuggestionChips({ suggestions }: SuggestionChipsProps) {
  const { sendQuery, isQuerying } = useWorkspaceStore();

  return (
    <div className="border-t border-[var(--color-border-subtle)] px-4 py-2">
      <p className="mb-1.5 text-[10px] font-medium uppercase tracking-wider text-[var(--color-text-dim)]">
        Suggested
      </p>
      <div className="flex flex-wrap gap-1.5">
        {suggestions.slice(0, 3).map((suggestion, idx) => (
          <button
            key={idx}
            onClick={() => !isQuerying && sendQuery(suggestion)}
            disabled={isQuerying}
            className="rounded-full border border-[var(--color-border)] bg-[var(--color-surface)] px-3 py-1 text-[11px] text-[var(--color-text-muted)] transition-all hover:border-[var(--color-primary)]/40 hover:text-[var(--color-primary)] hover:bg-[var(--color-primary)]/5 disabled:opacity-50"
          >
            {suggestion}
          </button>
        ))}
      </div>
    </div>
  );
}
