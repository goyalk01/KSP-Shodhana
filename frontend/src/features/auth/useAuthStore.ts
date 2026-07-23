import { create } from 'zustand';

export type OfficerRole = 'ROLE_OFFICER' | 'ROLE_INSPECTOR' | 'ROLE_SUPERINTENDENT';

interface AuthState {
  currentRole: OfficerRole;
  badgeNumber: string;
  officerName: string;
  setRole: (role: OfficerRole) => void;
  hasPermission: (requiredRole: OfficerRole) => boolean;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  currentRole: 'ROLE_SUPERINTENDENT',
  badgeNumber: 'KSP-SP-9912',
  officerName: 'SP Rajesh Gowda',

  setRole: (role: OfficerRole) => {
    let name = 'Constable Officer';
    let badge = 'KSP-OFF-1002';
    if (role === 'ROLE_INSPECTOR') {
      name = 'Inspector Vikram Patil';
      badge = 'KSP-INS-4481';
    } else if (role === 'ROLE_SUPERINTENDENT') {
      name = 'SP Rajesh Gowda';
      badge = 'KSP-SP-9912';
    }
    set({ currentRole: role, officerName: name, badgeNumber: badge });
  },

  hasPermission: (requiredRole: OfficerRole) => {
    const { currentRole } = get();
    if (currentRole === 'ROLE_SUPERINTENDENT') return true;
    if (requiredRole === 'ROLE_INSPECTOR' && currentRole === 'ROLE_INSPECTOR') return true;
    if (requiredRole === 'ROLE_OFFICER') return true;
    return false;
  },
}));
