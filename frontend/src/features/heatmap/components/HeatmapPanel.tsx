"use client";

import { MapContainer, TileLayer, CircleMarker, Popup, useMap } from "react-leaflet";
import type { HeatmapData } from "@/types/domain";
import { MAP_CENTER, MAP_DEFAULT_ZOOM, SEVERITY_COLORS } from "@/lib/constants";
import { useEffect, useState } from "react";

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
  const [spatialData, setSpatialData] = useState<HeatmapData | null>(data);
  const [isSpatialQuery, setIsSpatialQuery] = useState<boolean>(false);

  useEffect(() => {
    setSpatialData(data);
  }, [data]);

  const handlePostGisSpatialQuery = async () => {
    try {
      setIsSpatialQuery(true);
      const res = await fetch('/api/proxy/api/v1/crimes/spatial/radius?lat=12.9716&lng=77.5946&radiusKm=15');
      if (res.ok) {
        const body = await res.json();
        const crimes = body.data || [];
        const points = crimes.map((c: any) => ({
          lat: c.latitude || 12.9716,
          lng: c.longitude || 77.5946,
          intensity: c.severity === 'CRITICAL' ? 0.9 : c.severity === 'HIGH' ? 0.7 : 0.4
        }));
        setSpatialData({
          points,
          center: MAP_CENTER,
          zoom: 11
        });
      }
    } catch (e) {
      console.error('Spatial radius query error:', e);
    } finally {
      setIsSpatialQuery(false);
    }
  };

  const activeData = spatialData || data;

  if (!activeData || activeData.points.length === 0) {
    return (
      <div className="flex h-full flex-col rounded-2xl border border-[var(--color-border)]/50 bg-white shadow-sm overflow-hidden">
        <div className="panel-header flex justify-between items-center px-4 py-2 bg-[var(--color-surface)] border-b border-[var(--color-border)]/50">
          <span className="font-serif font-bold text-[var(--color-text)] text-sm">Crime Hotspots Map</span>
          <button
            onClick={handlePostGisSpatialQuery}
            className="text-xs font-bold bg-[var(--color-primary)] hover:bg-[var(--color-primary-hover)] text-white px-3 py-1.5 rounded-lg transition shadow-xs cursor-pointer whitespace-nowrap shrink-0"
          >
            Filter 15km Radius
          </button>
        </div>
        <div className="flex flex-1 items-center justify-center text-sm text-[var(--color-text-dim)]">
          No location data available
        </div>
      </div>
    );
  }

  const center = activeData.center || MAP_CENTER;
  const zoom = activeData.zoom || MAP_DEFAULT_ZOOM;

  return (
    <div className="flex h-full flex-col rounded-2xl border border-[var(--color-border)]/50 bg-white shadow-sm overflow-hidden">
      <div className="panel-header flex justify-between items-center px-4 py-2.5 bg-[var(--color-surface)] border-b border-[var(--color-border)]/50">
        <div className="flex items-center space-x-2 min-w-0">
          <span className="font-serif font-bold text-[var(--color-text)] text-sm whitespace-nowrap shrink-0">Crime Hotspots Map</span>
          <span className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--color-primary)] bg-[var(--color-primary)]/10 border border-[var(--color-primary)]/20 px-2 py-0.5 rounded-md whitespace-nowrap shrink-0">
            15km Radius Zone
          </span>
        </div>
        <div className="flex items-center space-x-2 shrink-0">
          <button
            onClick={handlePostGisSpatialQuery}
            className="text-xs font-bold bg-[var(--color-primary)] hover:bg-[var(--color-primary-hover)] text-white px-3.5 py-1.5 rounded-lg transition shadow-sm cursor-pointer whitespace-nowrap shrink-0"
          >
            {isSpatialQuery ? 'Filtering Radius...' : 'Filter 15km Radius'}
          </button>
          <span className="text-[10px] font-extrabold uppercase tracking-wider text-[var(--color-primary)] bg-[var(--color-primary)]/10 px-2.5 py-1 rounded-md shrink-0 whitespace-nowrap border border-[var(--color-primary)]/20">
            {activeData.points.length} Incidents
          </span>
        </div>
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
            url="https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png"
            attribution='&copy; <a href="https://carto.com/">CARTO</a>'
          />
          <MapUpdater center={center} zoom={zoom} />

          {activeData.points.map((point, idx) => {
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
                  <div className="text-xs font-serif">
                    <p className="font-bold text-[var(--color-primary)]">Crime Incident Location</p>
                    <p>Incident Density: {Math.round(point.intensity * 100)}%</p>
                    <p>Coordinates: {point.lat.toFixed(4)}, {point.lng.toFixed(4)}</p>
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
