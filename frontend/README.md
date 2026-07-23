# KSP Shodhana — Next.js 14 Presentation Layer Workspace

The Next.js 14 presentation layer provides an interactive investigation workspace for law enforcement officers operating **KSP-Shodhana**.

---

## Technical Specifications

* **Framework**: Next.js 14 (App Router)
* **Language**: TypeScript (`5.0`)
* **Port**: `3000`
* **State Management**: Zustand (`5.0`) global workspace store
* **Styling**: Tailwind CSS v4 organic design palette
* **Maps & Graphs**: Leaflet (`1.9.4`) spatial heatmaps & React Force Graph 2D (`1.29`) physics suspect graph
* **Forensics Security**: `WatermarkOverlay.tsx` dynamic steganographic watermark overlay
* **Multi-Language Audio**: `translator.ts` script translation engine for Devanagari Hindi (`hi-IN`) and Kannada (`kn-IN`) Web Speech TTS

---

## Key Components

* `features/chat/`: AI Copilot input, message bubbles, real-time SSE streaming handler, and speak aloud buttons.
* `features/heatmap/`: Interactive Leaflet crime density heatmap panel with district auto-zoom.
* `features/network/`: 2D physics-directed suspect co-accused force graph panel.
* `features/timeline/`: Chronological investigation event log panel.
* `features/evidence/`: Explainable evidence cards with official FIR citations.
* `components/security/WatermarkOverlay.tsx`: Forensic overlay rendering Officer Badge #, Timestamp, and Client IP.

---

## Quickstart & Launch

```bash
cd frontend

# Install Node dependencies
npm install

# Start Next.js development server
npm run dev
```
Application accessible at `http://localhost:3000`.
