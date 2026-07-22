"use client";

import { useWorkspaceStore } from "@/stores/workspaceStore";

export default function WorkspaceHeader() {
  const { clearWorkspace, isQuerying } = useWorkspaceStore();

  return (
    <header className="glass flex h-16 items-center justify-between px-6 mx-4 mt-4 mb-2 rounded-2xl border border-[var(--color-border)] shadow-soft min-w-0">
      {/* Logo + Title */}
      <div className="flex items-center gap-3 shrink-0">
        <div className="flex h-9 w-9 items-center justify-center rounded-full bg-gradient-to-br from-[var(--color-primary)] to-[var(--color-secondary)] shadow-soft">
          <span className="text-xs font-bold text-white tracking-wider">KS</span>
        </div>
        <div>
          <h1 className="font-serif text-base font-extrabold tracking-tight text-[var(--color-text)] leading-tight">
            KSP Shodhana
          </h1>
          <p className="text-[9px] font-semibold text-[var(--color-text-dim)] tracking-widest uppercase">
            Ask · Analyze · Act
          </p>
        </div>
      </div>

      {/* Status + Actions */}
      <div className="flex items-center gap-2 sm:gap-3 shrink-0">
        {isQuerying && (
          <div className="flex items-center gap-2 rounded-full border border-[var(--color-secondary)]/30 bg-[var(--color-secondary)]/10 px-3 py-1.5 text-xs font-bold text-[var(--color-secondary)] animate-pulse">
            <span className="relative flex h-2 w-2">
              <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-[var(--color-secondary)] opacity-75"></span>
              <span className="relative inline-flex h-2 w-2 rounded-full bg-[var(--color-secondary)]"></span>
            </span>
            Analyzing data...
          </div>
        )}
        <button
          onClick={() => window.open("/api/proxy/api/v1/reports/1/preview", "_blank")}
          aria-label="Export official police case dossier preview in new tab"
          title="Export official police case dossier preview in new tab"
          className="rounded-full bg-[var(--color-primary)] px-5 py-2 text-xs font-bold text-[var(--color-primary-foreground)] shadow-soft transition-all duration-300 hover:scale-105 hover:bg-[var(--color-primary-hover)] active:scale-95 cursor-pointer focus-visible:ring-2 focus-visible:ring-[var(--color-primary)]"
        >
          📄 Export Dossier
        </button>
        <button
          onClick={clearWorkspace}
          aria-label="Start a new investigation session and clear current workspace"
          title="Start a new investigation session and clear current workspace"
          className="rounded-full border border-[var(--color-border)] bg-[var(--color-surface)] px-5 py-2 text-xs font-bold text-[var(--color-text-muted)] transition-all duration-300 hover:scale-105 hover:bg-[var(--color-surface-hover)] hover:text-[var(--color-text)] active:scale-95 cursor-pointer focus-visible:ring-2 focus-visible:ring-[var(--color-primary)]"
        >
          New Session
        </button>
      </div>
    </header>
  );
}
