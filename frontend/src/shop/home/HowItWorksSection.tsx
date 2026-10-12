import { CalendarDays, Salad, Truck } from 'lucide-react'

const steps = [
  {
    icon: Salad,
    title: 'Wybierz dietę',
    description: 'Dopasuj kaloryczność i rodzaj diety do swoich celów.',
  },
  {
    icon: CalendarDays,
    title: 'Ustal dni dostaw',
    description: 'Zdecyduj, od kiedy i w które dni chcesz otrzymywać posiłki.',
  },
  {
    icon: Truck,
    title: 'Odbieraj posiłki',
    description: 'Codziennie rano świeży zestaw na cały dzień trafia pod Twoje drzwi.',
  },
]

export function HowItWorksSection() {
  return (
    <section id="jak-to-dziala" className="border-t border-slate-200 bg-slate-50">
      <div className="mx-auto max-w-6xl px-6 py-16">
        <h2 className="text-3xl font-semibold tracking-tight">Jak to działa</h2>
        <ol className="mt-10 grid gap-6 md:grid-cols-3">
          {steps.map(({ icon: Icon, title, description }, index) => (
            <li key={title} className="relative rounded-2xl bg-white p-6 ring-1 ring-slate-200">
              <span className="absolute top-6 right-6 text-4xl font-bold text-slate-100 select-none">
                {index + 1}
              </span>
              <span className="inline-flex size-11 items-center justify-center rounded-xl bg-brand-soft text-brand">
                <Icon aria-hidden className="size-5" />
              </span>
              <h3 className="mt-5 text-lg font-semibold">{title}</h3>
              <p className="mt-2 text-slate-600">{description}</p>
            </li>
          ))}
        </ol>
      </div>
    </section>
  )
}
