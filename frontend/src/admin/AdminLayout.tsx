import { ExternalLink } from 'lucide-react'
import { Link, NavLink, Outlet } from 'react-router'
import { Logo } from '@/components/Logo'

const upcomingSections = ['Zamówienia', 'Jadłospis', 'Lista produkcyjna']

// TODO(E4): protect the panel with the owner login.
export function AdminLayout() {
  return (
    <div className="flex min-h-screen flex-col bg-slate-50 text-slate-900 antialiased">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-6xl items-center justify-between gap-4 px-6 py-4">
          <div className="flex items-center gap-3">
            <Logo />
            <span className="rounded-full bg-slate-900 px-2.5 py-0.5 text-xs font-medium text-white">
              Panel
            </span>
          </div>
          <Link
            to="/"
            className="inline-flex items-center gap-1.5 text-sm font-medium text-slate-600 transition hover:text-brand"
          >
            Zobacz sklep
            <ExternalLink aria-hidden className="size-4" />
          </Link>
        </div>
      </header>
      <div className="mx-auto flex w-full max-w-6xl flex-1 flex-col gap-8 px-6 py-8 md:flex-row">
        <nav aria-label="Panel" className="shrink-0 md:w-52">
          <ul className="flex gap-1 overflow-x-auto md:flex-col">
            <li>
              <NavLink
                to="/admin/diety"
                className={({ isActive }) =>
                  `block rounded-lg px-3 py-2 text-sm font-medium whitespace-nowrap transition ${
                    isActive
                      ? 'bg-white text-brand-strong shadow-sm ring-1 ring-slate-200'
                      : 'text-slate-600 hover:bg-white'
                  }`
                }
              >
                Diety
              </NavLink>
            </li>
            {upcomingSections.map((section) => (
              <li key={section}>
                <span
                  aria-disabled="true"
                  className="block cursor-not-allowed rounded-lg px-3 py-2 text-sm whitespace-nowrap text-slate-400"
                >
                  {section} <span className="text-xs">· wkrótce</span>
                </span>
              </li>
            ))}
          </ul>
        </nav>
        <main className="min-w-0 flex-1">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
