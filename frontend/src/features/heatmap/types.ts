/**
 * TypeScript types specific to the Heatmap feature.
 */

export interface HeatmapConfig {
  radius: number;
  blur: number;
  maxZoom: number;
  gradient?: Record<number, string>;
}

export const DEFAULT_HEATMAP_CONFIG: HeatmapConfig = {
  radius: 25,
  blur: 15,
  maxZoom: 17,
};
