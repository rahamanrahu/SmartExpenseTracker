/* ==========================================
   SMART EXPENSE TRACKER — APP JS
   ========================================== */

/* ---------- ICON LIBRARY ---------- */
const Icons = {
  logo: `<svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M4 6C4 4.89543 4.89543 4 6 4H14L20 10V18C20 19.1046 19.1046 20 18 20H6C4.89543 20 4 19.1046 4 18V6Z" fill="url(#lg1)"/><path d="M14 4L20 10H15C14.4477 10 14 9.55228 14 9V4Z" fill="url(#lg2)" opacity="0.8"/><path d="M8 12H16M8 15H13" stroke="white" stroke-width="1.6" stroke-linecap="round"/><defs><linearGradient id="lg1" x1="4" y1="4" x2="20" y2="20" gradientUnits="userSpaceOnUse"><stop stop-color="#a78bfa"/><stop offset="1" stop-color="#5b7cff"/></linearGradient><linearGradient id="lg2" x1="14" y1="4" x2="20" y2="10" gradientUnits="userSpaceOnUse"><stop stop-color="#c4b5fd"/><stop offset="1" stop-color="#818cf8"/></linearGradient></defs></svg>`,
  dashboard: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="9" rx="1.5"/><rect x="14" y="3" width="7" height="5" rx="1.5"/><rect x="14" y="12" width="7" height="9" rx="1.5"/><rect x="3" y="16" width="7" height="5" rx="1.5"/></svg>`,
  expenses: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M3 7h18M6 7V5a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v2M4 7l1 12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2l1-12"/><path d="M10 12v5M14 12v5"/></svg>`,
  budgets: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 3v9l6 3"/></svg>`,
  reports: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M3 3v18h18"/><path d="M7 14l4-4 4 4 5-6"/></svg>`,
  goals: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><circle cx="12" cy="12" r="5"/><circle cx="12" cy="12" r="1.5" fill="currentColor"/></svg>`,
  calendar: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M8 3v4M16 3v4"/></svg>`,
  wallets: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M21 7H5a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2h16V7Z"/><path d="M16 13h2M5 7V5a2 2 0 0 1 2-2h11"/></svg>`,
  settings: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.7 1.7 0 0 0 .3 1.8l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.7 1.7 0 0 0-1.8-.3 1.7 1.7 0 0 0-1 1.5V21a2 2 0 1 1-4 0v-.1a1.7 1.7 0 0 0-1.1-1.5 1.7 1.7 0 0 0-1.8.3l-.1.1a2 2 0 1 1-2.8-2.8l.1-.1a1.7 1.7 0 0 0 .3-1.8 1.7 1.7 0 0 0-1.5-1H3a2 2 0 1 1 0-4h.1a1.7 1.7 0 0 0 1.5-1.1 1.7 1.7 0 0 0-.3-1.8l-.1-.1a2 2 0 1 1 2.8-2.8l.1.1a1.7 1.7 0 0 0 1.8.3H9a1.7 1.7 0 0 0 1-1.5V3a2 2 0 1 1 4 0v.1a1.7 1.7 0 0 0 1 1.5 1.7 1.7 0 0 0 1.8-.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1a1.7 1.7 0 0 0-.3 1.8V9a1.7 1.7 0 0 0 1.5 1H21a2 2 0 1 1 0 4h-.1a1.7 1.7 0 0 0-1.5 1z"/></svg>`,
  bell: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/><path d="M10 21a2 2 0 0 0 4 0"/></svg>`,
  search: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="7"/><path d="m21 21-4.3-4.3"/></svg>`,
  plus: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M12 5v14M5 12h14"/></svg>`,
  menu: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M3 12h18M3 6h18M3 18h18"/></svg>`,
  close: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12"/></svg>`,
  chevronDown: `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m6 9 6 6 6-6"/></svg>`,
  chevronRight: `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m9 18 6-6-6-6"/></svg>`,
  chevronLeft: `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m15 18-6-6 6-6"/></svg>`,
  trend: `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m3 17 6-6 4 4 8-8"/><path d="M17 7h4v4"/></svg>`,
  trendDown: `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m3 7 6 6 4-4 8 8"/><path d="M17 17h4v-4"/></svg>`,
  food: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M3 11h18M5 11v8a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-8M8 6c0-1.1 1.8-2 4-2s4 .9 4 2"/></svg>`,
  transport: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M5 17h14M7 17v-5l2-5h6l2 5v5M6 12h12"/><circle cx="8" cy="17" r="2"/><circle cx="16" cy="17" r="2"/></svg>`,
  shopping: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M6 2 3 6v13a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4H6z"/><path d="M3 6h18M16 10a4 4 0 0 1-8 0"/></svg>`,
  bills: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6z"/><path d="M14 2v6h6M8 13h8M8 17h5"/></svg>`,
  entertainment: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="6" width="20" height="12" rx="2"/><path d="m10 9 5 3-5 3V9z" fill="currentColor"/></svg>`,
  health: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M20 14a8 8 0 1 0-16 0"/><path d="M12 10v8m-4-4h8"/></svg>`,
  education: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10 12 5 2 10l10 5 10-5z"/><path d="M6 12v5c0 1.5 3 3 6 3s6-1.5 6-3v-5"/></svg>`,
  other: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 8v4M12 16h.01"/></svg>`,
  edit: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.12 2.12 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>`,
  trash: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M3 6h18M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2m3 0v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6h14z"/><path d="M10 11v6M14 11v6"/></svg>`,
  download: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M7 10l5 5 5-5M12 15V3"/></svg>`,
  filter: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M22 3H2l8 9.46V19l4 2v-8.54L22 3z"/></svg>`,
  check: `<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg>`,
  sparkles: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="m12 3-1.9 5.8a2 2 0 0 1-1.3 1.3L3 12l5.8 1.9a2 2 0 0 1 1.3 1.3L12 21l1.9-5.8a2 2 0 0 1 1.3-1.3L21 12l-5.8-1.9a2 2 0 0 1-1.3-1.3L12 3Z"/><path d="M19 3v4M21 5h-4"/></svg>`,
  google: `<svg width="18" height="18" viewBox="0 0 24 24"><path fill="#EA4335" d="M12 10.8v3.9h5.4c-.2 1.4-1.6 4.1-5.4 4.1-3.3 0-5.9-2.7-5.9-6s2.6-6 5.9-6c1.8 0 3.1.8 3.8 1.5l2.6-2.5C16.8 4.2 14.6 3.3 12 3.3c-4.8 0-8.7 3.9-8.7 8.7s3.9 8.7 8.7 8.7c5 0 8.3-3.5 8.3-8.5 0-.6-.1-1-.1-1.4H12z"/></svg>`,
  github: `<svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M12 .5C5.7.5.5 5.7.5 12c0 5 3.3 9.3 7.8 10.8.6.1.8-.2.8-.6v-2c-3.2.7-3.9-1.5-3.9-1.5-.5-1.3-1.3-1.7-1.3-1.7-1-.7.1-.7.1-.7 1.2.1 1.8 1.2 1.8 1.2 1 1.8 2.8 1.3 3.5 1 .1-.8.4-1.3.8-1.6-2.6-.3-5.3-1.3-5.3-5.8 0-1.3.5-2.3 1.2-3.1-.1-.3-.5-1.5.1-3.1 0 0 1-.3 3.2 1.2.9-.3 1.9-.4 2.9-.4s2 .1 2.9.4c2.2-1.5 3.2-1.2 3.2-1.2.6 1.6.2 2.8.1 3.1.8.8 1.2 1.9 1.2 3.1 0 4.5-2.7 5.5-5.3 5.8.4.4.8 1.1.8 2.2v3.3c0 .3.2.7.8.6 4.5-1.5 7.8-5.8 7.8-10.8C23.5 5.7 18.3.5 12 .5z"/></svg>`,
  eye: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7z"/><circle cx="12" cy="12" r="3"/></svg>`,
  eyeOff: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M17.9 17.9A10.1 10.1 0 0 1 12 20c-7 0-10-8-10-8a18.5 18.5 0 0 1 5.2-6m3.3-1.6A9.5 9.5 0 0 1 12 4c7 0 10 8 10 8a18.7 18.7 0 0 1-2.2 3.2M9.9 9.9a3 3 0 1 0 4.2 4.2M1 1l22 22"/></svg>`,
  mic: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="9" y="2" width="6" height="12" rx="3"/><path d="M19 10a7 7 0 0 1-14 0M12 19v3"/></svg>`,
  camera: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/></svg>`,
  arrowUp: `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 19V5M5 12l7-7 7 7"/></svg>`,
  arrowDown: `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 5v14M5 12l7 7 7-7"/></svg>`,
  info: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 16v-4M12 8h.01"/></svg>`,
  more: `<svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><circle cx="12" cy="12" r="1.5"/><circle cx="5" cy="12" r="1.5"/><circle cx="19" cy="12" r="1.5"/></svg>`,
  tag: `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M20.6 13.4 13.4 20.6a2 2 0 0 1-2.8 0L2 12V2h10l8.6 8.6a2 2 0 0 1 0 2.8z"/><circle cx="7" cy="7" r="1" fill="currentColor"/></svg>`,
  lock: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="10" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>`,
  user: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>`,
  logout: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9"/></svg>`,
  help: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M9.1 9a3 3 0 0 1 5.8 1c0 2-3 3-3 3M12 17h.01"/></svg>`,
  sun: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M4.93 19.07l1.41-1.41M17.66 6.34l1.41-1.41"/></svg>`,
  moon: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12.8A9 9 0 1 1 11.2 3a7 7 0 0 0 9.8 9.8z"/></svg>`,
  repeat: `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="m17 2 4 4-4 4"/><path d="M3 11v-1a4 4 0 0 1 4-4h14M7 22l-4-4 4-4"/><path d="M21 13v1a4 4 0 0 1-4 4H3"/></svg>`,
  arrowRight: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14M12 5l7 7-7 7"/></svg>`,
  shield: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>`,
  flag: `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M4 15s1-1 4-1 5 2 8 2 4-1 4-1V3s-1 1-4 1-5-2-8-2-4 1-4 1z"/><path d="M4 22v-7"/></svg>`,
  wallet: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12V7a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-5"/><path d="M16 12h5v-2h-5a1 1 0 0 0 0 2z" fill="currentColor"/></svg>`,
};

