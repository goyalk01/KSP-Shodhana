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
        <p className="text-xs font-semibold text-[var(--color-text-dim)]">
          Visualizations will appear here based on your queries
        </p>
      </div>
    );
  }

  // Balanced, aesthetic card shapes
  const cardShapes = [
    "rounded-2xl rounded-tr-3xl",
    "rounded-2xl rounded-bl-3xl",
    "rounded-2xl rounded-tl-3xl",
    "rounded-2xl rounded-br-3xl",
    "rounded-2xl",
  ];

  return (
    <div className="flex flex-1 flex-col items-center justify-center gap-8 p-8 animate-fade-in overflow-y-auto">
      {/* Hero */}
      <div className="text-center">
        <div 
          className="mx-auto mb-5 flex h-16 w-16 items-center justify-center bg-gradient-to-br from-[var(--color-primary)] to-[var(--color-secondary)] shadow-soft"
          style={{ borderRadius: "60% 40% 30% 70% / 60% 30% 70% 40%" }}
        >
          <span className="text-2xl">🔍</span>
        </div>
        <h2 className="font-serif text-3xl font-extrabold tracking-tight text-[var(--color-text)]">
          KSP Shodhana
        </h2>
        <p className="mt-1.5 text-xs font-bold uppercase tracking-widest text-[var(--color-secondary)]">
          AI-Powered Crime Intelligence Workspace
        </p>
        <p className="mt-3.5 max-w-md text-xs font-semibold text-[var(--color-text-muted)] leading-relaxed">
          Ask questions in English or Kannada about crimes, criminals, and investigations.
          The AI will analyze data and show relevant visualizations automatically.
        </p>
      </div>

      {/* Example Queries */}
      <div className="grid w-full max-w-3xl grid-cols-1 gap-4 sm:grid-cols-2">
        {EXAMPLE_QUERIES.map((query, idx) => {
          const shapeClass = cardShapes[idx % cardShapes.length];
          const rotateClass = idx % 2 === 0 ? "hover:rotate-1" : "hover:-rotate-1";
          
          return (
            <button
              key={idx}
              onClick={() => !isQuerying && sendQuery(query.text)}
              disabled={isQuerying}
              aria-label={`Run query: ${query.text}`}
              title={`Run query: ${query.text}`}
              className={`group flex items-start gap-3.5 border border-[var(--color-border)]/50 bg-white p-4.5 pl-4 pr-5 text-left shadow-soft transition-all duration-300 ${shapeClass} ${rotateClass} hover:-translate-y-1 hover:shadow-lg hover:border-[var(--color-primary)]/40 hover:shadow-[var(--color-primary)]/5 disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer focus-visible:ring-2 focus-visible:ring-[var(--color-primary)]`}
            >
              <span className="text-xl shrink-0 mt-0.5">{query.icon}</span>
              <div className="flex-1 min-w-0">
                <p className="text-[9px] font-bold uppercase tracking-widest text-[var(--color-primary)] mb-1">
                  {query.label}
                </p>
                <p className="text-xs font-bold text-[var(--color-text-muted)] group-hover:text-[var(--color-text)] transition-colors leading-snug break-words">
                  {query.text}
                </p>
              </div>
            </button>
          );
        })}
      </div>

      {/* Footer hint */}
      <p className="text-[10px] font-bold text-[var(--color-text-dim)] tracking-wide mt-2">
        💡 Try asking in Kannada: &quot;ಬೆಂಗಳೂರಿನಲ್ಲಿ ಕಳ್ಳತನ ಪ್ರಕರಣಗಳನ್ನು ತೋರಿಸಿ&quot;
      </p>
    </div>
  );
}
