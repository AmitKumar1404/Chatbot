import { useCallback, useEffect, useMemo, useState } from "react";

export const SIDEBAR_MOBILE_BREAKPOINT = 768;

function getIsMobile() {
  if (typeof window === "undefined") return false;
  return window.matchMedia(
    `(max-width: ${SIDEBAR_MOBILE_BREAKPOINT}px)`
  ).matches;
}

/**
 * Responsive sidebar state: desktop expanded/collapsed rail, mobile drawer.
 * Breakpoint matches App.css (768px).
 */
export function useSidebarState() {
  const [isMobile, setIsMobile] = useState(getIsMobile);
  const [desktopCollapsed, setDesktopCollapsed] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);

  useEffect(() => {
    const mq = window.matchMedia(
      `(max-width: ${SIDEBAR_MOBILE_BREAKPOINT}px)`
    );
    const onChange = (e) => {
      setIsMobile(e.matches);
      if (e.matches) {
        setMobileOpen(false);
      }
    };
    mq.addEventListener("change", onChange);
    return () => mq.removeEventListener("change", onChange);
  }, []);

  const isCollapsed = !isMobile && desktopCollapsed;
  const isDrawerOpen = isMobile && mobileOpen;

  const toggleSidebar = useCallback(() => {
    if (isMobile) {
      setMobileOpen((prev) => !prev);
    } else {
      setDesktopCollapsed((prev) => !prev);
    }
  }, [isMobile]);

  const openSidebar = useCallback(() => {
    if (isMobile) setMobileOpen(true);
  }, [isMobile]);

  const closeSidebar = useCallback(() => {
    if (isMobile) setMobileOpen(false);
  }, [isMobile]);

  const sidebarClassName = useMemo(() => {
    const parts = ["sidebar"];
    if (isCollapsed) parts.push("sidebar--collapsed");
    if (isDrawerOpen) parts.push("sidebar--mobile-open");
    return parts.join(" ");
  }, [isCollapsed, isDrawerOpen]);

  return {
    isMobile,
    isCollapsed,
    isDrawerOpen,
    mobileOpen,
    desktopCollapsed,
    toggleSidebar,
    openSidebar,
    closeSidebar,
    sidebarClassName,
  };
}