/* ---------- MODAL SYSTEM ---------- */
function openModal(id) {
  const m = document.getElementById(id);
  if (m) {
    m.classList.add('active');
    m.setAttribute('aria-hidden', 'false');
    // Save previous focus, trap inside modal
    m._prevFocus = document.activeElement;
    setTimeout(() => {
      const focusable = m.querySelectorAll('input:not([type=hidden]), select, textarea, button:not([disabled])');
      if (focusable.length) focusable[0].focus();
    }, 50);
    // Tab trap
    if (!m._trapBound) {
      m._trapBound = true;
      m.addEventListener('keydown', (e) => {
        if (e.key !== 'Tab') return;
        const focusable = Array.from(m.querySelectorAll('input:not([type=hidden]), select, textarea, button:not([disabled]), a[href]'));
        if (!focusable.length) return;
        const first = focusable[0], last = focusable[focusable.length - 1];
        if (e.shiftKey) { if (document.activeElement === first) { e.preventDefault(); last.focus(); } }
        else { if (document.activeElement === last) { e.preventDefault(); first.focus(); } }
      });
    }
  }
}

function closeModal(id) {
  const m = document.getElementById(id);
  if (m) {
    m.classList.remove('active');
    m.setAttribute('aria-hidden', 'true');
    // Restore focus
    if (m._prevFocus && m._prevFocus.focus) {
      try { m._prevFocus.focus(); } catch(_) {}
    }
  }
}

