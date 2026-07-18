"use client";

import { useWorkspaceStore } from "@/stores/workspaceStore";

export default function WorkspaceHeader() {
  const { clearWorkspace, isQuerying } = useWorkspaceStore();

  return (
    <header className="glass flex h-14 items-center justify-between px-4 border-b border-[var(--color-border)]">
      {/* Logo + Title */}
      <div className="flex items-center gap-3">
        <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-to-br from-[var(--color-primary)] to-[var(--color-accent)]">
          <span className="text-sm font-bold text-white">KS</span>
        </div>
        <div>
          <h1 className="text-sm font-semibold tracking-tight text-[var(--color-text)]">
            KSP Shodhana
          </h1>
          <p className="text-[10px] text-[var(--color-text-dim)] tracking-wider uppercase">
            Ask · Analyze · Act
          </p>
        </div>
      </div>

      {/* Status + Actions */}
      <div className="flex items-center gap-3">
        {isQuerying && (
          <div className="flex items-center gap-2 text-xs text-[var(--color-accent)]">
            <div className="h-2 w-2 rounded-full bg-[var(--color-accent)] animate-pulse" />
            Processing...
          </div>
        )}
        <button
          onClick={() => window.open("/api/proxy/api/v1/reports/1/preview", "_blank")}
          className="rounded-md border border-[var(--color-border)] px-3 py-1.5 text-xs font-medium text-[var(--color-primary)] transition-colors hover:bg-[var(--color-primary)]/10 hover:text-[var(--color-primary-hover)]"
        >
          📄 Export Dossier
        </button>
        <button
          onClick={clearWorkspace}
          className="rounded-md px-3 py-1.5 text-xs font-medium text-[var(--color-text-muted)] transition-colors hover:bg-[var(--color-surface-hover)] hover:text-[var(--color-text)]"
        >
          New Session
        </button>
      </div>
    </header>
  );
}
