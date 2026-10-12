import { Outlet } from 'react-router'
import { Logo } from '@/components/Logo'

const navigation = [
  { href: '#diety', label: 'Diety' },
  { href: '#jak-to-dziala', label: 'Jak to działa' },
]

export function ShopLayout() {
  return (
    <div className="flex min-h-screen flex-col bg-white text-slate-900 antialiased">
      <header className="sticky top-0 z-10 border-b border-slate-200/70 bg-white/80 backdrop-blur">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
          <Logo />
          <nav
            aria-label="Główna nawigacja"
            className="flex items-center gap-6 text-sm font-medium"
          >
            {navigation.map((item) => (
              <a
                key={item.href}
                href={item.href}
                className="hidden text-slate-600 transition hover:text-brand sm:inline"
              >
                {item.label}
              </a>
            ))}
            <a
              href="#diety"
              className="rounded-full bg-brand px-4 py-2 text-brand-foreground transition hover:bg-brand-strong"
            >
              Wybierz dietę
            </a>
          </nav>
        </div>
      </header>
      <main className="flex-1">
        <Outlet />
      </main>
      <footer className="border-t border-slate-200 bg-slate-50">
        <div className="mx-auto flex max-w-6xl flex-col gap-4 px-6 py-10 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <Logo />
            <p className="mt-2 max-w-sm text-sm text-slate-500">
              Świeże posiłki przygotowywane codziennie i dostarczane prosto pod Twoje drzwi.
            </p>
          </div>
          <p className="text-sm text-slate-500">© {new Date().getFullYear()} Catering</p>
        </div>
      </footer>
    </div>
  )
}