function closeAllModals() {
  document.querySelectorAll('.modal-backdrop.active').forEach(m => m.classList.remove('active'));
}

/* Wire all modal open/close triggers — call after DOM is ready or after new content is injected */
function bindAllModals() {
  // [data-modal-open="someId"] → open that modal
  document.querySelectorAll('[data-modal-open]').forEach(btn => {
    if (btn.dataset.modalBound) return;
    btn.dataset.modalBound = '1';
    btn.addEventListener('click', (e) => {
      e.preventDefault();
      e.stopPropagation();
      const id = btn.dataset.modalOpen;
      closeAllModals(); // close any open modal first
      openModal(id);
    });
  });

  // [data-modal-close] → close the nearest .modal-backdrop ancestor
  document.querySelectorAll('[data-modal-close]').forEach(btn => {
    if (btn.dataset.modalCloseBound) return;
    btn.dataset.modalCloseBound = '1';
    btn.addEventListener('click', () => {
      const backdrop = btn.closest('.modal-backdrop');
      if (backdrop) backdrop.classList.remove('active');
      else closeAllModals();
    });
  });

  // Legacy [data-close] support
  document.querySelectorAll('[data-close]').forEach(btn => {
    if (btn.dataset.closeBound) return;
    btn.dataset.closeBound = '1';
    btn.addEventListener('click', () => {
      const backdrop = btn.closest('.modal-backdrop');
      if (backdrop) backdrop.classList.remove('active');
      else closeAllModals();
    });
  });

  // Click on backdrop itself → close
  document.querySelectorAll('.modal-backdrop').forEach(bd => {
    if (bd.dataset.backdropBound) return;
    bd.dataset.backdropBound = '1';
    bd.addEventListener('click', (e) => {
      if (e.target === bd) bd.classList.remove('active');
    });
  });

  // ESC key → close
  if (!window._escBound) {
    window._escBound = true;
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') closeAllModals();
    });
  }
}

/* ---------- DROPDOWN SYSTEM ---------- */
function bindAllDropdowns() {
  // Support both [data-dropdown] wrapper and plain .dropdown
  document.querySelectorAll('.dropdown, [data-dropdown]').forEach(dd => {
    if (dd.dataset.dropdownBound) return;
    dd.dataset.dropdownBound = '1';

    // The trigger can be [data-dropdown-toggle], .avatar, or the first button
    const trigger = dd.querySelector('[data-dropdown-toggle], .avatar') || dd.querySelector('button');
    if (!trigger) return;

    trigger.addEventListener('click', (e) => {
      e.stopPropagation();
      const isOpen = dd.classList.contains('open');
      // Close all dropdowns
      document.querySelectorAll('.dropdown, [data-dropdown]').forEach(x => x.classList.remove('open'));
      if (!isOpen) dd.classList.add('open');
    });
  });

  if (!window._dropdownDocBound) {
    window._dropdownDocBound = true;
    document.addEventListener('click', () => {
      document.querySelectorAll('.dropdown, [data-dropdown]').forEach(dd => dd.classList.remove('open'));
    });
  }
}

