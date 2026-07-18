"use client";

import { useWorkspaceStore } from "@/stores/workspaceStore";
import ChatPanel from "@/features/chat/components/ChatPanel";
import VisualizationGrid from "@/features/workspace/components/VisualizationGrid";
import WorkspaceHeader from "@/features/workspace/components/WorkspaceHeader";
import WelcomeState from "@/features/workspace/components/WelcomeState";

export default function WorkspacePage() {
  const { messages, activeVisualizations } = useWorkspaceStore();

  const hasMessages = messages.length > 0;
  const hasVisualizations = activeVisualizations.length > 0;

  return (
    <div className="flex h-screen flex-col overflow-hidden">
      {/* Header */}
      <WorkspaceHeader />

      {/* Main Content */}
      <div className="flex flex-1 overflow-hidden">
        {/* Chat Sidebar */}
        <div className="flex w-[420px] min-w-[380px] flex-col border-r border-[var(--color-border)]">
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
      </div>
    </div>
  );
}
