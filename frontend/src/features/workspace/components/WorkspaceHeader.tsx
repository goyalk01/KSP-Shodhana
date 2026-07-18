"use client";

import { useWorkspaceStore } from "@/stores/workspaceStore";

export default function WorkspaceHeader() {
  const { clearWorkspace, isQuerying } = useWorkspaceStore();

  return (
    <header className="glass flex h-16 items-center justify-between px-6 mx-4 mt-4 mb-2 rounded-full border border-[var(--color-border)] shadow-soft">
      {/* Logo + Title */}
      <div className="flex items-center gap-3">
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
      <div className="flex items-center gap-3">
        {isQuerying && (
          <div className="flex items-center gap-2 text-xs font-bold text-[var(--color-secondary)]">
            <div className="h-2.5 w-2.5 rounded-full bg-[var(--color-secondary)] animate-pulse" />
            Analyzing data...
          </div>
        )}
        <button
          onClick={() => window.open("/api/proxy/api/v1/reports/1/preview", "_blank")}
          className="rounded-full bg-[var(--color-primary)] px-5 py-2 text-xs font-bold text-[var(--color-primary-foreground)] shadow-soft transition-all duration-300 hover:scale-105 hover:bg-[var(--color-primary-hover)] active:scale-95 cursor-pointer"
        >
          📄 Export Dossier
        </button>
        <button
          onClick={clearWorkspace}
          className="rounded-full border border-[var(--color-border)] bg-[var(--color-surface)] px-5 py-2 text-xs font-bold text-[var(--color-text-muted)] transition-all duration-300 hover:scale-105 hover:bg-[var(--color-surface-hover)] hover:text-[var(--color-text)] active:scale-95 cursor-pointer"
        >
          New Session
        </button>
      </div>
    </header>
  );
}