/* ---------- SIDEBAR SYSTEM ---------- */
function initSidebar() {
  const sidebar = document.getElementById('sidebar');
  const appMain = document.getElementById('appMain') || document.querySelector('.app-main');
  const overlay = document.getElementById('sidebarOverlay');

  if (!sidebar) return;

  // Mobile menu button (either ID works)
  const mobileBtn = document.getElementById('menuBtn') || document.getElementById('mobileMenuBtn');
  if (mobileBtn) {
    mobileBtn.addEventListener('click', () => {
      sidebar.classList.add('open');
      if (overlay) { overlay.classList.add('active'); overlay.style.display = 'block'; }
    });
  }

  // Overlay click → close sidebar
  if (overlay) {
    overlay.addEventListener('click', () => {
      sidebar.classList.remove('open');
      overlay.classList.remove('active');
    });
  }

  // Collapse button (desktop)
  const collapseBtn = document.getElementById('collapseBtn');
  if (collapseBtn && appMain) {
    collapseBtn.addEventListener('click', () => {
      sidebar.classList.toggle('sidebar-collapsed');
      appMain.classList.toggle('collapsed');
      localStorage.setItem('sidebarCollapsed', sidebar.classList.contains('sidebar-collapsed'));
    });
  }

  // Restore collapse state
  if (localStorage.getItem('sidebarCollapsed') === 'true' && appMain) {
    sidebar.classList.add('sidebar-collapsed');
    appMain.classList.add('collapsed');
  }
}

/* ---------- THEME ---------- */
function initTheme() {
  // Prefer server-rendered theme (already set by layout.html inline script)
  // Then fall back to localStorage
  const serverTheme = document.documentElement.getAttribute('data-theme') || 'dark';
  const saved = localStorage.getItem('theme') || serverTheme;
  document.documentElement.setAttribute('data-theme', saved);
  localStorage.setItem('theme', saved);

  const themeBtn = document.getElementById('themeBtn');
  if (themeBtn) {
    themeBtn.innerHTML = saved === 'light' ? Icons.sun : Icons.moon;

    themeBtn.addEventListener('click', () => {
      const cur = document.documentElement.getAttribute('data-theme');
      const next = cur === 'dark' ? 'light' : 'dark';
      document.documentElement.setAttribute('data-theme', next);
      localStorage.setItem('theme', next);
      themeBtn.innerHTML = next === 'light' ? Icons.sun : Icons.moon;
      toast('info', `Switched to ${next} mode`);
      if (typeof Chart !== 'undefined') {
        setChartDefaults();
        Object.values(Chart.instances).forEach(c => c.update());
      }
      // Persist theme to DB (fire-and-forget)
      const csrf = document.querySelector('meta[name="_csrf"]')?.content;
      const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
      if (csrf && csrfHeader) {
        const fd = new FormData();
        fd.append('theme', next);
        fd.append('_csrf', csrf);
        fetch('/settings/preferences', { method: 'POST', body: fd, headers: { [csrfHeader]: csrf } })
          .catch(() => {}); // fire-and-forget
      }
    });
  }
}

/* ---------- PRIVACY ---------- */
function initPrivacy() {
  const privacyBtn = document.getElementById('privacyBtn');
  if (!privacyBtn) return;

  if (localStorage.getItem('privacy') === 'on') document.body.classList.add('privacy-on');
  privacyBtn.innerHTML = document.body.classList.contains('privacy-on') ? Icons.eyeOff : Icons.eye;

  privacyBtn.addEventListener('click', () => {
    document.body.classList.toggle('privacy-on');
    const on = document.body.classList.contains('privacy-on');
    localStorage.setItem('privacy', on ? 'on' : 'off');
    privacyBtn.innerHTML = on ? Icons.eyeOff : Icons.eye;
    toast('info', on ? 'Amounts hidden' : 'Amounts visible');
  });
}

/* ---------- ADD EXPENSE BUTTON ---------- */
function initAddExpenseBtn() {
  // Top-bar "Add expense" button — opens expense modal
  const addBtn = document.getElementById('addExpenseBtn');
  if (addBtn && !addBtn.dataset.addBound) {
    addBtn.dataset.addBound = '1';
    addBtn.addEventListener('click', () => {
      const modal = document.getElementById('expenseModal');
      if (modal) openModal('expenseModal');
      else openExpenseModal();
    });
  }

  // FAB
  const fab = document.getElementById('fab');
  if (fab && !fab.dataset.fabBound) {
    fab.dataset.fabBound = '1';
    fab.addEventListener('click', () => {
      const modal = document.getElementById('expenseModal');
      if (modal) openModal('expenseModal');
      else openExpenseModal();
    });
  }
}

