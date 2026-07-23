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
      <div className="flex flex-col items-center justify-center p-12 bg-white border border-[var(--color-border)]/50 rounded-2xl shadow-sm text-center">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-12 h-12 text-rose-500 mb-4 animate-bounce">
          <path fillRule="evenodd" d="M10 1a4.5 4.5 0 0 0-4.5 4.5V9H5a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-6a2 2 0 0 0-2-2h-.5V5.5A4.5 4.5 0 0 0 10 1Zm3 8V5.5a3 3 0 1 0-6 0V9h6Z" clipRule="evenodd" />
        </svg>
        <h3 className="text-lg font-serif font-extrabold text-[var(--color-text)] mb-2">ACCESS RESTRICTED</h3>
        <p className="text-xs text-[var(--color-text-muted)] max-w-md">
          Viewing the WORM Immutable Cryptographic Audit Ledger requires <span className="text-[var(--color-primary)] font-bold">ROLE_SUPERINTENDENT</span> privileges. Switch your RBAC role in the top header to unlock.
        </p>
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full bg-white border border-[var(--color-border)]/50 rounded-2xl p-5 shadow-sm overflow-hidden">
      <div className="flex items-center justify-between pb-3 border-b border-[var(--color-border)]/50">
        <div className="flex items-center space-x-2">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-5 h-5 text-[var(--color-primary)]">
            <path d="M10 1a9 9 0 1 0 0 18 9 9 0 0 0 0-18Zm0 16.5A7.5 7.5 0 1 1 10 2.5a7.5 7.5 0 0 1 0 15Z" />
          </svg>
          <h2 className="text-base font-serif font-extrabold text-[var(--color-text)]">WORM SHA-256 IMMUTABLE AUDIT LEDGER</h2>
          {integrityVerified && (
            <span className="flex items-center space-x-1 text-[10px] bg-emerald-500/10 text-emerald-700 border border-emerald-500/30 px-2.5 py-0.5 rounded-md font-bold">
              <span>INTEGRITY VERIFIED</span>
            </span>
          )}
        </div>
        <button
          onClick={fetchLedger}
          className="flex items-center space-x-1 text-xs font-bold bg-[var(--color-primary)] hover:bg-[var(--color-primary-hover)] text-white px-3 py-1.5 rounded-lg transition shadow-xs cursor-pointer"
        >
          <span>{loading ? 'Refreshing...' : 'Refresh Ledger'}</span>
        </button>
      </div>

      <div className="flex-1 overflow-y-auto mt-4 space-y-2.5 text-xs pr-1">
        {logs.length === 0 ? (
          <div className="text-[var(--color-text-dim)] text-center py-8">No audit events recorded yet.</div>
        ) : (
          logs.map((entry) => (
            <div key={entry.sequenceNumber} className="bg-slate-50 border border-slate-200/80 rounded-xl p-3.5 space-y-1.5 hover:border-[var(--color-primary)]/40 transition">
              <div className="flex justify-between items-center text-[11px]">
                <span className="text-[var(--color-primary)] font-bold">#{entry.sequenceNumber} [{entry.action}]</span>
                <span className="text-[var(--color-text-muted)] font-mono">{entry.timestamp}</span>
              </div>
              <div className="flex space-x-4 text-slate-700">
                <span>Badge: <strong className="text-[var(--color-text)] font-semibold">{entry.badgeNumber}</strong></span>
                <span>Target FIR: <strong className="text-[var(--color-text)] font-semibold">{entry.resourceId}</strong></span>
                <span>IP: <strong className="text-[var(--color-text)] font-semibold">{entry.clientIp}</strong></span>
              </div>
              <div className="pt-1.5 border-t border-slate-200 text-[10px] text-slate-500 truncate">
                <span>Hash: <strong className="text-[var(--color-primary)] font-mono font-semibold">{entry.currentHash}</strong></span>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
