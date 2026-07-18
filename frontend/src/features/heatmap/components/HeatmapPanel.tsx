"use client";

import { MapContainer, TileLayer, CircleMarker, Popup, useMap } from "react-leaflet";
import type { HeatmapData } from "@/types/domain";
import { MAP_CENTER, MAP_DEFAULT_ZOOM, SEVERITY_COLORS } from "@/lib/constants";
import { useEffect } from "react";

interface HeatmapPanelProps {
  data: HeatmapData | null;
}

function MapUpdater({ center, zoom }: { center: { lat: number; lng: number }; zoom: number }) {
  const map = useMap();
  useEffect(() => {
    map.setView([center.lat, center.lng], zoom, { animate: true });
  }, [map, center, zoom]);
  return null;
}

export default function HeatmapPanel({ data }: HeatmapPanelProps) {
  if (!data || data.points.length === 0) {
    return (
      <div className="panel flex h-full flex-col">
        <div className="panel-header">
          <span>🗺️ Crime Heatmap</span>
        </div>
        <div className="flex flex-1 items-center justify-center text-sm text-[var(--color-text-dim)]">
          No location data available
        </div>
      </div>
    );
  }

  const center = data.center || MAP_CENTER;
  const zoom = data.zoom || MAP_DEFAULT_ZOOM;

  return (
    <div className="panel flex h-full flex-col">
      <div className="panel-header">
        <span>🗺️ Crime Heatmap</span>
        <span className="text-[10px] font-normal normal-case tracking-normal text-[var(--color-text-dim)]">
          {data.points.length} incidents
        </span>
      </div>

      <div className="flex-1 relative">
        <MapContainer
          center={[center.lat, center.lng]}
          zoom={zoom}
          className="h-full w-full"
          zoomControl={true}
          attributionControl={false}
        >
          <TileLayer
            url="https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"
            attribution='&copy; <a href="https://carto.com/">CARTO</a>'
          />
          <MapUpdater center={center} zoom={zoom} />

          {data.points.map((point, idx) => {
            // Color based on intensity
            const color =
              point.intensity > 0.7
                ? SEVERITY_COLORS.Critical
                : point.intensity > 0.4
                  ? SEVERITY_COLORS.High
                  : SEVERITY_COLORS.Medium;

            return (
              <CircleMarker
                key={idx}
                center={[point.lat, point.lng]}
                radius={8 + point.intensity * 20}
                pathOptions={{
                  color: color,
                  fillColor: color,
                  fillOpacity: 0.3 + point.intensity * 0.4,
                  weight: 1,
                }}
              >
                <Popup>
                  <div className="text-xs">
                    <p className="font-semibold">Crime Incident</p>
                    <p>Intensity: {Math.round(point.intensity * 100)}%</p>
                    <p>Location: {point.lat.toFixed(4)}, {point.lng.toFixed(4)}</p>
                  </div>
                </Popup>
              </CircleMarker>
            );
          })}
        </MapContainer>
      </div>
    </div>
  );
}