/* ---------- SEGMENTED CONTROLS ---------- */
function initSegmentedControls() {
  // Generic segmented controls — each button toggles 'active' within its parent
  document.querySelectorAll('.segmented').forEach(seg => {
    if (seg.dataset.segBound) return;
    seg.dataset.segBound = '1';
    seg.querySelectorAll('button').forEach(btn => {
      btn.addEventListener('click', () => {
        seg.querySelectorAll('button').forEach(x => x.classList.remove('active'));
        btn.classList.add('active');
      });
    });
  });
}

/* ---------- ⌘K SEARCH SHORTCUT + TABLE FILTER ---------- */
function initSearch() {
  document.addEventListener('keydown', e => {
    if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
      e.preventDefault();
      // Focus global search in topbar, or the page-level search input
      const input = document.getElementById('globalSearch') || document.getElementById('searchInput') || document.querySelector('.search input');
      if (input) input.focus();
    }
  });

  // Wire global search → navigate to expenses with query
  const globalSearch = document.getElementById('globalSearch');
  if (globalSearch) {
    globalSearch.addEventListener('keydown', e => {
      if (e.key === 'Enter' && globalSearch.value.trim()) {
        e.preventDefault();
        window.location.href = '/expenses?q=' + encodeURIComponent(globalSearch.value.trim());
      }
    });
  }

  // Wire expenses-page search to filter table rows in real time (debounced)
  const searchInput = document.getElementById('searchInput');
  const expenseTable = document.getElementById('expensesTable');
  if (searchInput && expenseTable) {
    let debounceTimer;
    searchInput.addEventListener('input', () => {
      clearTimeout(debounceTimer);
      debounceTimer = setTimeout(() => {
        const q = searchInput.value.toLowerCase().trim();
        expenseTable.querySelectorAll('tbody tr[data-id]').forEach(row => {
          const text = row.textContent.toLowerCase();
          row.style.display = (!q || text.includes(q)) ? '' : 'none';
        });
      }, 200);
    });
  }
}

function initExpenseEditFlow() {
  const params = new URLSearchParams(window.location.search);
  const editId = params.get('edit');
  const form = document.getElementById('expenseForm');
  if (!form) return;

  const title = document.getElementById('expenseModalTitle');
  const submitBtn = form.querySelector('button[type="submit"]');

  if (!editId) {
    if (title) title.textContent = 'Add expense';
    if (submitBtn) submitBtn.textContent = 'Save expense';
    form.setAttribute('action', '/expenses');
    return;
  }

  form.setAttribute('action', `/expenses/${editId}`);
  if (title) title.textContent = 'Edit expense';
  if (submitBtn) submitBtn.textContent = 'Update expense';

  const editData = document.getElementById('editData');
  if (editData) {
    const applyValue = (id, value) => {
      const el = document.getElementById(id);
      if (el && value && value !== 'null') {
        el.value = value;
      }
    };
    applyValue('merchant', editData.dataset.merchant);
    applyValue('amount', editData.dataset.amount);
    applyValue('category', editData.dataset.category);
    applyValue('date', editData.dataset.date);
    applyValue('notes', editData.dataset.notes);
    applyValue('tags', editData.dataset.tags);
  }

  openModal('expenseModal');
}

function initExpenseDeleteConfirm() {
  const confirmModal = document.getElementById('confirmModal');
  const confirmBody = document.getElementById('confirmBody');
  const confirmOk = document.getElementById('confirmOk');
  if (!confirmModal || !confirmOk) return;

  let pendingForm = null;

  document.querySelectorAll('.js-expense-delete-form').forEach(form => {
    if (form.dataset.deleteConfirmBound) return;
    form.dataset.deleteConfirmBound = '1';

    form.addEventListener('submit', (e) => {
      if (form.dataset.confirmedSubmit === '1') {
        form.dataset.confirmedSubmit = '0';
        return;
      }

      e.preventDefault();
      pendingForm = form;
      const merchant = form.dataset.merchant || 'this expense';
      if (confirmBody) {
        confirmBody.textContent = `Delete ${merchant}? This cannot be undone.`;
      }
      openModal('confirmModal');
    });
  });

  if (!confirmOk.dataset.confirmBound) {
    confirmOk.dataset.confirmBound = '1';
    confirmOk.addEventListener('click', () => {
      if (!pendingForm) return;
      pendingForm.dataset.confirmedSubmit = '1';
      closeModal('confirmModal');
      pendingForm.submit();
      pendingForm = null;
    });
  }
}

/* ---------- MAIN INIT ---------- */
function initApp(active, title, subtitle) {
  initSidebar();
  initTheme();
  initPrivacy();
  initSearch();
  initExpenseEditFlow();
  initExpenseDeleteConfirm();
  bindAllModals();
  bindAllDropdowns();
  initAddExpenseBtn();
  initSegmentedControls();
  initAiCardRefresh();
  bindAssistButtons();
}

