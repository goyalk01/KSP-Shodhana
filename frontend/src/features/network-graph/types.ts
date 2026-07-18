/**
 * TypeScript types specific to the Network Graph feature.
 */

export interface GraphConfig {
  nodeSize: number;
  linkWidth: number;
  chargeStrength: number;
  centerStrength: number;
}

export const DEFAULT_GRAPH_CONFIG: GraphConfig = {
  nodeSize: 8,
  linkWidth: 2,
  chargeStrength: -200,
  centerStrength: 0.05,
};
