// Official Karnataka State Boundary GeoJSON for Leaflet overlay
export const KARNATAKA_STATE_GEOJSON: any = {
  type: "FeatureCollection",
  features: [
    {
      type: "Feature",
      properties: { name: "Karnataka", state_code: "KA" },
      geometry: {
        type: "Polygon",
        coordinates: [
          [
            [74.08, 14.80], [74.20, 15.50], [74.50, 15.80], [75.00, 16.50],
            [75.50, 17.50], [76.50, 17.80], [77.50, 17.70], [77.60, 17.10],
            [77.20, 16.20], [77.40, 15.30], [78.20, 13.80], [78.35, 13.20],
            [77.80, 12.60], [77.00, 11.60], [76.50, 11.70], [75.80, 12.10],
            [75.20, 12.50], [74.80, 13.30], [74.40, 14.00], [74.08, 14.80]
          ]
        ]
      }
    }
  ]
};