/* ---------- EXPENSE MODAL (JS-only fallback) ---------- */
function openExpenseModal() {
  let m = document.getElementById('expenseModal');
  if (!m) {
    document.body.insertAdjacentHTML('beforeend', expenseModalHTML());
    m = document.getElementById('expenseModal');
    // Rebind after injecting
    bindAllModals();
    bindExpenseModal();
  }
  openModal('expenseModal');
}

function expenseModalHTML() {
  return `
  <div class="modal-backdrop" id="expenseModal" role="dialog" aria-modal="true" aria-hidden="true">
    <div class="modal glass-hi">
      <div class="flex items-center justify-between p-5" style="border-bottom:1px solid var(--border);">
        <div>
          <h3 style="font-size:16px;font-weight:600;letter-spacing:-0.01em;">New expense</h3>
          <div style="font-size:12px;color:var(--text-2);margin-top:2px;">Log a transaction in seconds.</div>
        </div>
        <button class="btn-icon" data-modal-close aria-label="Close">${Icons.close}</button>
      </div>

      <div class="p-5 flex flex-col gap-4">
        <div class="segmented" style="align-self:flex-start;">
          <button class="active" data-type="expense">Expense</button>
          <button data-type="income">Income</button>
          <button data-type="transfer">Transfer</button>
        </div>

        <div>
          <label class="label">Amount</label>
          <div style="position:relative;">
            <span style="position:absolute;left:14px;top:50%;transform:translateY(-50%);color:var(--text-3);font-weight:500;">₹</span>
            <input type="number" class="input" id="modalAmount" placeholder="0.00" style="padding-left:30px;font-size:20px;font-weight:600;" />
          </div>
        </div>

        <div class="grid grid-cols-2 gap-3">
          <div>
            <label class="label">Category</label>
            <select class="input" id="modalCategory">
              <option>🍔 Food &amp; Dining</option>
              <option>🚗 Transport</option>
              <option>🛍️ Shopping</option>
              <option>💡 Bills &amp; Utilities</option>
              <option>🎬 Entertainment</option>
              <option>🏥 Health</option>
            </select>
          </div>
          <div>
            <label class="label">Date</label>
            <input type="date" class="input" id="modalDate" />
          </div>
        </div>

        <div>
          <label class="label">Merchant / Payee</label>
          <input type="text" class="input" id="modalMerchant" placeholder="e.g. Swiggy, Blue Tokai, Amazon" />
        </div>

        <div>
          <label class="label">Notes (optional)</label>
          <textarea class="input" id="modalNotes" rows="2" placeholder="Add a quick note…"></textarea>
        </div>

        <div class="glass p-3 flex items-center justify-between" style="border-radius:10px;">
          <div class="flex items-center gap-2">
            ${Icons.repeat}
            <div>
              <div style="font-size:13px;font-weight:500;">Recurring</div>
              <div style="font-size:11px;color:var(--text-3);">Repeat this expense on a schedule.</div>
            </div>
          </div>
          <label class="switch"><input type="checkbox" id="modalRecurring"/><span class="switch-slider"></span></label>
        </div>

        <div class="flex items-center gap-2">
          <button type="button" class="btn btn-soft flex-1" data-assist="receipt">${Icons.camera}<span>Receipt</span></button>
          <button type="button" class="btn btn-soft flex-1" data-assist="voice">${Icons.mic}<span>Voice</span></button>
        </div>
      </div>

      <div class="flex items-center justify-end gap-2 p-4" style="border-top:1px solid var(--border);background:var(--bg-2);">
        <button type="button" class="btn btn-ghost" data-modal-close>Cancel</button>
        <button type="button" class="btn btn-primary" id="modalSaveBtn">Save expense</button>
      </div>
    </div>
  </div>
  `;
}

function bindExpenseModal() {
  const m = document.getElementById('expenseModal');
  if (!m) return;

  // Segmented type selector
  m.querySelectorAll('.segmented button').forEach(b => {
    b.onclick = () => {
      m.querySelectorAll('.segmented button').forEach(x => x.classList.remove('active'));
      b.classList.add('active');
    };
  });

  // Save button
  const save = document.getElementById('modalSaveBtn');
  if (save) {
    save.onclick = () => {
      const amount = document.getElementById('modalAmount')?.value;
      if (!amount || parseFloat(amount) <= 0) {
        toast('error', 'Please enter a valid amount');
        return;
      }
      m.classList.remove('active');
      toast('success', 'Expense added successfully');
    };
  }

  // Set today's date
  const dateInput = document.getElementById('modalDate');
  if (dateInput && !dateInput.value) {
    dateInput.value = new Date().toISOString().split('T')[0];
  }

  bindAssistButtons();
}

