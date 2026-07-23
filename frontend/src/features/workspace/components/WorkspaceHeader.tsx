"use client";

import { useWorkspaceStore } from "@/stores/workspaceStore";

export default function WorkspaceHeader() {
  const { clearWorkspace, isQuerying, setActiveTab } = useWorkspaceStore();

  const handleHomeClick = () => {
    setActiveTab("dashboard");
    clearWorkspace();
  };

  return (
    <header className="flex h-[72px] items-center justify-between px-6 sm:px-8 border-b border-[var(--color-border)]/50 bg-[var(--color-surface)] shrink-0 min-w-0">
      {/* Title + Subtitle */}
      <button
        onClick={handleHomeClick}
        aria-label="Return to Home Dashboard"
        title="Return to Home Dashboard"
        className="min-w-0 pr-4 shrink text-left cursor-pointer group"
      >
        <h1 className="font-serif text-base sm:text-lg lg:text-xl font-extrabold tracking-tight text-[var(--color-text)] leading-tight whitespace-nowrap group-hover:text-[var(--color-primary)] transition-colors">
          KSP Shodhana — Investigation Workspace
        </h1>
        <p className="text-[11px] font-semibold text-[var(--color-text-muted)] mt-0.5 whitespace-nowrap hidden sm:block">
          Here&apos;s what&apos;s happening across Karnataka today.
        </p>
      </button>

      {/* Status + Actions */}
      <div className="flex items-center gap-2.5 sm:gap-3 shrink-0">
        {isQuerying && (
          <div className="flex items-center gap-2 rounded-lg border border-[var(--color-secondary)]/30 bg-[var(--color-secondary)]/10 px-3 py-1.5 text-xs font-bold text-[var(--color-secondary)] whitespace-nowrap shrink-0">
            <span className="relative flex h-2 w-2">
              <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-[var(--color-secondary)] opacity-75"></span>
              <span className="relative inline-flex h-2 w-2 rounded-full bg-[var(--color-secondary)]"></span>
            </span>
            Analyzing...
          </div>
        )}
        <button
          onClick={() => window.open("/api/proxy/api/v1/reports/1/preview", "_blank")}
          aria-label="Export official police case dossier preview"
          title="Export official police case dossier preview"
          className="flex items-center gap-2 rounded-lg border border-[var(--color-primary)]/30 bg-[var(--color-primary)]/10 px-4 py-2 text-xs font-bold text-[var(--color-primary)] transition-all duration-200 hover:bg-[var(--color-primary)]/20 active:scale-[0.97] cursor-pointer whitespace-nowrap shrink-0"
        >
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-4 h-4 shrink-0">
            <path d="M13.75 7h-3v5.296l1.943-2.048a.75.75 0 0 1 1.114 1.004l-3.25 3.5a.75.75 0 0 1-1.114 0l-3.25-3.5a.75.75 0 1 1 1.114-1.004l1.943 2.048V7h-3A2.25 2.25 0 0 0 4 9.25v7.5A2.25 2.25 0 0 0 6.25 19h7.5A2.25 2.25 0 0 0 16 16.75v-7.5A2.25 2.25 0 0 0 13.75 7Z" />
            <path d="M10 1a.75.75 0 0 1 .75.75v5.5a.75.75 0 0 1-1.5 0v-5.5A.75.75 0 0 1 10 1Z" />
          </svg>
          <span className="whitespace-nowrap">Export Dossier</span>
        </button>
        <button
          onClick={clearWorkspace}
          aria-label="Start a new investigation session"
          title="Start a new investigation session"
          className="flex items-center gap-2 rounded-lg bg-[var(--color-primary)] px-4 py-2 text-xs font-bold text-white shadow-sm transition-all duration-200 hover:bg-[var(--color-primary-hover)] active:scale-[0.97] cursor-pointer whitespace-nowrap shrink-0"
        >
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-4 h-4 shrink-0">
            <path d="M10.75 4.75a.75.75 0 0 0-1.5 0v4.5h-4.5a.75.75 0 0 0 0 1.5h4.5v4.5a.75.75 0 0 0 1.5 0v-4.5h4.5a.75.75 0 0 0 0-1.5h-4.5v-4.5Z" />
          </svg>
          <span className="whitespace-nowrap">New Session</span>
        </button>
      </div>
    </header>
  );
}
