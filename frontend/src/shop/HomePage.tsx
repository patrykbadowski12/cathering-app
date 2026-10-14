import { DietPlanList } from '@/features/diet-plans/DietPlanList'
import { HeroSection } from '@/shop/home/HeroSection'
import { HowItWorksSection } from '@/shop/home/HowItWorksSection'
import { WeeklyMenuSection } from '@/shop/home/WeeklyMenuSection'

export function HomePage() {
  return (
    <>
      <HeroSection />
      <section id="diety" className="mx-auto max-w-6xl px-6 py-16">
        <div className="max-w-2xl">
          <h2 className="text-3xl font-semibold tracking-tight">Nasze diety</h2>
          <p className="mt-3 text-slate-600">
            Każda dieta to pełen zestaw posiłków na cały dzień, przygotowany przez naszych kucharzy.
          </p>
        </div>
        <div className="mt-10">
          <DietPlanList />
        </div>
      </section>
      <WeeklyMenuSection />
      <HowItWorksSection />
    </>
  )
}