/* ---------- AI + ASSIST WIRING ---------- */
function initAiCardRefresh() {
  const btn = document.getElementById('aiInsightRefreshBtn');
  const headline = document.getElementById('aiInsightHeadline');
  const providerLine = document.getElementById('aiProviderStatus');

  const renderProviders = (providers) => {
    if (!providerLine) return;
    const active = Object.entries(providers || {})
      .filter(([, status]) => status === 'active')
      .map(([name]) => name);
    providerLine.textContent = active.length
      ? `AI providers: ${active.join(', ')}`
      : 'AI providers: local fallback';
  };

  const refreshProviders = async () => {
    if (!providerLine) return;
    try {
      const res = await fetch('/ai/status');
      if (!res.ok) return;
      const data = await res.json();
      renderProviders(data.providers);
    } catch (_) {
      providerLine.textContent = 'AI providers: unavailable';
    }
  };

  if (btn && headline) {
    btn.addEventListener('click', async () => {
      const original = btn.innerHTML;
      btn.disabled = true;
      btn.innerHTML = '<svg style="animation:spin .8s linear infinite" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83"/></svg>';
      try {
        const res = await fetch('/ai/insight', { method: 'POST' });
        if (!res.ok) throw new Error('Insight refresh failed');
        const data = await res.json();
        if (data.insight) {
          headline.textContent = data.insight;
        }
        if (data.providers) {
          renderProviders(data.providers);
        } else {
          await refreshProviders();
        }
        toast('success', 'AI insight refreshed');
      } catch (_) {
        toast('error', 'Could not refresh AI insight');
      } finally {
        btn.disabled = false;
        btn.innerHTML = original;
      }
    });
  }

  if (providerLine) {
    refreshProviders();
    if (!window.__aiProviderPoller) {
      window.__aiProviderPoller = setInterval(refreshProviders, 30000);
    }
  }
}

function bindAssistButtons() {
  document.querySelectorAll('[data-assist="receipt"]').forEach((btn) => {
    if (btn.dataset.assistBound) return;
    btn.dataset.assistBound = '1';
    btn.addEventListener('click', handleReceiptAssist);
  });

  document.querySelectorAll('[data-assist="voice"]').forEach((btn) => {
    if (btn.dataset.assistBound) return;
    btn.dataset.assistBound = '1';
    btn.addEventListener('click', handleVoiceAssist);
  });
}

function applyAssistFields(fields, transcript) {
  if (!fields) return;
  const map = {
    merchant: ['merchant', 'modalMerchant'],
    amount: ['amount', 'modalAmount'],
    category: ['category', 'modalCategory'],
    date: ['date', 'modalDate'],
    notes: ['notes', 'modalNotes']
  };

  Object.entries(map).forEach(([key, ids]) => {
    if (fields[key] == null || fields[key] === '') return;
    ids.forEach((id) => {
      const el = document.getElementById(id);
      if (!el) return;
      if (key === 'category' && el.tagName === 'SELECT') {
        const value = String(fields[key]).toLowerCase();
        const option = Array.from(el.options || []).find(o => String(o.value).toLowerCase() === value);
        if (option) {
          el.value = option.value;
        }
      } else if (key === 'amount') {
        const n = Number(fields[key]);
        if (!Number.isNaN(n) && n > 0) el.value = n;
      } else {
        el.value = fields[key];
      }
    });
  });

  if (transcript) {
    const notesEl = document.getElementById('notes') || document.getElementById('modalNotes');
    if (notesEl && !notesEl.value) {
      notesEl.value = transcript;
    }
  }
}

async function handleReceiptAssist() {
  const input = document.createElement('input');
  input.type = 'file';
  input.accept = 'image/*,application/pdf';
  input.onchange = async () => {
    const file = input.files && input.files[0];
    if (!file) return;
    const form = new FormData();
    form.append('file', file);
    try {
      const res = await fetch('/api/assist/receipt', { method: 'POST', body: form });
      const data = await res.json();
      if (!res.ok || data.success === false) throw new Error(data.message || 'Receipt parsing failed');
      applyAssistFields(data.fields);
      toast('success', 'Receipt processed and fields filled');
    } catch (err) {
      toast('error', err.message || 'Receipt processing failed');
    }
  };
  input.click();
}

async function handleVoiceAssist() {
  const input = document.createElement('input');
  input.type = 'file';
  input.accept = 'audio/*';
  input.onchange = async () => {
    const file = input.files && input.files[0];
    if (!file) return;
    const form = new FormData();
    form.append('file', file);
    try {
      const res = await fetch('/api/assist/voice', { method: 'POST', body: form });
      const data = await res.json();
      if (!res.ok) throw new Error(data.message || 'Voice processing failed');
      applyAssistFields(data.fields, data.transcript);
      if (data.success) {
        toast('success', 'Voice transcription complete');
      } else {
        toast('info', 'Voice uploaded. Fallback transcript applied.');
      }
    } catch (err) {
      toast('error', err.message || 'Voice processing failed');
    }
  };
  input.click();
}

