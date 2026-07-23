"use client";

import { useWorkspaceStore } from "@/stores/workspaceStore";

const QUICK_ACTIONS = [
  {
    icon: (
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-5 h-5">
        <path fillRule="evenodd" d="M9 3.5a5.5 5.5 0 1 0 0 11 5.5 5.5 0 0 0 0-11ZM2 9a7 7 0 1 1 12.452 4.391l3.328 3.329a.75.75 0 1 1-1.06 1.06l-3.329-3.328A7 7 0 0 1 2 9Z" clipRule="evenodd" />
      </svg>
    ),
    label: "Search Crimes",
    sub: "Query case records",
    query: "Show all theft cases in Bengaluru this month",
    color: "bg-[var(--color-primary)]",
  },
  {
    icon: (
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-5 h-5">
        <path d="M10 9a3 3 0 1 0 0-6 3 3 0 0 0 0 6ZM6 8a2 2 0 1 1-4 0 2 2 0 0 1 4 0ZM1.49 15.326a.78.78 0 0 1-.358-.442 3 3 0 0 1 4.308-3.516 6.484 6.484 0 0 0-1.905 3.959c-.023.222-.014.442.025.654a4.97 4.97 0 0 1-2.07-.655ZM16.44 15.98a4.97 4.97 0 0 0 2.07-.654.78.78 0 0 0 .357-.442 3 3 0 0 0-4.308-3.517 6.484 6.484 0 0 1 1.907 3.96 2.32 2.32 0 0 1-.026.654ZM18 8a2 2 0 1 1-4 0 2 2 0 0 1 4 0ZM5.304 16.19a.844.844 0 0 1-.277-.71 5 5 0 0 1 9.947 0 .843.843 0 0 1-.277.71A6.975 6.975 0 0 1 10 18a6.974 6.974 0 0 1-4.696-1.81Z" />
      </svg>
    ),
    label: "Criminal Network",
    sub: "Map connections",
    query: "Show the criminal network of Ravi Kumar",
    color: "bg-[var(--color-secondary)]",
  },
  {
    icon: (
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-5 h-5">
        <path fillRule="evenodd" d="m9.69 18.933.003.001C9.89 19.02 10 19 10 19s.11.02.308-.066l.002-.001.006-.003.018-.008a5.741 5.741 0 0 0 .281-.14c.186-.096.446-.24.757-.433.62-.384 1.445-.966 2.274-1.765C15.302 14.988 17 12.493 17 9A7 7 0 1 0 3 9c0 3.492 1.698 5.988 3.355 7.584a13.731 13.731 0 0 0 2.273 1.765 11.842 11.842 0 0 0 .976.544l.062.029.018.008.006.003ZM10 11.25a2.25 2.25 0 1 0 0-4.5 2.25 2.25 0 0 0 0 4.5Z" clipRule="evenodd" />
      </svg>
    ),
    label: "Crime Hotspots",
    sub: "Geographic view",
    query: "Show crime hotspots in Karnataka",
    color: "bg-[var(--color-danger)]",
  },
  {
    icon: (
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-5 h-5">
        <path fillRule="evenodd" d="M5.75 2a.75.75 0 0 1 .75.75V4h7V2.75a.75.75 0 0 1 1.5 0V4h.25A2.75 2.75 0 0 1 18 6.75v8.5A2.75 2.75 0 0 1 15.25 18H4.75A2.75 2.75 0 0 1 2 15.25v-8.5A2.75 2.75 0 0 1 4.75 4H5V2.75A.75.75 0 0 1 5.75 2Zm-1 5.5c-.69 0-1.25.56-1.25 1.25v6.5c0 .69.56 1.25 1.25 1.25h10.5c.69 0 1.25-.56 1.25-1.25v-6.5c0-.69-.56-1.25-1.25-1.25H4.75Z" clipRule="evenodd" />
      </svg>
    ),
    label: "Timeline",
    sub: "Investigation events",
    query: "Show the investigation timeline for FIR KA/2026/00101",
    color: "bg-blue-500",
  },
];

