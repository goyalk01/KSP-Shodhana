"use client";

import { useWorkspaceStore } from "@/stores/workspaceStore";

const EXAMPLE_QUERIES = [
  {
    icon: "🔍",
    text: "Show all theft cases in Bengaluru this month",
    label: "Search Crimes",
  },
  {
    icon: "🕸️",
    text: "Show the criminal network of Ravi Kumar",
    label: "Network Graph",
  },
  {
    icon: "🗺️",
    text: "Show crime hotspots in Karnataka",
    label: "Heatmap",
  },
  {
    icon: "📋",
    text: "Show the investigation timeline for FIR KA/2026/00101",
    label: "Timeline",
  },
  {
    icon: "🔗",
    text: "Which criminals are linked to the Mysuru jewelry robbery?",
    label: "Evidence",
  },
];

interface WelcomeStateProps {
  showMinimal?: boolean;
}

export default function WelcomeState({ showMinimal = false }: WelcomeStateProps) {
  const { sendQuery, isQuerying } = useWorkspaceStore();

  if (showMinimal) {
    return (
      <div className="flex flex-1 items-center justify-center p-8">
        <p className="text-sm text-[var(--color-text-dim)]">
          Visualizations will appear here based on your queries
        </p>
      </div>
    );
  }

  return (
    <div className="flex flex-1 flex-col items-center justify-center gap-8 p-8 animate-fade-in">
      {/* Hero */}
      <div className="text-center">
        <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-gradient-to-br from-[var(--color-primary)] to-[var(--color-accent)] shadow-lg shadow-[var(--color-primary)]/20">
          <span className="text-2xl">🔍</span>
        </div>
        <h2 className="text-2xl font-bold tracking-tight text-[var(--color-text)]">
          KSP Shodhana
        </h2>
        <p className="mt-1 text-sm text-[var(--color-text-muted)]">
          AI-Powered Crime Intelligence Workspace
        </p>
        <p className="mt-3 max-w-md text-xs text-[var(--color-text-dim)] leading-relaxed">
          Ask questions in English or Kannada about crimes, criminals, and investigations.
          The AI will analyze data and show relevant visualizations automatically.
        </p>
      </div>

      {/* Example Queries */}
      <div className="grid w-full max-w-2xl grid-cols-1 gap-2 sm:grid-cols-2">
        {EXAMPLE_QUERIES.map((query, idx) => (
          <button
            key={idx}
            onClick={() => !isQuerying && sendQuery(query.text)}
            disabled={isQuerying}
            className="group flex items-start gap-3 rounded-xl border border-[var(--color-border)] bg-[var(--color-surface)] p-4 text-left transition-all duration-200 hover:border-[var(--color-primary)]/40 hover:bg-[var(--color-surface-hover)] hover:shadow-lg hover:shadow-[var(--color-primary)]/5 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <span className="text-lg">{query.icon}</span>
            <div>
              <p className="text-[10px] font-semibold uppercase tracking-wider text-[var(--color-primary)] mb-1">
                {query.label}
              </p>
              <p className="text-xs text-[var(--color-text-muted)] group-hover:text-[var(--color-text)] transition-colors">
                {query.text}
              </p>
            </div>
          </button>
        ))}
      </div>

      {/* Footer hint */}
      <p className="text-[11px] text-[var(--color-text-dim)]">
        💡 Try asking in Kannada: &quot;ಬೆಂಗಳೂರಿನಲ್ಲಿ ಕಳ್ಳತನ ಪ್ರಕರಣಗಳನ್ನು ತೋರಿಸಿ&quot;
      </p>
    </div>
  );
}
