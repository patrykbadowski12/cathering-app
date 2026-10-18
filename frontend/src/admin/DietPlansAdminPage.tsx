import { CreateDietPlanForm } from '@/features/diet-plans/CreateDietPlanForm'
import { DietPlanTable } from '@/features/diet-plans/DietPlanTable'

export function DietPlansAdminPage() {
  return (
    <>
      <h1 className="text-2xl font-semibold tracking-tight">Diety</h1>
      <p className="mt-1 text-slate-600">Diety, które klienci widzą w sklepie.</p>
      <div className="mt-6 grid items-start gap-6 lg:grid-cols-[1fr_20rem]">
        <DietPlanTable />
        <CreateDietPlanForm />
      </div>
    </>
  )
}
