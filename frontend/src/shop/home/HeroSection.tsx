import { Clock, Leaf, Truck } from 'lucide-react'

const highlights = [
  { icon: Truck, label: 'Dostawa codziennie rano' },
  { icon: Leaf, label: 'Świeże składniki' },
  { icon: Clock, label: 'Zero gotowania' },
]

const sampleDay = [
  { meal: 'Śniadanie', dish: 'Owsianka z owocami sezonowymi', kcal: 380 },
  { meal: 'Obiad', dish: 'Kurczak z kaszą i warzywami', kcal: 620 },
  { meal: 'Kolacja', dish: 'Sałatka z łososiem', kcal: 450 },
]

export function HeroSection() {
  return (
    <section className="relative overflow-hidden bg-brand-soft">
      <div
        aria-hidden
        className="absolute -top-24 -right-24 size-96 rounded-full bg-brand-muted opacity-60 blur-3xl"
      />
      <div className="relative mx-auto grid max-w-6xl items-center gap-12 px-6 py-16 lg:grid-cols-2 lg:py-24">
        <div className="motion-safe:animate-fade-up">
          <span className="inline-flex items-center gap-2 rounded-full bg-white/70 px-3 py-1 text-sm font-medium text-brand-strong">
            <Leaf aria-hidden className="size-4" />
            Catering dietetyczny
          </span>
          <h1 className="mt-6 text-4xl font-bold tracking-tight text-balance sm:text-5xl">
            Zdrowe jedzenie z dostawą pod drzwi
          </h1>
          <p className="mt-5 max-w-xl text-lg text-pretty text-slate-600">
            Wybierz dietę dopasowaną do siebie. Codziennie świeże posiłki, bez gotowania i liczenia
            kalorii.
          </p>
          <div className="mt-8 flex flex-wrap gap-3">
            <a
              href="#diety"
              className="rounded-full bg-brand px-6 py-3 font-medium text-brand-foreground shadow-sm transition hover:bg-brand-strong"
            >
              Zobacz diety
            </a>
            <a
              href="#jak-to-dziala"
              className="rounded-full border border-slate-300 bg-white/60 px-6 py-3 font-medium text-slate-700 transition hover:bg-white"
            >
              Jak to działa
            </a>
          </div>
          <ul className="mt-10 flex flex-wrap gap-x-6 gap-y-3 text-sm text-slate-600">
            {highlights.map(({ icon: Icon, label }) => (
              <li key={label} className="inline-flex items-center gap-2">
                <Icon aria-hidden className="size-4 text-brand" />
                {label}
              </li>
            ))}
          </ul>
        </div>
        <div className="hidden [animation-delay:150ms] motion-safe:animate-fade-up lg:block">
          <div className="mx-auto max-w-sm rotate-2 rounded-2xl bg-white p-6 shadow-xl ring-1 ring-slate-900/5">
            <p className="text-xs font-semibold tracking-wider text-slate-400 uppercase">
              Przykładowy dzień
            </p>
            <ul className="mt-4 divide-y divide-slate-100">
              {sampleDay.map((item) => (
                <li key={item.meal} className="flex items-center justify-between gap-4 py-3">
                  <div>
                    <p className="text-sm font-medium text-brand-strong">{item.meal}</p>
                    <p className="text-slate-700">{item.dish}</p>
                  </div>
                  <span className="shrink-0 text-sm text-slate-400">{item.kcal} kcal</span>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </section>
  )
}
