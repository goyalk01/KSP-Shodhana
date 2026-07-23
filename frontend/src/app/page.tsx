"use client";

import { useWorkspaceStore } from "@/stores/workspaceStore";
import ChatPanel from "@/features/chat/components/ChatPanel";
import VisualizationGrid from "@/features/workspace/components/VisualizationGrid";
import WorkspaceHeader from "@/features/workspace/components/WorkspaceHeader";
import WelcomeState from "@/features/workspace/components/WelcomeState";
import Sidebar from "@/features/workspace/components/Sidebar";
import SettingsPanel from "@/features/workspace/components/SettingsPanel";

export default function WorkspacePage() {
  const { messages, activeVisualizations, activeTab } = useWorkspaceStore();

  const hasMessages = messages.length > 0;
  const hasVisualizations = activeVisualizations.length > 0;

  return (
    <div className="flex h-screen overflow-hidden bg-[var(--color-background)]">
      {/* Left Sidebar Navigation */}
      <Sidebar />

      {/* Main Content Area */}
      <div className="flex flex-1 flex-col overflow-hidden">
        {/* Header */}
        <WorkspaceHeader />

        {/* Body */}
        <div className="flex flex-1 overflow-hidden">
          {activeTab === "settings" ? (
            <SettingsPanel />
          ) : (
            <>
              {/* Chat Sidebar */}
              <div className="flex w-[340px] min-w-[300px] flex-col p-3 pr-0">
                <ChatPanel />
              </div>

              {/* Visualization Area */}
              <div className="flex flex-1 flex-col overflow-hidden">
                {hasVisualizations ? (
                  <VisualizationGrid />
                ) : (
                  <WelcomeState showMinimal={hasMessages} />
                )}
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
