import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "KSP Shodhana | AI Crime Intelligence Workspace",
  description:
    "AI-Powered Crime Intelligence & Investigation Workspace for Karnataka State Police. Ask. Analyze. Act.",
  keywords: ["KSP", "Crime Intelligence", "AI", "Karnataka Police", "Investigation"],
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className="dark">
      <body className="antialiased">{children}</body>
    </html>
  );
}
