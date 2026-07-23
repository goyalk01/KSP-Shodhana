'use client';

import React, { useEffect, useState } from 'react';
import { useAuthStore } from '@/features/auth/useAuthStore';

interface AuditLogEntry {
  sequenceNumber: number;
  timestamp: string;
  officerId: string;
  badgeNumber: string;
  action: string;
  resourceId: string;
  clientIp: string;
  previousHash: string;
  currentHash: string;
}

export const AuditLedgerPanel: React.FC = () => {
  const { hasPermission } = useAuthStore();
  const isSuperintendent = hasPermission('ROLE_SUPERINTENDENT');
  const [logs, setLogs] = useState<AuditLogEntry[]>([]);
  const [integrityVerified, setIntegrityVerified] = useState<boolean>(true);
  const [loading, setLoading] = useState<boolean>(false);

  const fetchLedger = async () => {
    setLoading(true);
    try {
      const res = await fetch('http://localhost:8080/api/v1/audit/ledger');
      if (res.ok) {
        const data = await res.json();
        setLogs(data.ledger || []);
        setIntegrityVerified(data.integrityVerified ?? true);
      }
    } catch (e) {
      console.error('Audit ledger fetch error:', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLedger();
  }, []);

  if (!isSuperintendent) {
    return (
      <div className="flex flex-col items-center justify-center p-12 bg-slate-900/60 border border-slate-800 rounded-xl text-center">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-12 h-12 text-rose-500 mb-4 animate-bounce">
          <path fillRule="evenodd" d="M10 1a4.5 4.5 0 0 0-4.5 4.5V9H5a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-6a2 2 0 0 0-2-2h-.5V5.5A4.5 4.5 0 0 0 10 1Zm3 8V5.5a3 3 0 1 0-6 0V9h6Z" clipRule="evenodd" />
        </svg>
        <h3 className="text-lg font-bold text-white mb-2 font-mono">ACCESS RESTRICTED</h3>
        <p className="text-sm text-slate-400 max-w-md">
          Viewing the WORM Immutable Cryptographic Audit Ledger requires <span className="text-amber-400 font-mono font-semibold">ROLE_SUPERINTENDENT</span> privileges. Switch your RBAC role in the top header to unlock.
        </p>
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full bg-slate-950 border border-slate-800 rounded-xl p-4 overflow-hidden">
      <div className="flex items-center justify-between pb-3 border-b border-slate-800">
        <div className="flex items-center space-x-2">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-5 h-5 text-amber-400">
            <path d="M10 1a9 9 0 1 0 0 18 9 9 0 0 0 0-18Zm0 16.5A7.5 7.5 0 1 1 10 2.5a7.5 7.5 0 0 1 0 15Z" />
          </svg>
          <h2 className="text-base font-bold text-white font-mono">WORM SHA-256 IMMUTABLE AUDIT LEDGER</h2>
          {integrityVerified && (
            <span className="flex items-center space-x-1 text-xs bg-emerald-500/10 text-emerald-400 border border-emerald-500/30 px-2 py-0.5 rounded-md font-mono">
              <span>INTEGRITY VERIFIED</span>
            </span>
          )}
        </div>
        <button
          onClick={fetchLedger}
          className="flex items-center space-x-1 text-xs bg-slate-800 hover:bg-slate-700 text-slate-300 px-3 py-1.5 rounded-lg border border-slate-700 transition"
        >
          <span>{loading ? 'Refreshing...' : 'Refresh'}</span>
        </button>
      </div>

      <div className="flex-1 overflow-y-auto mt-4 space-y-2 font-mono text-xs pr-1">
        {logs.length === 0 ? (
          <div className="text-slate-500 text-center py-8">No audit events recorded yet.</div>
        ) : (
          logs.map((entry) => (
            <div key={entry.sequenceNumber} className="bg-slate-900/80 border border-slate-800 rounded-lg p-3 space-y-1.5 hover:border-amber-500/40 transition">
              <div className="flex justify-between items-center text-slate-400 text-[11px]">
                <span className="text-amber-400 font-bold">#{entry.sequenceNumber} [{entry.action}]</span>
                <span>{entry.timestamp}</span>
              </div>
              <div className="flex space-x-4 text-slate-300">
                <span>Badge: <strong className="text-white">{entry.badgeNumber}</strong></span>
                <span>Target FIR: <strong className="text-white">{entry.resourceId}</strong></span>
                <span>IP: <strong className="text-white">{entry.clientIp}</strong></span>
              </div>
              <div className="pt-1 border-t border-slate-800/80 text-[10px] text-slate-500 truncate">
                <span>Hash: <strong className="text-amber-400/90 font-mono">{entry.currentHash}</strong></span>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