/* ---------- TOASTS ---------- */
function toast(type, message) {
  let host = document.getElementById('toastHost');
  if (!host) {
    host = document.createElement('div');
    host.id = 'toastHost';
    host.className = 'toast-host';
    document.body.appendChild(host);
  }
  const el = document.createElement('div');
  el.className = `toast ${type}`;
  const icons = {
    success: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#34d399" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.1V12a10 10 0 1 1-5.9-9.1"/><path d="M22 4 12 14l-3-3"/></svg>',
    error:   '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#f87171" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M12 8v4M12 16h.01"/></svg>',
    info:    '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#a78bfa" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M12 16v-4M12 8h.01"/></svg>',
  };
  const closeBtn = '<button class="toast-close" aria-label="Close" style="background:none;border:none;cursor:pointer;color:inherit;padding:0;margin-left:auto;line-height:1;font-size:16px;opacity:.6">×</button>';
  el.innerHTML = `${icons[type] || icons.info}<div style="flex:1">${message}</div>${closeBtn}`;
  el.style.cssText = 'display:flex;align-items:center;gap:10px;';
  host.appendChild(el);
  const dismiss = () => {
    el.style.opacity = '0';
    el.style.transform = 'translateY(10px)';
    setTimeout(() => el.remove(), 400);
  };
  const timer = setTimeout(dismiss, 4000);
  el.querySelector('.toast-close').addEventListener('click', () => { clearTimeout(timer); dismiss(); });
}

/* ---------- CHART DEFAULTS ---------- */
function setChartDefaults() {
  if (typeof Chart === 'undefined') return;
  const isDark = document.documentElement.getAttribute('data-theme') !== 'light';
  Chart.defaults.color = isDark ? '#8a8d9c' : '#5f6374';
  Chart.defaults.borderColor = isDark ? 'rgba(255,255,255,0.06)' : 'rgba(17,20,30,0.08)';
  Chart.defaults.font.family = "'Inter', sans-serif";
  Chart.defaults.plugins.tooltip.backgroundColor = isDark ? '#161a26' : '#ffffff';
  Chart.defaults.plugins.tooltip.borderColor = isDark ? 'rgba(255,255,255,0.12)' : 'rgba(17,20,30,0.1)';
  Chart.defaults.plugins.tooltip.borderWidth = 1;
  Chart.defaults.plugins.tooltip.titleColor = isDark ? '#f4f5f8' : '#0c0e14';
  Chart.defaults.plugins.tooltip.bodyColor = isDark ? '#c9cbd6' : '#2a2d3a';
  Chart.defaults.plugins.tooltip.padding = 12;
  Chart.defaults.plugins.tooltip.cornerRadius = 10;
  Chart.defaults.plugins.tooltip.displayColors = false;
  Chart.defaults.plugins.legend.display = false;
}

/* ---------- CATEGORY HELPER ---------- */
function catIcon(cat) {
  const map = {
    food:          { icon: Icons.food,          cls: 'cat-food' },
    transport:     { icon: Icons.transport,     cls: 'cat-transport' },
    shopping:      { icon: Icons.shopping,      cls: 'cat-shopping' },
    bills:         { icon: Icons.bills,         cls: 'cat-bills' },
    entertainment: { icon: Icons.entertainment, cls: 'cat-entertainment' },
    health:        { icon: Icons.health,        cls: 'cat-health' },
    education:     { icon: Icons.education,     cls: 'cat-education' },
    other:         { icon: Icons.other,         cls: 'cat-other' },
  };
  const m = map[cat] || map.other;
  return `<span class="cat-icon ${m.cls}">${m.icon}</span>`;
}

function fmtAmount(n, sign = '') {
  const s = Math.abs(n).toLocaleString('en-IN', { maximumFractionDigits: 0 });
  return `${sign}₹${s}`;
}

/* ---------- FORM LOADING STATES ---------- */
function initFormLoadingStates() {
  document.querySelectorAll('form[method="post"]').forEach(form => {
    if (form.dataset.loadingBound) return;
    form.dataset.loadingBound = '1';
    form.addEventListener('submit', () => {
      const btn = form.querySelector('button[type="submit"]');
      if (btn && !btn.dataset.noLoading) {
        btn.disabled = true;
        btn._origText = btn.textContent;
        btn.innerHTML = '<svg style="animation:spin .8s linear infinite" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83"/></svg> Saving…';
      }
    });
  });
  // Restore on browser back
  window.addEventListener('pageshow', () => {
    document.querySelectorAll('button[type="submit"]').forEach(btn => {
      if (btn._origText) { btn.innerHTML = btn._origText; btn.disabled = false; }
    });
  });
}

/* ---------- DOM READY ---------- */
document.addEventListener('DOMContentLoaded', () => {
  bindAllModals();
  bindAllDropdowns();
  initSegmentedControls();
  initFormLoadingStates();
  if (typeof Chart !== 'undefined') setChartDefaults();
});
