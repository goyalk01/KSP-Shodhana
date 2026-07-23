"use client";

import { useEffect, useState } from "react";
import { useWorkspaceStore } from "@/stores/workspaceStore";

export default function SettingsPanel() {
  const {
    geminiModel,
    geminiApiKey,
    defaultDistrict,
    localFallbackActive,
    settingsLoading,
    settingsSuccessMessage,
    fetchSettings,
    updateSettings,
  } = useWorkspaceStore();

  // Local state for UI settings
  const [station, setStation] = useState("Shivajinagar PS");
  const [language, setLanguage] = useState("English");
  const [refreshInterval, setRefreshInterval] = useState("Real-time");
  const [district, setDistrict] = useState(defaultDistrict);
  const [fallback, setFallback] = useState(localFallbackActive);

  // Sync state with store on load
  useEffect(() => {
    fetchSettings();
  }, [fetchSettings]);

  useEffect(() => {
    setDistrict(defaultDistrict);
    setFallback(localFallbackActive);
  }, [defaultDistrict, localFallbackActive]);

  const handleSave = () => {
    // Keep backend settings (model & API key) intact under-the-hood
    updateSettings({
      geminiModel: geminiModel,
      geminiApiKey: geminiApiKey,
      defaultDistrict: district,
      localFallbackActive: fallback,
    });
  };

  return (
    <div className="flex-1 p-6 pb-20 overflow-y-auto max-w-4xl mx-auto w-full animate-fade-in">
      <div className="rounded-2xl border border-[var(--color-border)]/50 bg-white p-7 shadow-sm mb-10">
        {/* Title */}
        <div className="border-b border-[var(--color-border)]/50 pb-5 mb-6">
          <h2 className="font-serif text-xl font-extrabold text-[var(--color-text)]">
            ⚙️ Workspace Settings
          </h2>
          <p className="text-xs font-semibold text-[var(--color-text-muted)] mt-1">
            Configure investigation defaults, preferred interface options, and local overrides.
          </p>
        </div>

        {/* Form Fields */}
        <div className="space-y-6">
          {/* Primary Police Station */}
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-[var(--color-text)] mb-2">
              Primary Police Station
            </label>
            <select
              value={station}
              onChange={(e) => setStation(e.target.value)}
              className="w-full rounded-lg border border-[var(--color-border)] bg-white px-3 py-2.5 text-xs font-bold text-[var(--color-text)] focus:border-[var(--color-primary)]/50 focus:outline-none cursor-pointer"
            >
              <option value="Shivajinagar PS">Shivajinagar PS (Bengaluru)</option>
              <option value="MG Road PS">MG Road PS (Bengaluru)</option>
              <option value="Koramangala PS">Koramangala PS (Bengaluru)</option>
              <option value="Whitefield PS">Whitefield PS (Bengaluru)</option>
              <option value="Sayyaji Rao Road PS">Sayyaji Rao Road PS (Mysuru)</option>
              <option value="Vidyanagar PS">Vidyanagar PS (Hubballi)</option>
            </select>
            <p className="mt-1.5 text-[10px] font-semibold text-[var(--color-text-dim)]">
              Specify your primary duty station to filter incoming cases and dispatch warnings.
            </p>
          </div>

          {/* District Jurisdiction */}
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-[var(--color-text)] mb-2">
              Default Jurisdiction / District
            </label>
            <select
              value={district}
              onChange={(e) => setDistrict(e.target.value)}
              className="w-full rounded-lg border border-[var(--color-border)] bg-white px-3 py-2.5 text-xs font-bold text-[var(--color-text)] focus:border-[var(--color-primary)]/50 focus:outline-none cursor-pointer"
            >
              <option value="Bengaluru Urban">Bengaluru Urban</option>
              <option value="Mysuru">Mysuru</option>
              <option value="Hubballi-Dharwad">Hubballi-Dharwad</option>
              <option value="Mangaluru">Mangaluru</option>
            </select>
            <p className="mt-1.5 text-[10px] font-semibold text-[var(--color-text-dim)]">
              Defines the coordinate center and geographical bounds for intelligence mapping.
            </p>
          </div>

          {/* Interface Language */}
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-[var(--color-text)] mb-2">
              Interface Language / ಭಾಷೆ
            </label>
            <select
              value={language}
              onChange={(e) => setLanguage(e.target.value)}
              className="w-full rounded-lg border border-[var(--color-border)] bg-white px-3 py-2.5 text-xs font-bold text-[var(--color-text)] focus:border-[var(--color-primary)]/50 focus:outline-none cursor-pointer"
            >
              <option value="English">English (Default)</option>
              <option value="Kannada">Kannada (ಕನ್ನಡ)</option>
            </select>
            <p className="mt-1.5 text-[10px] font-semibold text-[var(--color-text-dim)]">
              Choose the primary language for reports, notifications, and menu titles.
            </p>
          </div>

          {/* Auto-Refresh Rate */}
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-[var(--color-text)] mb-2">
              Intelligence Stream Refresh Interval
            </label>
            <select
              value={refreshInterval}
              onChange={(e) => setRefreshInterval(e.target.value)}
              className="w-full rounded-lg border border-[var(--color-border)] bg-white px-3 py-2.5 text-xs font-bold text-[var(--color-text)] focus:border-[var(--color-primary)]/50 focus:outline-none cursor-pointer"
            >
              <option value="Real-time">Real-time (Active Sync)</option>
              <option value="5-min">Every 5 Minutes</option>
              <option value="Manual">Manual Refresh Only</option>
            </select>
            <p className="mt-1.5 text-[10px] font-semibold text-[var(--color-text-dim)]">
              Controls how frequently the workspace polls the database for updated criminal filings.
            </p>
          </div>

          {/* Offline Fallback Override */}
          <div className="flex items-center justify-between p-4 rounded-xl border border-[var(--color-border)]/50 bg-[var(--color-background)]/35">
            <div className="pr-4">
              <h4 className="text-xs font-bold text-[var(--color-text)]">
                Local Heuristics Fallback (Offline Mode)
              </h4>
              <p className="text-[10px] font-semibold text-[var(--color-text-muted)] mt-0.5 leading-relaxed">
                Bypasses standard LLM calls and utilizes rule-based local parsers for instant processing. Ideal for networks with strict security regulations or during offline sessions.
              </p>
            </div>
            <button
              type="button"
              onClick={() => setFallback(!fallback)}
              className={`relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none ${
                fallback ? "bg-[var(--color-primary)]" : "bg-[var(--color-border)]"
              }`}
            >
              <span
                className={`pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out ${
                  fallback ? "translate-x-5" : "translate-x-0"
                }`}
              />
            </button>
          </div>
        </div>

        {/* Footer Actions */}
        <div className="flex items-center justify-between mt-8 pt-5 border-t border-[var(--color-border)]/50">
          <div className="flex items-center gap-2">
            {settingsSuccessMessage && (
              <span className="text-[11px] font-bold text-[var(--color-success)] animate-fade-in">
                ✅ {settingsSuccessMessage}
              </span>
            )}
          </div>
          <button
            onClick={handleSave}
            disabled={settingsLoading}
            className="flex items-center gap-2 rounded-lg bg-[var(--color-primary)] px-6 py-2.5 text-xs font-bold text-white shadow-sm hover:bg-[var(--color-primary-hover)] active:scale-[0.98] disabled:opacity-50 cursor-pointer"
          >
            {settingsLoading ? "Saving..." : "Save Settings"}
          </button>
        </div>
      </div>
    </div>
  );
}