const RECENT_QUERIES = [
  { text: "Which criminals are linked to the Mysuru jewelry robbery?", tag: "EVIDENCE", tagColor: "bg-[var(--color-primary)]" },
  { text: "Show all cases with Critical severity", tag: "CRITICAL", tagColor: "bg-[var(--color-danger)]" },
  { text: "Map crime density in Bengaluru Urban", tag: "HEATMAP", tagColor: "bg-[var(--color-secondary)]" },
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

  return (
    <div className="flex flex-1 flex-col gap-6 p-6 overflow-y-auto animate-fade-in">
      {/* Quick Actions Header */}
      <div>
        <h3 className="text-sm font-extrabold text-[var(--color-text)] mb-3">Quick Actions</h3>
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
          {QUICK_ACTIONS.map((action, idx) => (
            <button
              key={idx}
              onClick={() => !isQuerying && sendQuery(action.query)}
              disabled={isQuerying}
              aria-label={`Run query: ${action.query}`}
              title={action.query}
              className="group flex items-center gap-3 rounded-xl border border-[var(--color-border)]/50 bg-white p-4 text-left transition-all duration-200 hover:-translate-y-0.5 hover:shadow-md hover:border-[var(--color-primary)]/30 disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer"
            >
              <div className={`flex h-9 w-9 shrink-0 items-center justify-center rounded-lg ${action.color} text-white shadow-sm`}>
                {action.icon}
              </div>
              <div className="min-w-0">
                <p className="text-[13px] font-bold text-[var(--color-text)] leading-tight truncate">{action.label}</p>
                <p className="text-[11px] font-semibold text-[var(--color-text-muted)] truncate">{action.sub}</p>
              </div>
            </button>
          ))}
        </div>
      </div>

      {/* Recent Queries / Tasks */}
      <div className="rounded-xl border border-[var(--color-border)]/50 bg-white overflow-hidden">
        <div className="px-5 py-3.5 border-b border-[var(--color-border)]/50">
          <h3 className="text-sm font-extrabold text-[var(--color-text)]">Suggested Queries</h3>
        </div>
        <div className="divide-y divide-[var(--color-border)]/30">
          {RECENT_QUERIES.map((item, idx) => (
            <button
              key={idx}
              onClick={() => !isQuerying && sendQuery(item.text)}
              disabled={isQuerying}
              className="group flex w-full items-center gap-3 px-5 py-3.5 text-left transition-colors hover:bg-[var(--color-surface-hover)] disabled:opacity-50 cursor-pointer"
            >
              {/* Checkbox circle */}
              <div className="flex h-5 w-5 shrink-0 items-center justify-center rounded-full border-2 border-[var(--color-border)] group-hover:border-[var(--color-primary)] transition-colors">
                <div className="h-2 w-2 rounded-full bg-transparent group-hover:bg-[var(--color-primary)] transition-colors" />
              </div>
              <p className="flex-1 text-[13px] font-semibold text-[var(--color-text)] group-hover:text-[var(--color-primary)] transition-colors truncate min-w-0">
                {item.text}
              </p>
              <span className={`shrink-0 rounded-md px-2.5 py-0.5 text-[10px] font-bold text-white ${item.tagColor}`}>
                {item.tag}
              </span>
            </button>
          ))}
        </div>
      </div>

      {/* Bottom Grid: Stats + Help */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {/* Active Cases Summary */}
        <div className="rounded-xl border border-[var(--color-border)]/50 bg-white p-5">
          <h3 className="text-sm font-extrabold text-[var(--color-text)] mb-3">Active Intelligence</h3>
          <div className="space-y-2.5">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-[var(--color-text-muted)]">Active crime records</span>
              <span className="text-xs font-bold text-[var(--color-text)]">8 cases</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-[var(--color-text-muted)]">Criminal profiles tracked</span>
              <span className="text-xs font-bold text-[var(--color-text)]">6 suspects</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-[var(--color-text-muted)]">Network connections</span>
              <span className="text-xs font-bold text-[var(--color-text)]">5 links</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-[var(--color-text-muted)]">Districts covered</span>
              <span className="text-xs font-bold text-[var(--color-text)]">4 districts</span>
            </div>
          </div>
        </div>

        {/* Language Support */}
        <div className="rounded-xl border border-[var(--color-border)]/50 bg-white p-5">
          <h3 className="text-sm font-extrabold text-[var(--color-text)] mb-3">How to Use</h3>
          <div className="space-y-2.5">
            <div className="flex items-start gap-2">
              <span className="mt-0.5 flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-[var(--color-primary)] text-white text-[10px] font-bold">1</span>
              <p className="text-xs font-semibold text-[var(--color-text-muted)]">Type a question in the chat panel in <strong className="text-[var(--color-text)]">English</strong> or <strong className="text-[var(--color-text)]">Kannada</strong></p>
            </div>
            <div className="flex items-start gap-2">
              <span className="mt-0.5 flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-[var(--color-primary)] text-white text-[10px] font-bold">2</span>
              <p className="text-xs font-semibold text-[var(--color-text-muted)]">AI analyzes data and shows visualizations automatically</p>
            </div>
            <div className="flex items-start gap-2">
              <span className="mt-0.5 flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-[var(--color-primary)] text-white text-[10px] font-bold">3</span>
              <p className="text-xs font-semibold text-[var(--color-text-muted)]">Export findings as an official case dossier</p>
            </div>
          </div>
          <p className="mt-3 text-[10px] font-bold text-[var(--color-text-dim)]">
            Try: &quot;ಬೆಂಗಳೂರಿನಲ್ಲಿ ಕಳ್ಳತನ ಪ್ರಕರಣಗಳನ್ನು ತೋರಿಸಿ&quot;
          </p>
        </div>
      </div>
    </div>
  );
}
