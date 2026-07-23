/**
 * TypeScript types specific to the Timeline feature.
 */

export interface TimelineConfig {
  maxEvents: number;
  sortOrder: "asc" | "desc";
}

export const DEFAULT_TIMELINE_CONFIG: TimelineConfig = {
  maxEvents: 50,
  sortOrder: "desc",
};

/** Icon mapping for timeline event types */
export const EVENT_TYPE_ICONS: Record<string, string> = {
  "FIR Filed": "",
  "Arrest": "",
  "Witness Statement": "",
  "Evidence Found": "",
  "Hearing": "",
  "Bail Granted": "",
  "Charge Sheet Filed": "",
  "Conviction": "",
  "Default": "",
};
